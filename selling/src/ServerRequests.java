import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class ServerRequests {
	public static final Namespace namespace = Namespace.getNamespace("w",
			"http://www.cs.au.dk/dWebTek/2014");

	// Returns the needed http connection
	private static HttpURLConnection getConnection(URL serverAddress,
			String request) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) serverAddress
				.openConnection();
		connection.setRequestMethod(request);
		connection.setDoOutput(true);
		return connection;
	}

	// Sends an list item request to the cloud server
	public static Document sendListItemsRequest() {
		try {
			URL serverAddress = new URL(
					"http://services.brics.dk/java4/cloud/listItems?shopID=448");
			HttpURLConnection connection = getConnection(serverAddress, "GET");
			connection.connect();

			InputStream in = connection.getInputStream();
			SAXBuilder builder = new SAXBuilder();
			Document response = builder.build(in);
			response.getRootElement().setNamespace(namespace);

			in.close();
			connection.disconnect();

			return response;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	// Sends a create item request to the server and returns the response document
	public static Document SendCreateItemRequest(Document doc) {

		try {
			URL serverAddress = new URL(
					"http://services.brics.dk/java4/cloud/createItem");
			HttpURLConnection connection = getConnection(serverAddress, "POST");
			connection.connect();
			
			XMLOutputter output = new XMLOutputter(Format.getPrettyFormat());
			output.setFormat(output.getFormat().setEncoding("utf-8"));
			output.output(doc, connection.getOutputStream());
			
			InputStream response = connection.getInputStream();
			BufferedInputStream inputstream = new BufferedInputStream(response);
			SAXBuilder builder = new SAXBuilder();
			return builder.build(inputstream);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
	
	// Sends different kinds of requests depending on the url that it receives. This has been made more general since
	// alot of the requests are similar in every way.
	public static int SendRequestWithResponse(Document doc, String url) {
		try {
			URL serverAddress = new URL("http://services.brics.dk/java4/cloud/" + url);
			HttpURLConnection connection = getConnection(serverAddress, "POST");
			connection.connect();
			
			XMLOutputter printer = new XMLOutputter();
			printer.setFormat(printer.getFormat().setEncoding("utf-8"));
			printer.output(doc, connection.getOutputStream());
			int responseCode = connection.getResponseCode();
			System.out.println(responseCode);
			if (responseCode != 200) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						connection.getErrorStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					System.out.println(inputLine);
				}
			}
			connection.disconnect();
			return responseCode;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}
}
