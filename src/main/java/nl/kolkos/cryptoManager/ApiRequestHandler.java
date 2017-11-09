package nl.kolkos.cryptoManager;

import java.net.URL;
import java.util.Scanner;

import org.json.JSONObject;

public class ApiRequestHandler {
	/**
	 * This method gets the current wallet information from the api
	 * @param coinName the name of the requested coin
	 * @param walletAddress the address of the wallet
	 * @return a json object containing the results of the api request
	 * @throws Exception api error
	 */
	public JSONObject walletInfoApiRequest(String coinName, String walletAddress) throws Exception {
		// build the url for the request
		String url = "https://api.blockcypher.com/v1/" + coinName.toLowerCase() + "/main/addrs/" + walletAddress + "/balance";
		JSONObject json = this.doAPIRequest(url);
		return json;
	}
	
	/**
	 * This method gets the current value of the coin
	 * @param coinName the name of the coin
	 * @param currency currency to calculate the value
	 * @return a json object containing the result of the api request
	 * @throws Exception a api error
	 */
	public JSONObject currentCoinValueApiRequest(String coinName, String currency) throws Exception {
		// build the url for the api request
		String url = "https://www.bitstamp.net/api/v2/ticker_hour/" + coinName.toLowerCase() + currency.toLowerCase() + "/";
		JSONObject json = this.doAPIRequest(url);
		return json;
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
}
