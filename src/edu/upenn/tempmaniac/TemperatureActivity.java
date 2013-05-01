package edu.upenn.tempmaniac;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import edu.upenn.tempmaniac.R;

import edu.upenn.tempServerConnection.TempOperations;

import android.app.Activity;
//import android.app.Notification;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
//import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;
//import android.support.v4.app.*;
public class TemperatureActivity extends Activity {
	private static long appStartTime;
	private static Handler handler;
	private TextView latestTemp;
	private static UpdateReadingThread urt = null;
	private boolean showCe;
	TextView tempLow;
	TextView tempHigh;
	TextView tempAvg;
	Double thresholdDouble = 1000.0;
	NotificationManager mNotificationManager;
	private static Activity thisActivity;
	int numMessages = 0;
	private static String latestTempReading = "N/A";
	
	
	public static String getLatestTempReading() {
		return latestTempReading;
	}
	
	public static void killUpdateThread() {
		if(urt!=null) {
			urt.stopUpdating();
		}
	}
	public static void stopActivity() {
		if(thisActivity != null) {
			thisActivity.finish();
			Log.v("temp", "temp activity stopped");
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.temperature);

		Date now = new Date();
		appStartTime = now.getTime();

		thisActivity = this;
		latestTemp = (TextView)this.findViewById(R.id.tempReading);
		tempLow = (TextView)this.findViewById(R.id.tempLow);
		tempHigh = (TextView)this.findViewById(R.id.tempHigh);
		tempAvg = (TextView)this.findViewById(R.id.tempAvg);
		RadioButton rb = (RadioButton)this.findViewById(R.id.radioCe);
		mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		rb.setChecked(true);
		showCe = true;

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.v("handler:", "displaying temp");
				Bundle b = msg.getData();
				String t = b.getString("temp");
				String errorMsg = b.getString("error");

				if(errorMsg!=null) {
					Log.v("handler", "error received");
					/*
            		// 1. Instantiate an AlertDialog.Builder with its constructor
            		AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
            		// 2. Chain together various setter methods to set the dialog characteristics
            		builder.setMessage("Fail:" + errorMsg)
            		       .setTitle("Server Connection Error");
            		// 3. Get the AlertDialog from create()
            		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	               // User clicked OK button
            	           }
            	       });
            		AlertDialog dialog = builder.create();
            		dialog.show();
					 */
					Toast t1 = Toast.makeText(thisActivity, "temperature connection fail", Toast.LENGTH_SHORT);
					t1.show();
					latestTemp.setText("N/A");
					tempLow.setText("N/A");
					tempHigh.setText("N/A");
					tempAvg.setText("N/A");
					return;
				}else {

					Double tDouble = null;
					try {
						tDouble = Double.parseDouble(t);
					}catch(Exception e) {
						e.printStackTrace();
					}
					if(tDouble!=null && tDouble>thresholdDouble) {
						mNotificationManager =
								(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
						// Sets an ID for the notification, so it can be updated
						int notifyID = 1;

						NotificationCompat.Builder mNotifyBuilder = 
								new NotificationCompat.Builder(thisActivity)
						.setContentTitle("Temp exceeds " + String.format("%.1f", thresholdDouble))
						.setContentText("Temperature is "+ t)
						.setSmallIcon(R.drawable.icon_weather_tab);

						Intent resultIntent = new Intent(thisActivity, TemperatureActivity.class);

						// The stack builder object will contain an artificial back stack for the
						// started Activity.
						// This ensures that navigating backward from the Activity leads out of
						// your application to the Home screen.
						TaskStackBuilder stackBuilder = TaskStackBuilder.create(thisActivity);
						// Adds the back stack for the Intent (but not the Intent itself)
						stackBuilder.addParentStack(TemperatureActivity.class);
						// Adds the Intent that starts the Activity to the top of the stack
						stackBuilder.addNextIntent(resultIntent);
						PendingIntent resultPendingIntent =
								stackBuilder.getPendingIntent(
										0,
										PendingIntent.FLAG_UPDATE_CURRENT
										);

						mNotifyBuilder.setContentIntent(resultPendingIntent);
						// Start of a loop that processes data and then notifies the user

						mNotifyBuilder.setContentText("Temperature is "+ t)
						.setNumber(++numMessages);
						// Because the ID remains unchanged, the existing notification is
						// updated.
						mNotificationManager.notify(
								notifyID,
								mNotifyBuilder.build());
					}

					if(t!=null) {
						String format;
						if(showCe)
							format = "(Ce)";
						else
							format = "(Fa)";
						latestTemp.setText(t+format);       
					}


				}

			}
		};
		urt = new UpdateReadingThread();
		Thread updateThread = new Thread(urt);
		updateThread.start();

	}

	@Override
	public void onStart() {
		super.onStart();
		/*
    	TextView tempLowText = (TextView)this.findViewById(R.id.tempLow);
    	TextView tempHighText = (TextView)this.findViewById(R.id.tempHigh);
    	TextView tempAvgText = (TextView)this.findViewById(R.id.tempAvg);

    	TempOperations t = new TempOperations();
    	t.initialize();
    	//double[] tempData = t.getFakeTempDataOneHour();
    	double[] tempData = t.getTempDataOneHour();
    	if(!showCe) {
    		for(int i=0; i<tempData.length; i++)
    			tempData[i] = convertCeToFa(tempData[i]);
		}
    	tempLowText.setText(String.format("%.2f", tempData[0]));
    	tempHighText.setText(String.format("%.2f", tempData[1]));
    	tempAvgText.setText(String.format("%.2f", tempData[2]));
		 */

		int options = 1;
		UpdateTempDataTask updateTemp = new UpdateTempDataTask();
		updateTemp.execute(options);

	}

	public void setThresholdOnClick(View view) {
		EditText editTextThreshold = (EditText) this.findViewById(R.id.editTextThreshold);
		String threshold = editTextThreshold.getText().toString();
		if(threshold.equals("")) {
			Toast.makeText(this,
					"Please give a number as threshold", Toast.LENGTH_LONG).show();
			return;
		}
		try {
			thresholdDouble = Double.parseDouble(threshold);
		}catch(Exception e) {
			Toast.makeText(this,
					"threshold number invalid", Toast.LENGTH_LONG).show();
			return;
		}

		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.icon_motor_tab)
		.setContentTitle("Temp Threshold")
		.setContentText("Threshold set to "+thresholdDouble);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, TemperatureActivity.class);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(TemperatureActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
						);

		mBuilder.setContentIntent(resultPendingIntent);

		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());
	}

	public void getGraphOnClick( View view ){
		Intent intent = new Intent(this, DisplayGraphActivity.class);
		/*ArrayList<String> temps = new ArrayList<String>();
        temps.add("0");
        temps.add("22.1");
        temps.add("23.4");
        temps.add("25.6");
        temps.add("16.9");
        TempOperations to = new TempOperations();
        //temps = to.fakeGetAllTempOneHour(60);
        temps = to.getAllTempOneHour();
        intent.putStringArrayListExtra("tempList",temps);*/
		startActivity(intent);

	}

	public void buttonFinishOnClick(View view) {
		System.exit(0);
	}

	public void buttonSinceStartOnclick(View view) {
		int options = 4;
		UpdateTempDataTask updateTemp = new UpdateTempDataTask();
		updateTemp.execute(options);
		
	}
	public void disp1HourOnClick(View view) {
		int options = 1;
		UpdateTempDataTask updateTemp = new UpdateTempDataTask();
		updateTemp.execute(options);
	}

	public void disp24HoursOnClick(View view) {
		int options = 2;
		UpdateTempDataTask updateTemp = new UpdateTempDataTask();
		updateTemp.execute(options);
	}

	public void dispAllHistoryOnClick(View view) {
		int options = 3;
		UpdateTempDataTask updateTemp = new UpdateTempDataTask();
		updateTemp.execute(options);
	}

	public void radioButtonOnClick(View view) {
		boolean checked = ((RadioButton) view).isChecked();
		TextView tempLowText = (TextView)this.findViewById(R.id.tempLow);
		TextView tempHighText = (TextView)this.findViewById(R.id.tempHigh);
		TextView tempAvgText = (TextView)this.findViewById(R.id.tempAvg);
		try {
			// Check which radio button was clicked
			switch(view.getId()) {
			case R.id.radioCe:
				if (checked && !showCe) {
					showCe = true;

					String low = tempLowText.getText().toString();
					Double lowDouble = convertFaToCe(Double.parseDouble(low));
					tempLowText.setText(String.format("%.2f", lowDouble));

					String high = tempHighText.getText().toString();
					Double highDouble = convertFaToCe(Double.parseDouble(high));
					tempHighText.setText(String.format("%.2f", highDouble));

					String avg = tempAvgText.getText().toString();
					Double avgDouble = convertFaToCe(Double.parseDouble(avg));
					tempAvgText.setText(String.format("%.2f", avgDouble));
				}
				break;
			case R.id.radioFa:
				if (checked && showCe) {
					showCe = false;

					String low = tempLowText.getText().toString();
					Double lowDouble = convertCeToFa(Double.parseDouble(low));
					tempLowText.setText(String.format("%.2f", lowDouble));

					String high = tempHighText.getText().toString();
					Double highDouble = convertCeToFa(Double.parseDouble(high));
					tempHighText.setText(String.format("%.2f", highDouble));

					String avg = tempAvgText.getText().toString();
					Double avgDouble = convertCeToFa(Double.parseDouble(avg));
					tempAvgText.setText(String.format("%.2f", avgDouble));
				}
				break;
			}
		}catch(NumberFormatException e) {
			e.printStackTrace();
			Toast t1 = Toast.makeText(thisActivity, "Temperature currently not available ", Toast.LENGTH_SHORT);
			t1.show();
		}
	}

	public void stopUpdateOnClick(View view) {
		if(urt!=null) {
			urt.stopUpdating(); 
			urt = null;
			latestTemp.setText("N/A");
		}else {
			Toast t = Toast.makeText(thisActivity, "update stopped already", Toast.LENGTH_LONG);
			t.show();
		}
	}

	public void resumeUpdateOnClick(View view) {
		if(urt == null) {
			urt = new UpdateReadingThread();
			Thread updateThread = new Thread(urt);
			updateThread.start();
		}else {
			Toast t = Toast.makeText(thisActivity, "updating...already", Toast.LENGTH_LONG);
			t.show();
		}
	}

	private double convertCeToFa(double ce) {
		return ce*1.8 + 32;
	}

	private double convertFaToCe(double fa) {
		return (fa - 32)/1.8;
	}

	class UpdateReadingThread extends Thread{

		public void stopUpdating() {
			stopUpdateReading = true;
		}

		boolean stopUpdateReading = false;

		public void run() {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			TempOperations temp = new TempOperations();	
			while(!stopUpdateReading) {
				Log.v("temp", "updating temp in BG");
				//Double reading = temp.getFakeLatestReading(true);
				Double reading = temp.getLatestReading(true);
				if(reading == null) {
					Log.v("error", "Lost connection with server when get latest reading");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("error", "temp server connection fail");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);

				}else {
					if(!showCe) {
						reading = convertCeToFa(reading);
					}
					Log.v("latest reading", reading.toString());

					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("temp", String.format("%.2f", reading));
					latestTempReading = String.format("%.2f", reading);
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);

				}

				try {
					Thread.sleep(1000);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			Log.v("latestTemp", "updating thread ends");

		}

	}


	class UpdateTempDataTask extends AsyncTask<Integer, Integer, String[]>{

		protected UpdateTempDataTask() {
			super();
		}

		@Override
		protected String[] doInBackground(Integer... params) {
			TempOperations temp = new TempOperations();
			boolean r = temp.initialize();
			if(!r) {
				Log.v("error", "Lost connection with server when updating temp data");
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("error", "Temp connection fail");
				msg.setData(b);
				// send message to the handler with the current message handler
				handler.sendMessage(msg);
				return null;
			}
			double[] data;
			switch(params[0]) {
			case 1:
				Log.v("tempData", "retrieving tempdata 1 hour");
				//data = temp.getFakeTempDataOneHour();
				data = temp.getTempDataOneHour();
				break;
			case 2:
				Log.v("tempData", "retrieving tempdata 24 hours");
				//data = temp.getFakeTempDataOneHour();
				data = temp.getTempData24Hours();
				break;
			case 3:
				Log.v("tempData", "retrieving tempdata all");
				data = temp.getTempDataOneHour();
				//data = temp.get
				break;
			case 4:
				Log.v("tempData", "retrieving tempdata since");
				
				data = temp.getReadingSinceData(appStartTime);
				for(int i=0; i<data.length; i++) {
					Log.v("tempData" , "tempDataSince = "+ data[i]);
				}
				break;
			default:
				Log.v("tempData", "retrieving tempdata 1 hour(default)");
				//data = temp.getFakeTempDataOneHour();
				data = temp.getTempDataOneHour();

				break;
			}
			//Log.v("latest tempData", Double.toString(data[0]));
			if(!showCe) {
				for(int i=0; i<data.length; i++) {
					data[i] = convertCeToFa(data[i]); 
				}
			}
			Log.v("tempData", "updating tempLow in BG");
			String[] result = new String[] {String.format("%.2f", data[0]), String.format("%.2f", data[1]),
					String.format("%.2f", data[2])}; 
			/*
    		Message msg = new Message();
			Bundle b = new Bundle();
		    b.putString("tempLow", String.format("%.2f", data[0]));
		    b.putString("tempHigh", String.format("%.2f", data[1]));
		    b.putString("tempAvg", String.format("%.2f", data[2]));

		    msg.setData(b);
		      // send message to the handler with the current message handler
		    handler.sendMessage(msg);
			 */
			return result;
		}

		@Override
		protected void onPostExecute (String[] result) {
			if(result == null) {
				Log.v("temp", "fail to retrieve temp data");
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("error", "Temp fail to retrieve temp data");
				msg.setData(b);
				// send message to the handler with the current message handler
				handler.sendMessage(msg);
				return;
			}

			String low = result[0];
			String high = result[1];
			String avg = result[2];

			if(low!=null)
				tempLow.setText(low);
			if(high!=null)
				tempHigh.setText(high);
			if(avg!=null)
				tempAvg.setText(avg);
		}

	}

}



