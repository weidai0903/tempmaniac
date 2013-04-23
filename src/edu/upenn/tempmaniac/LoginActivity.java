package edu.upenn.tempmaniac;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.androidtablayout.R;
import com.facebook.*;
import com.facebook.model.*;
import com.facebook.widget.LoginButton;

import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

public class LoginActivity extends Activity {

    private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";
    private static Intent mainIntent = null;
    private TextView textInstructionsOrLink;
    private Button buttonLoginLogout;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();

    
    public static void invalidateMainIntent() {
    	mainIntent = null;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        buttonLoginLogout = (Button)findViewById(R.id.buttonLoginLogout);
        textInstructionsOrLink = (TextView)findViewById(R.id.instructionsOrLink);

        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        if (session == null) {
        	Log.v("login", "session is null");
            if (savedInstanceState != null) {
            	Log.v("login", "savedInstanceState is not null");
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
            	Log.v("login", "create new session");
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }else {
        	Log.v("login", "session is not null");
        }

        updateView();
    }

    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    private void updateView() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
        	Log.v("Login", "success, welcome");
            textInstructionsOrLink.setText("Welcome to the world of TempManiac!");
            buttonLoginLogout.setText("Logout");
            buttonLoginLogout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogout(); }
            });
            
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                // callback after Graph API response with user object
                
                public void onCompleted(GraphUser user, Response response) {
                  if (user != null) {
                	  
                      TextView welcome = (TextView) findViewById(R.id.welcome);
                      welcome.setText("Hello " + user.getName() + "!");
                	  Log.v("login", "Success! user:" + user.getName());
                	  if(mainIntent == null) {
                		  mainIntent = new Intent(LoginActivity.this, MainActivityNew.class);
                		  LoginActivity.this.startActivity(mainIntent);
                		  Log.v("login", "main activity started");
                	  }else {
                		  //LoginActivity.this.startActivity(mainIntent);
                		  Log.v("login", "old main activity started");
                	  }
                  }
                }
                
              });
            
        } else {
        	Log.v("Login", "Fail");
            textInstructionsOrLink.setText("please login");
            buttonLoginLogout.setText("Login");
            buttonLoginLogout.setOnClickListener(new OnClickListener() {
                public void onClick(View view) { onClickLogin(); }
            });
        }
    }

    private void onClickLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    private void onClickLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
        TextView welcome = (TextView) findViewById(R.id.welcome);
        welcome.setText("you are logged out");
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        
        public void call(Session session, SessionState state, Exception exception) {
            updateView();
        }
    }
}
