package edu.upenn.tempmaniac;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import edu.upenn.tempmaniac.R;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

public class AccountActivity extends Activity{

	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");

	private static GraphUser user;
	public static void setUser(GraphUser u) {
		Log.v("account", "user set to" + user);
		user = u;
	}
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

	public void shareTempOnClick(View view){
		Log.v("account", "share click");
		postStatusUpdate();
	}
	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null && session.getPermissions().contains("publish_actions");
	}

	private void postStatusUpdate() {
		Session session = Session.getActiveSession();
		if(user!=null) {
			if (hasPublishPermission()) {
				final String message = "Hello " + user.getName();
				Log.v("account", "going to post for user:" + user.getName());
				Request request = Request
						.newStatusUpdateRequest(Session.getActiveSession(), message, new Request.Callback() {

							public void onCompleted(Response response) {
								showPublishResult(message, response.getGraphObject(), response.getError());
							}
						});
				request.executeAsync();
				Log.v("account", "new post submitted");
			} else {
				session.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSIONS));
				Log.v("account", "get user post permission");
			}
		}else {
			Log.v("account", "no user found");
		}
	}

	private void showPublishResult(String message, GraphObject result, FacebookRequestError error) {
		String title = null;
		String alertMessage = null;
		if (error == null) {
			title = "success";
			alertMessage = "post complete";
		} else {
			title = "fail to post";
			alertMessage = error.getErrorMessage();
		}

		new AlertDialog.Builder(this)
		.setTitle(title)
		.setMessage(alertMessage)
		.setPositiveButton("OK", null)
		.show();
	}

}
