package edu.upenn.tempmaniac;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import edu.upenn.tempmaniac.R;

import edu.upenn.geolocation.GPSTracker;
import edu.upenn.geolocation.LocationService;
import edu.upenn.tempServerConnection.Weather;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity {
	private Drawable icon;
	LinearLayout myGallery;
	private static Handler handler;
	//private LinearLayout currentConditionLayout;
	Activity thisActivity;
	private LinearLayout galary;
	private Drawable forecastIcons[];
	ProgressBar progressBar;
	EditText editTextCity;
	String setCity;
	RadioButton radioCeWeather;
	String GPSCity = null;
	boolean showCe = true;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);    

		galary = (LinearLayout) findViewById(R.id.galary);
		//galary.set
		progressBar = (ProgressBar) findViewById(R.id.progressbarWeather);
		progressBar.setProgress(0);
		progressBar.setVisibility(View.INVISIBLE);

		editTextCity = (EditText)this.findViewById(R.id.editTextSetCity);
		setCity = editTextCity.getText().toString();

		radioCeWeather = (RadioButton)this.findViewById(R.id.radioWeatherCe);
		radioCeWeather.setChecked(true);

		thisActivity = this;
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				Log.v("weather handler:", "displaying weather");

				Bundle b = msg.getData();
				String error = b.getString("error");
				if(error!=null) {
					Toast.makeText(thisActivity,
							error, Toast.LENGTH_SHORT).show();
					return;

				}
				String time = b.getString("time");
				String currentTemp = b.getString("currentTemp");
				String weatherIcon = b.getString("weatherIcon");
				String desc = b.getString("description");
				String code = b.getString("code");

				Log.v("weather icon", weatherIcon);
				TextView tvCurrentTime = new TextView(thisActivity);
				tvCurrentTime.setText("Current time: "+time);
				tvCurrentTime.setBackgroundColor(Color.BLACK);
				tvCurrentTime.setTextColor(Color.WHITE);

				TextView tvCurrentTemp = new TextView(thisActivity);
				tvCurrentTemp.setText("Current temperature:"+currentTemp);

				//TextView tvWeatherIcon = new TextView(thisActivity);
				//tvWeatherIcon.setText(weatherIcon);
				ImageView iconImage = new ImageView(thisActivity);
				iconImage.setImageDrawable(icon);

				TextView tvDescription = new TextView(thisActivity);
				tvDescription.setText("Today is " + desc);
				TextView tvCode = new TextView(thisActivity);
				tvCode.setText(code);

				LinearLayout currentConditionLayout = new LinearLayout(thisActivity);
				currentConditionLayout.setBackgroundColor(color.holo_green_light);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT, 1);
				params.gravity = Gravity.CENTER_HORIZONTAL;
				params.leftMargin = 100;


				currentConditionLayout.setLayoutParams(params);

				currentConditionLayout.setOrientation(LinearLayout.VERTICAL);
				currentConditionLayout.addView(tvCurrentTime);
				currentConditionLayout.addView(tvCurrentTemp);
				currentConditionLayout.addView(iconImage);
				currentConditionLayout.addView(tvDescription);
				currentConditionLayout.addView(tvCode);

				galary.removeAllViews();
				galary.addView(currentConditionLayout);
			}
		};



		//getWeatherTask weatherTask = new getWeatherTask();
		//weatherTask.execute();



		/*
        int how_many_buttons = 10;
        for (int i=0; i<how_many_buttons; i++) {     
            Button btn = new Button(this);   
            btn.setHeight(20);     
            btn.setText("EXAMPLE");    
            //btn.setOnClickListener(this);     
            ll.addView(btn); 
        } 
		 */

		/*
        String ExternalStorageDirectoryPath = Environment
                .getExternalStorageDirectory()
                .getAbsolutePath();   
        String targetPath = ExternalStorageDirectoryPath + "/test/";

        Log.v("targetPath", targetPath);
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setGravity(Gravity.CENTER);
		 */

		//myGallery.addView(layout);

	}

	

	public void buttonUpdateCityOnClick(View view) {
		LocationService ls = new LocationService(thisActivity);
		ls.getCity((TextView)this.findViewById(R.id.textViewCity));
	}

	public void radioWeatherOnClick(View view) {

		boolean checked = ((RadioButton) view).isChecked();
		switch(view.getId()) {
		case R.id.radioWeatherCe:
			if(checked) {
				showCe = true;
			}
			break;
		case R.id.radioWeatherFa:
			if(checked) {
				showCe = false;
			}
			break;
		}
	}


	public void dispCurrentTempOnClick(View view) {
		TextView tvLocation = (TextView)this.findViewById(R.id.textViewCity);
		GPSCity = tvLocation.getText().toString();
		setCity = editTextCity.getText().toString();
		getWeatherTask weatherTask = new getWeatherTask();
		weatherTask.execute(0);
	}

	public void dispForecastOnClick(View view) {
		TextView tvLocation = (TextView)this.findViewById(R.id.textViewCity);
		GPSCity = tvLocation.getText().toString();
		setCity = editTextCity.getText().toString();
		getWeatherTask weatherTask = new getWeatherTask();
		weatherTask.execute(1);
	}


	public static Drawable LoadImageFromWebOperations(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, "weather icon");
			Log.v("Load image", "complete");
			return d;
		} catch (Exception e) {
			Log.v("exception", e.toString());
			e.printStackTrace();
			return null;
		}
	}

	private double convertCeToFa(double ce) {
		return ce*1.8 + 32;
	}

	private double convertFaToCe(double fa) {
		return (fa - 32)/1.8;
	}

	class getWeatherTask extends AsyncTask<Integer, Integer, ArrayList<String[]>>{

		String currentTime;
		int myProgress;
		@Override
		protected void onPreExecute(){

			//Toast.makeText(thisActivity,
			//"Retrieving weather data", Toast.LENGTH_LONG).show();
			progressBar.setVisibility(View.VISIBLE);
			myProgress = 0;
			publishProgress(myProgress);
		}
		
		
		@Override
		protected ArrayList<String[]> doInBackground(Integer... params) {
			Log.v("Weather", "back ground thread starts");
			Weather weather = new Weather();
			switch(params[0]) {
			case 0:
				String[] condition = null;
				if(setCity.equals("")) {
					if(!GPSCity.equals("") && !GPSCity.equals("N/A") ) {
						condition = weather.getCurrentCondition(GPSCity);
						Log.v("weather", "get current temp for "+ GPSCity);
					}else {
						Log.v("weather", "get current temp for Philly");
						condition = weather.getCurrentCondition("philadelphia");
					}
				}else {
					Log.v("weather", "show current temp for "+ setCity);
					condition = weather.getCurrentCondition(setCity);
				}
				if(condition == null) {
					Log.v("Weather", "error get current condition");
					Message msg = new Message();
					Bundle b = new Bundle();
					b.putString("error", "city not found");
					msg.setData(b);
					handler.sendMessage(msg);
					return null;
				}
				Log.v("Weather", "Successful get current condition");
				myProgress = 50;
				publishProgress(myProgress);
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("time", condition[0]);
				if(!showCe) {
					try {
						double tempD = Double.parseDouble(condition[1]);
						double tempFa = convertCeToFa(tempD);
						b.putString("currentTemp", String.format("%.1f", tempFa)+ " F");
					}catch(Exception e) {
						e.printStackTrace();
					}
				}else {
					b.putString("currentTemp", condition[1]+" C");
				}
				b.putString("weatherIcon", condition[2]);
				b.putString("description", condition[3]);
				b.putString("code", condition[4]);
				currentTime = condition[0];

				icon = LoadImageFromWebOperations(condition[2]);

				msg.setData(b);
				// send message to the handler with the current message handler
				handler.sendMessage(msg);

				myProgress = 80;
				publishProgress(myProgress);
				return new ArrayList<String[]>();


			case 1:
				ArrayList<String[]> conditions;
				if(setCity.equals("")) {
					if(!GPSCity.equals("") || !GPSCity.equals("N/A") ) {
						conditions = weather.getTempForecast(5, GPSCity);
						Log.v("weather", "get forcast for "+ GPSCity);
					}else {
						Log.v("weather", "get forecast for philly");
						conditions = weather.getTempForecast(5, "philadelphia");
					}
				}else {
					Log.v("weather", "get forcast for "+ setCity);
					conditions = weather.getTempForecast(5, setCity);
				}
				if(conditions == null) {
					return null;
				}
				forecastIcons = new Drawable[5];
				myProgress = 20;
				publishProgress(myProgress);
				for(int i=0; i<conditions.size(); i++) {
					String url = conditions.get(i)[3];
					forecastIcons[i] = LoadImageFromWebOperations(url);
					if(myProgress<100)
						myProgress += 20; 
					publishProgress(myProgress);
				}
				return conditions;
			}

			return null;
		}
		@Override
		protected void onProgressUpdate (Integer... values) {
			progressBar.setProgress(values[0]);
		}
		@Override
		protected void onPostExecute (ArrayList<String[]> result) {
			myProgress = 100;
			publishProgress(myProgress);

			if(result !=null) {
				if(result.size()>0) {

					galary.removeAllViews();

					for(int i=1; i<result.size(); i++) {
						String[] forecast = result.get(i);

						TextView tvCurrentTime = new TextView(thisActivity);
						tvCurrentTime.setText("Date: "+ forecast[0]);
						tvCurrentTime.setBackgroundColor(Color.BLACK);
						tvCurrentTime.setTextColor(Color.WHITE);

						TextView tvMaxTemp = new TextView(thisActivity);
						TextView tvMinTemp = new TextView(thisActivity);

						if(!showCe) {
							try {
								double maxT = Double.parseDouble(forecast[1]);
								double maxTF = convertCeToFa(maxT);
								tvMaxTemp.setText("Max temperature: "+ String.format("%.1f", maxTF) + " F");

								double minT = Double.parseDouble(forecast[2]);
								double minTF = convertCeToFa(minT);
								tvMinTemp.setText("Min temperature: "+ String.format("%.1f", minTF) + " F");

							}catch(Exception e) {
								e.printStackTrace();
							}
						}else {
							tvMinTemp.setText("Min temperature: "+ forecast[2] + " C");
							tvMaxTemp.setText("Max temperature: "+ forecast[1] + " C");
						}


						TextView tvTempDesc= new TextView(thisActivity);
						ImageView iconImage = new ImageView(thisActivity);
						iconImage.setImageDrawable(forecastIcons[i]);

						tvTempDesc.setText("Condition: "+ forecast[4]);

						LinearLayout currentConditionLayout = new LinearLayout(thisActivity);
						currentConditionLayout.setOrientation(LinearLayout.VERTICAL);
						currentConditionLayout.addView(tvCurrentTime);
						currentConditionLayout.addView(tvMaxTemp);
						currentConditionLayout.addView(tvMinTemp);
						currentConditionLayout.addView(iconImage);
						currentConditionLayout.addView(tvTempDesc);
						//currentConditionLayout
						getBorder getBorder = new getBorder(thisActivity);
						getBorder.setWidth(5);
						getBorder.setHeight(165);
						galary.addView(currentConditionLayout);
						galary.addView(getBorder);
					}
				}else {

				}
			}
			else {
				Toast.makeText(thisActivity,
						"City not found", Toast.LENGTH_LONG).show();
			}
			progressBar.setVisibility(View.INVISIBLE);
		}	
	}

	public class getBorder extends TextView {
		public getBorder(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			Paint paint = new Paint();

			paint.setColor(android.graphics.Color.RED);

			canvas.drawLine(0, 0, this.getWidth() - 1, 0, paint);
			canvas.drawLine(0, 0, 0, this.getHeight() - 1, paint);
			canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1,
					this.getHeight() - 1, paint);
			canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1,
					this.getHeight() - 1, paint);
		}
	}

}