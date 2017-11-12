package nl.kolkos.cryptoManager;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ApiRequestHandler {
	
	/**
	 * This method gets the current value of the coin
	 * @param coinName the name of the coin
	 * @param currency currency to calculate the value
	 * @return a json object containing the result of the api request
	 * @throws Exception a api error
	 */
	public JSONObject currentCoinValueApiRequest(String cmdId, String currency) throws Exception {
		// build the url for the api request
		String url = "https://api.coinmarketcap.com/v1/ticker/" + cmdId + "/?convert=" + currency;
		
		JSONArray jsonArray = this.jsonArrayRequest(url);
		
		
		JSONObject jsonObject = jsonArray.getJSONObject(0);
		return jsonObject;
	}
	
	/**
	 * General method to handle the API requests
	 * @param the URL of the API request
	 * @return a JSONObject containing the response of the server
	 * @throws Exception
	 */
	private JSONObject doAPIRequest(String urlString) throws Exception {

		
		URL url = new URL(urlString);
		
		// read from the URL
	    Scanner scan = new Scanner(url.openStream());
	    String str = new String();
	    while (scan.hasNext())
	        str += scan.nextLine();
	    scan.close();
	    
	    // build a JSON object
	    JSONObject jsonObject = new JSONObject(str);

	    
	    return jsonObject;
	}
	
	private JSONArray jsonArrayRequest(String urlString) throws IOException, JSONException {
		URL url = new URL(urlString);
		
		// read from the URL
	    Scanner scan = new Scanner(url.openStream());
	    String str = new String();
	    while (scan.hasNext())
	        str += scan.nextLine();
	    scan.close();
	    
	    JSONArray jsonArray = new JSONArray(str);
	    
	    return jsonArray;
	}
	
	public JSONArray getAllCMCCoins() throws IOException, JSONException {
		String url = "https://api.coinmarketcap.com/v1/ticker/";
		JSONArray result = this.jsonArrayRequest(url);
		return result;
		
	}
	
	
}
