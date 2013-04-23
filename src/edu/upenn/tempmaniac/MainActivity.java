package edu.upenn.tempmaniac;

import com.example.androidtablayout.R;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	
	private static Activity thisActivity;
	
	public void stopActivity() {
		thisActivity.finish();
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LoginActivity.invalidateMainIntent();
        thisActivity = this;
        setContentView(R.layout.main);
        
        TabHost tabHost = getTabHost();
        
        // Tab for Temperature
        TabSpec tempspec = tabHost.newTabSpec("Temperature");
        tempspec.setIndicator("Temperature", getResources().getDrawable(R.drawable.icon_temp_tab));
        Intent tempIntent = new Intent(this, TemperatureActivity.class);
        tempspec.setContent(tempIntent);
        
        // Tab for Device
        TabSpec deviceSpec = tabHost.newTabSpec("Device");
        // setting Title and Icon for the Tab
        deviceSpec.setIndicator("Device", getResources().getDrawable(R.drawable.icon_motor_tab));
        Intent deviceIntent = new Intent(this, DeviceActivity.class);
        deviceSpec.setContent(deviceIntent);
        
        // Tab for Weather
        TabSpec weatherSpec = tabHost.newTabSpec("Weather");
        weatherSpec.setIndicator("Weather", getResources().getDrawable(R.drawable.icon_weather_tab));
        Intent weatherIntent = new Intent(this, WeatherActivity.class);
        weatherSpec.setContent(weatherIntent);
        
        // Tab for Videos
        TabSpec accountSpec = tabHost.newTabSpec("Account");
        accountSpec.setIndicator("Account", getResources().getDrawable(R.drawable.icon_facebook_tab));
        Intent accountIntent = new Intent(this, AccountActivity.class);
        
        accountSpec.setContent(accountIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(tempspec); 
        tabHost.addTab(deviceSpec); 
        tabHost.addTab(weatherSpec); 
        tabHost.addTab(accountSpec);
    }
}