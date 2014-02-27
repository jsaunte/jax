package dk.cs.dwebtek;

import org.jdom2.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.omg.CORBA.ServerRequest;

import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("shop")
public class ShopService {
	/**
	 * Out Servlet session. We will need this for the shopping basket
	 */
	@Context
	HttpSession session;

	/**
	 * Make the price increase per request (for the sake of example)
	 */

	@GET
	@Path("items")
	public String getItems() {
		// You should get the items from the cloud server.
		// In the template we just construct some simple data as an array of
		// objects
		JSONArray array = new JSONArray();
		Document doc = ServerRequests.sendListItemsRequest();
		for (Element e : doc.getRootElement().getChildren()) {
			JSONObject j = new JSONObject();
			j.put("id", e.getChild("itemID", ServerRequests.namespace)
					.getValue());
			j.put("name", e.getChild("itemName", ServerRequests.namespace)
					.getValue());
			j.put("description",
					e.getChild("itemDescription", ServerRequests.namespace)
							.getValue());
			j.put("stock", e.getChild("itemStock", ServerRequests.namespace)
					.getValue());
			j.put("price", e.getChild("itemPrice", ServerRequests.namespace)
					.getValue());
			array.put(j);
		}
		System.out.println("hej");

		// You can create a MessageBodyWriter so you don't have to call
		// toString() every time
		return array.toString();
	}

	@POST
	@Path("login")
	public boolean Login(@FormParam("user")String user, @FormParam("pass")String pass) {
		
		Document doc = new Document();
		Element root = new Element("login", ServerRequests.namespace);

		Element name = new Element("customerName", ServerRequests.namespace);
		name.setText(user);

		Element pWord = new Element("customerPass", ServerRequests.namespace);
		pWord.setText(pass);

		root.addContent(name);
		root.addContent(pWord);
		doc.setRootElement(root);
		boolean loggedin = ServerRequests.SendRequestWithResponse(doc, "login") == 200;
		session.setAttribute("loggedin", loggedin);
		return loggedin;

	}
}
