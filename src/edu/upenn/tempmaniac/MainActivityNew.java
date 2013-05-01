package edu.upenn.tempmaniac;

import edu.upenn.tempmaniac.R;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class MainActivityNew extends TabActivity {
    /** Called when the activity is first created. */
	
	private static Activity thisActivity;
	
	public static void stopActivity() {
		thisActivity.finish();
	}
	
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
        
        // Tab for Device --> TODO: Motor
        TabSpec motorSpec = tabHost.newTabSpec("Motor");
        // setting Title and Icon for the Tab
        motorSpec.setIndicator("Motor", getResources().getDrawable(R.drawable.icon_motor_tab));
        Intent motorIntent = new Intent(this, MotorActivity.class);
        motorSpec.setContent(motorIntent);
        
        // Tab for Device2 --> TODO:icon_device_tab-->display
        TabSpec displaySpec = tabHost.newTabSpec("Display");
        // setting Title and Icon for the Tab
        displaySpec.setIndicator("Display", getResources().getDrawable(R.drawable.icon_display_tab));
        Intent displayIntent = new Intent(this, DisplayActivity.class);
        displaySpec.setContent(displayIntent);
        
        // Tab for Weather
        TabSpec weatherSpec = tabHost.newTabSpec("Weather");
        weatherSpec.setIndicator("Weather", getResources().getDrawable(R.drawable.icon_weather_tab));
        Intent weatherIntent = new Intent(this, WeatherActivity.class);
        weatherSpec.setContent(weatherIntent);
        
        // Tab for Videos
        TabSpec accountSpec = tabHost.newTabSpec("Account");
        accountSpec.setIndicator("Account", getResources().getDrawable(R.drawable.icon_facebook_tab));
        Intent accountIntent = new Intent(this, AccountActivityNew.class);
        accountSpec.setContent(accountIntent);
        
        // Adding all TabSpec to TabHost
        tabHost.addTab(tempspec); 
        tabHost.addTab(motorSpec);
        
        tabHost.addTab(displaySpec); 
        tabHost.addTab(weatherSpec); 
        tabHost.addTab(accountSpec);
    }
}