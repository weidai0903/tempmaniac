package edu.upenn.tempmaniac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.androidtablayout.R;
import com.facebook.Session;

public class AccountActivity extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);
    }
	
	public void buttonLogoutOnClick(View view) {
		logout();
	}
	
	private void logout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
        MotorActivity.killUpdateThread();
        TemperatureActivity.killUpdateThread();
        TemperatureActivity.stopActivity();
        MotorActivity.stopActivity();
        //MainActivityNew.stopActivity();
        
        Log.v("account", "temp and motor activity stopped");
        Intent loginIntent = new Intent(this, LoginActivity.class);
        this.startActivity(loginIntent);
        Log.v("account", "logout complete");
	}

}
