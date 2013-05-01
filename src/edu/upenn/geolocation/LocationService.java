package edu.upenn.geolocation; 
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import edu.upenn.tempmaniac.R;

public class LocationService{

	private Location currentLoc;
	String currentLocString = "NULL";
	GPSTracker mGPS;
	private Context context;
	private TextView tvCity;
	
	public LocationService (Context context) {
		this.context = context;
		
	}
	public void getCity(TextView tv){
		tvCity = tv;
		double mLat = 0;
		double mLong = 0;
		mGPS = new GPSTracker(context);
		if(mGPS.canGetLocation ){

			mLat=mGPS.getLatitude();
			mLong=mGPS.getLongitude();
			currentLoc = new Location( "TempManiac");
			currentLoc.setLatitude(mLat);
			currentLoc.setLongitude(mLong);

			Log.v("gps handler:", "latitude: " + mLat +", Longitude: " + mLong );
			new ReverseGeocodingTask( context ).execute(currentLoc);

		}else{
			Log.v("gps handler:", "no response" );
		}
	}

	private class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> {
		Context mContext;

		public ReverseGeocodingTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected Void doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			Log.v("location:", "geocoder present:" + Geocoder.isPresent());
			Location loc = params[0];
			List<Address> addresses = null;
			try {
				// Call the synchronous getFromLocation() method by passing in the lat/long values.

				addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
				Log.v("location", "addresses:" + addresses);
			} catch (IOException e) {
				e.printStackTrace();
				// Update UI field with the exception.
				//Message.obtain(mHandler, UPDATE_ADDRESS, e.toString()).sendToTarget();
			}
			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				// Format the first line of address (if available), city, and country name.
				String addressText = String.format("%s, %s, %s",
						address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
								address.getLocality(),
								address.getCountryName());
				currentLocString = address.getLocality();
				Log.v("gps final:", "city:" + currentLocString );
				// Update the UI via a message handler.
				//Message.obtain(mHandler, UPDATE_ADDRESS, addressText).sendToTarget();
			}
			else{
				currentLocString = null;
				Log.v("location:", "fail to get address" );
			}
			return null;
		}
		
		@Override
		 protected void onPostExecute(Void result) {
			if(currentLocString == null) {
				tvCity.setText("N/A");
				Toast t = Toast.makeText(context, "Oops!Location service currently unavailable", Toast.LENGTH_SHORT);
				t.show();
			}else {
				tvCity.setText(currentLocString);
			}
		}
	}

}




