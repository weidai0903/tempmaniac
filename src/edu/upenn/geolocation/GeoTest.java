package edu.upenn.geolocation;

import java.net.URL;

import org.json.JSONException;

public class GeoTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/*Utils.getIPAddress(true); // IPv4
		RestfulCall rc = new RestfulCall(ipAddress);
		//			try {
		//				System.out.println(rc.getResult());
		//			} catch (Exception e) {
		//				e.printStackTrace();
		//			}

		URL	restfulAddress = new URL(rc.getResult());
		GeoLocation gl = new GeoLocation(restfulAddress);
		String location = null;
		try {
			location = gl.getLocation().getJSONObject("ipinfo").getJSONObject("Location").getString("latitude").toString() +
					"," + gl.getLocation().getJSONObject("ipinfo").getJSONObject("Location").getString("longitude").toString();
		}  catch (JSONException j) {
			location = "39.96405,-75.19718";	// set location to philly if IP undetected
		}
		*/
	}

}
