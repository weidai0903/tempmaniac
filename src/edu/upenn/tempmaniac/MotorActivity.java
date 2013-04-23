package edu.upenn.tempmaniac;

import com.example.androidtablayout.R;

import edu.upenn.tempServerConnection.Led;
import edu.upenn.tempServerConnection.Motor;
import edu.upenn.tempServerConnection.Sensor;
import edu.upenn.tempServerConnection.TempOperations;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class MotorActivity extends Activity {
	private TextView tvMotorSpeed;
	private static updateMotorSpeed ums = null;
	private static Activity thisActivity;
	private static Handler handler;
	private boolean sendDisplayCe = true;
	private boolean ledRed = true;
	private boolean displayCe;
	private String ledText = "";
	
	
	public static void killUpdateThread() {
		if(ums!=null) {
			ums.stopUpdate();
		}
	}
	
	public static void stopActivity() {
		if(thisActivity!=null) {
			thisActivity.finish();
			Log.v("motor", "motor activity stopped");

		}
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.motor);
		thisActivity = this;
		tvMotorSpeed = (TextView)this.findViewById(R.id.textViewMotorSpeed);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.v("handler:", "showing motor speed");
				Bundle b = msg.getData();
				String motorSpeed = b.getString("motorSpeed");
				String failure = b.getString("fail");
				if(failure!=null) {
					Log.v("handler", "error received");
					Toast t = Toast.makeText(thisActivity, "Fail:"+failure, Toast.LENGTH_SHORT);
					t.show();
					tvMotorSpeed.setText("N/A");
				}else {
					tvMotorSpeed.setText(motorSpeed);        
				}
			}

		};

		ums = new updateMotorSpeed();
		Thread showMotorSpeed = new Thread(ums);
		showMotorSpeed.start();
	}


	public void buttonLedShowTempOnClick(View view) {

		TalkToServer connect = new TalkToServer();
		connect.execute(13);

	}
	public void showOnBoardOnClick(View view) {
		CheckBox cb = (CheckBox) view;
		boolean checked = cb.isChecked();
		TalkToServer connect = new TalkToServer();
		if(checked) {
			//cb.setChecked(true);
			connect.execute(6);
		}else {
			connect.execute(7);
		}
	}

	public void showOnLedOnClick(View view) {
		CheckBox cb = (CheckBox) view;
		boolean checked = cb.isChecked();
		TalkToServer connect = new TalkToServer();
		if(checked) {
			//cb.setChecked(true);
			connect.execute(11);
		}else {
			connect.execute(12);
		}
	}

	public void checkBoxAutoSpeedOnClick(View view) {

		CheckBox cb = (CheckBox) view;
		boolean checked = cb.isChecked();
		TalkToServer connect = new TalkToServer();
		if(checked) {
			//cb.setChecked(true);
			connect.execute(8);
		}else {
			connect.execute(9);
		}
	}

	public void buttonDeviceFinishOnClick(View view) {
		System.exit(0);
	}

	public void tempFormatSubmitOnClick(View view) {

		if(sendDisplayCe) {
			TalkToServer connect = new TalkToServer();
			connect.execute(4);
		}else {
			TalkToServer connect = new TalkToServer();
			connect.execute(5);
		}
	}

	public void deviceRadioOnClick (View view) {
		RadioButton rb = (RadioButton) view;
		boolean checked = rb.isChecked();
		switch(view.getId()) {
		case R.id.radioDeviceCe:
			if(checked) {
				sendDisplayCe = true;
			}
			break;
		case R.id.radioDeviceFa:
			if(checked) {
				sendDisplayCe = false;
			}
			break;
		}
	}
	public void radioLedColorOnClick (View view) {
		RadioButton rb = (RadioButton) view;
		boolean checked = rb.isChecked();
		switch(view.getId()) {
		case R.id.radioLedRed:
			if(checked) {
				ledRed = true;
			}
			break;
		case R.id.radioLedGreen:
			if(checked) {
				ledRed = false;
			}
			break;
		}
	}

	public void sendToLEDOnClick (View view) {
		EditText editTextLedText = (EditText) this.findViewById(R.id.editTextLedText);
		ledText = editTextLedText.getText().toString();
		if(ledText == null || ledText.equals("")) {
			Toast t = Toast.makeText(thisActivity, "please give input in Text", Toast.LENGTH_SHORT);
			t.show();
		}else {
			TalkToServer connect = new TalkToServer();
			connect.execute(10);
		}
	}


	public void motorSpeeddownOnClick(View view) {
		TalkToServer connect = new TalkToServer();
		connect.execute(0);
	}

	public void motorSpeedupOnClick(View view){
		TalkToServer connect = new TalkToServer();
		connect.execute(1);
	}

	public void motorOffOnClick(View view) {
		TalkToServer connect = new TalkToServer();
		connect.execute(2);
	}

	public void motorOnOnClick(View view) {
		TalkToServer connect = new TalkToServer();
		connect.execute(3);
	}

	class updateMotorSpeed extends Thread{

		boolean stop = false;
		public void stopUpdate() {
			stop = true;
		}
		public void run() {

			while (!stop) {
				Motor motor = new Motor();
				//String speed = motor.fakeGetCurrentSpeed();
				String speed = motor.getCurrrentSpeed();
				Message msg = new Message();
				Bundle b = new Bundle();

				if(speed == null) {
					b.putString("fail", "Device server connection fail");
				}else {
					b.putString("motorSpeed", speed);
				}
				msg.setData(b);
				// send message to the handler with the current message handler
				handler.sendMessage(msg);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Log.v("thread", "motor speed update ends");

		}
	}

	public void stopUpdateSpeedOnClick(View view) {
		if(ums != null) {
			ums.stopUpdate();
			ums = null;
			tvMotorSpeed.setText("N/A");
		}else {
			Toast t = Toast.makeText(thisActivity, "stopped already", Toast.LENGTH_SHORT);
			t.show();
		}
			
	}

	public void resumeUpdateSpeedOnClick(View view) {
		if(ums == null) {
			ums = new updateMotorSpeed();
			Thread showMotorSpeed = new Thread(ums);
			showMotorSpeed.start();
		}else {
			Toast t = Toast.makeText(thisActivity, "updating already", Toast.LENGTH_SHORT);
			t.show();
		}
	}

	class TalkToServer extends AsyncTask<Integer, Integer, Long>{

		@Override
		protected Long doInBackground(Integer... params) {
			Motor motor = new Motor();
			Sensor sensor = new Sensor();
			Led led = new Led();
			boolean success;
			switch(params[0]) {
			case 0:
				Log.v("server", "speeddown motor");
				//speed down motor by 10
				//success = motor.fakeChangeSpeedMotor(false);
				success = motor.speeddownMotor();
				if(!success) {
					Log.v("server", "speeddown fail");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "motor speeddown");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}
				break;
			case 1:
				Log.v("server", "speedup motor");
				//speed up motor by 10
				//success = motor.fakeChangeSpeedMotor(false);
				success = motor.speedupMotor();
				if(!success) {
					Log.v("server", "speedup fail");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "motor speedup");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}
				break;
			case 2:
				Log.v("server", "turn off motor");
				//turn off motor
				//success = motor.fakeChangeSpeedMotor(false);
				success = motor.shutdownMotor(0);
				if(!success) {
					Log.v("server", "shutdown motor fail");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "motor turn off");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}
				break;
			case 3:
				Log.v("server", "turn on motor");
				//turn on motor
				//success = motor.fakeChangeSpeedMotor(false);
				success = motor.maxSpeedMotor();
				if(!success) {
					Log.v("server", "maximize motor fail");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "motor maximize speed");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}
				break;
			case 4:
				Log.v("server", "sensor show ce");
				//turn on motor
				//success = sensor.fakeSetDisplayOnSensor(false);
				success = sensor.setDisplayOnSensor(true);
				if(!success) {
					Log.v("server", "sensor show celsius");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "sensor show celsius");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}else {
					displayCe = true;
				}
				break;
			case 5:
				Log.v("server", "sensor show fa");
				//turn on motor
				//success = sensor.fakeSetDisplayOnSensor(false);
				success = sensor.setDisplayOnSensor(false);
				if(!success) {
					Log.v("server", "sensor show fahrenheit");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "sensor show fahrenheit");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}else {
					displayCe = false;
				}
				break;
			case 6:
				Log.v("server", "show temp on board");
				//show temp on board
				//success = sensor.fakeShowHideTempOnSensor(false);
				success = sensor.showTempOnSensor();
				if(!success) {
					Log.v("server", "show temp on board fail");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "show temp on board");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}
				break;
			case 7:
				Log.v("server", "hide temp on board");
				//show temp on board
				//success = sensor.fakeShowHideTempOnSensor(false);
				success = sensor.hideTempOnSensor();
				if(!success) {
					Log.v("server", "hide temp on board fail");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "hide temp on board");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}
				break;
			case 8:
				Log.v("server", "auto motor speed on");
				//show temp on board
				//success = sensor.fakeShowHideTempOnSensor(false);
				success = motor.autoSpeedOn(true);
				if(!success) {
					Log.v("server", "auto motor speed on fail");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "auto motor speed on");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}
				break;

			case 9:
				Log.v("server", "auto motor speed off");
				//show temp on board
				//success = sensor.fakeShowHideTempOnSensor(false);
				success = motor.autoSpeedOn(false);
				if(!success) {
					Log.v("server", "auto motor speed off fail");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "auto motor speed off");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}
				break;
			case 10:
				Log.v("led", "led color and text");

				success = led.setColorOnLed(ledRed);
				int ledNum;
				try {
					ledNum = Integer.parseInt(ledText);
					if(ledNum < 0 || ledNum >99) {
						throw new UnsupportedOperationException();
					}
					success = led.showNumOnLed(ledText);
					Log.v("LED", "ledNum showed:" + ledNum);
				}catch(Exception e) {
					if(ledText.equals("#")) {
						success = led.showSnakeOnLed();
					}else if(ledText.equals("%")) {
						success = led.showLoveOnLed();
					}else {
						ledText = ledText.replace(" ", "");
						ledText = ledText.replace("#", "");
						ledText = ledText.replace("/", "");
						ledText = ledText.replace("%", "");
						success = led.showTextOnLed(ledText);
					}
					Log.v("LED", "ledText showed:" + ledText);
				}

				if(!success) {
					Log.v("server", "led color fail");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "led color and text not changed");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}
				break;
			case 11:
				Log.v("led", "led on");

				try {
					success = led.setColorOnLed(ledRed);
					success = led.openLed();
					Log.v("LED", "led open:");
				}catch(Exception e) {
					success = false;
					Log.v("LED", "led open fail");
				}

				if(!success) {
					Log.v("server", "led open fail");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "led fail to open");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}
				break;
			case 12:
				Log.v("led", "led off");

				try {
					success = led.closeLed();
					Log.v("LED", "led closed:");
				}catch(Exception e) {
					success = false;
					Log.v("LED", "led close fail");
				}

				if(!success) {
					Log.v("server", "led close fail");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "led fail to close");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}
				break;

			case 13:
				Log.v("LED", "led show temp");

				TempOperations to = new TempOperations();		    
				Double tempD = to.getLatestReading(sendDisplayCe);
				if(tempD != null) {
					Log.v("led temp", "show "+ tempD);
					int temp = (int)tempD.doubleValue();
					ledText = Integer.toString(temp);

					success = led.setColorOnLed(ledRed);
					try {
						ledNum = Integer.parseInt(ledText);
						if(ledNum < 0 || ledNum >99) {
							throw new UnsupportedOperationException();
						}
						success = led.showNumOnLed(ledText);
						Log.v("LED", "ledNum showed:" + ledNum);
					}catch(Exception e) {

						ledText = ledText.replace(" ", "");
						success = led.showTextOnLed(ledText);
						Log.v("LED", "ledText showed:" + ledText);
					}
				}else {
					success = false;
				}
				if(!success) {
					Log.v("server", "led show temp fail");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("fail", "led fail to show temp");
					msg.setData(b);
					// send message to the handler with the current message handler
					handler.sendMessage(msg);
				}

				break;
			default:
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("fail", "operation not supported");
				msg.setData(b);
				// send message to the handler with the current message handler
				handler.sendMessage(msg);
				break;
			}

			return null;
		}

	}
}