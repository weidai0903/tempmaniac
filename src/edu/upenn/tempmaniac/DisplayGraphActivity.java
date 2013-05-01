package edu.upenn.tempmaniac;

import java.util.ArrayList;
import java.util.Random;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import edu.upenn.tempmaniac.R;

import edu.upenn.tempServerConnection.TempOperations;

public class DisplayGraphActivity extends Activity{

	Activity thisActivity;
	boolean barGraph;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.graph);
	    thisActivity = this;
	    // Get the message from the intent
	    Intent intent = getIntent();
	    //ArrayList<String> tempList = intent.getStringArrayListExtra("tempList");
	    ArrayList<String> tempList = new ArrayList<String>();
	    /*
	    double max = -100;
	    for( String temp : tempList ){
	    	double current = Double.parseDouble(temp);
	    	if( current > max ){
	    		max = current;
	    	}
	    }
	    
	    max = Math.ceil(max/10);
	    
	    double min = 100;
	    for( String temp : tempList ){
	    	double current = Double.parseDouble(temp);
	    	if( current < min ){
	    		min = current;
	    	}
	    }
	    
	    min = Math.floor(min/10);
	    
	    // init example series data
		*/
	    barGraph = true;
	    double max = 100;
	    double min = 0;
		XYMultipleSeriesDataset xySeries = getTempDataset( tempList );
		XYMultipleSeriesRenderer renderer = getDemoRenderer( tempList.size(), max, min );
	    GraphicalView graphView = ChartFactory.getLineChartView( this, xySeries, renderer);
	    LinearLayout layout = (LinearLayout) findViewById( R.id.LinearLayoutGraph );
	    layout.addView(graphView);
	    
	    RadioButton rb = (RadioButton) this.findViewById(R.id.radioShowBarGraph);
	    rb.setChecked(true);
	    
	    getTempData getData = new getTempData();
	    getData.execute();
	    Log.v("tempGraph", "asynchTask");

	}
	
	public void radioGraphTypeOnClick (View view) {
		RadioButton rb = (RadioButton) view;
		boolean checked = rb.isChecked();
		switch(view.getId()) {
		case R.id.radioShowBarGraph:
			if(checked) {
				barGraph = true;
				getTempData getData = new getTempData();
			    getData.execute();
			    Log.v("tempGraph", "show bar graph");
			}
			break;
		case R.id.radioShowLineGraph:
			if(checked) {
				barGraph = false;
				getTempData getData = new getTempData();
			    getData.execute();
			    Log.v("tempGraph", "show line graph");
			}
			break;
		}
	}
	
	  private XYMultipleSeriesDataset getTempDataset( ArrayList<String> tempList ) {
		    XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		    //Random r = new Random();
		    XYSeries series = new XYSeries("Temp series ");
		    for (int i = 0; i < tempList.size(); i++) {
		      //series.add(k, 20 + r.nextInt() % 100);
		    	series.add( i, Double.parseDouble( tempList.get(i) ) );
		    }
		    dataset.addSeries(series);
		    return dataset;
     }
	  
	  private XYMultipleSeriesRenderer getDemoRenderer( int xMax, double yMax , double yMin) {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    /*
		    renderer.setAxisTitleTextSize(16);
		    renderer.setChartTitleTextSize(20);
		    renderer.setLabelsTextSize(15);
		    renderer.setLegendTextSize(15);*/
		    renderer.setPointSize(5f);
		    renderer.setApplyBackgroundColor(true);
		    renderer.setBackgroundColor(Color.BLACK);
		    //renderer.setMarginsColor(Color.parseColor("#F5F5F5"));
		    renderer.setMarginsColor(Color.BLACK);
		    XYSeriesRenderer r = new XYSeriesRenderer();
		    r.setColor(Color.GREEN);
		    /*
		    r.setColor(Color.BLUE);
		    r.setPointStyle(PointStyle.SQUARE);
		    r.setFillBelowLine(true);
		    r.setFillBelowLineColor(Color.WHITE);
		    r.setFillPoints(true);
		    */
		    renderer.addSeriesRenderer(r);
		    /*
		    r = new XYSeriesRenderer();
		    r.setPointStyle(PointStyle.CIRCLE);
		    r.setColor(Color.GREEN);
		    r.setFillPoints(true);
		    renderer.addSeriesRenderer(r);
		    */
		    renderer.setYTitle("Temperature");
		    renderer.setXTitle("Time");
		    renderer.setYLabelsAlign(Align.RIGHT);
		    renderer.setAxesColor(Color.BLACK);
		    
		    renderer.setLabelsColor(Color.BLACK);
		    renderer.setXAxisMax( xMax );
		    Log.v("123",""+yMax);
		    renderer.setYAxisMax( yMax*10);
		    renderer.setYAxisMin(yMin * 10);
		    renderer.setChartTitle("Temperature/Time");
		    
		    return renderer;
	 }
	  
	  private XYMultipleSeriesRenderer getBarDemoRenderer(int xMax, double yMax , double yMin) {
		    XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		    renderer.setAxisTitleTextSize(16);
		    renderer.setChartTitleTextSize(20);
		    renderer.setLabelsTextSize(15);
		    renderer.setLegendTextSize(15);
		    renderer.setMargins(new int[] {20, 30, 15, 0});
		    renderer.setXAxisMax( xMax );
		    renderer.setYAxisMax( yMax*10);
		    renderer.setYAxisMin(yMin * 10);
		    renderer.setChartTitle("Temperature/Time");
		    
		    SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		    r.setColor(Color.BLUE);
		    renderer.addSeriesRenderer(r);
		    r = new SimpleSeriesRenderer();
		    r.setColor(Color.GREEN);
		   
		    renderer.addSeriesRenderer(r);
		    return renderer;
	  }
	  
	  
	  
	 public void onClickCancel( View view ){
		 finish();
	 }
	 
	 
	 class getTempData extends AsyncTask<Void, Void, ArrayList<String>>{
		ArrayList<String> temps;
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			Log.v("tempGraph", "going to get temp in one hour");
			TempOperations to = new TempOperations();
			boolean r = to.initialize();
			if(r) {
				 temps = to.getAllTempOneHour();
				 Log.v("TempGraph", "dispGraph:");
				return temps;
			}else
				temps = to.fakeGetAllTempOneHour(10);
				Log.v("TempGraph", "fail loading data in doBackground");
				return temps;
	       
		}
		
		@Override
		protected void onPostExecute (ArrayList<String> result) {
			if(result!=null) {
				temps = result;
				double max = Integer.MIN_VALUE;
			    for( String temp : temps ){
			    	double current = Double.parseDouble(temp);
			    	if( current > max ){
			    		max = current;
			    	}
			    }
			    
			    max = Math.ceil(max/10);
			    
			    double min = 100;
			    for( String temp : temps ){
			    	double current = Double.parseDouble(temp);
			    	if( current < min ){
			    		min = current;
			    	}
			    }
			    
			    min = Math.floor(min/10);
			    
			    // init example series data

				XYMultipleSeriesDataset xySeries = getTempDataset( temps );
				
				
				XYMultipleSeriesRenderer renderer = getDemoRenderer( temps.size(), max, min );
				GraphicalView graphView;
				
				if(!barGraph) {
				
					graphView = ChartFactory.getLineChartView( thisActivity, xySeries, renderer);
				}else {
					graphView = ChartFactory.getBarChartView(thisActivity, xySeries, renderer, Type.DEFAULT);

				}
			   
				
				Log.v("graph", "temp size =" + temps.size());
				//XYMultipleSeriesRenderer renderer = getBarDemoRenderer(temps.size(), max, min);
				//XYMultipleSeriesRenderer renderer = getDemoRenderer( temps.size(), max, min );
				if(renderer == null) {
					Log.v("graph", "renderer is null");
				}
				if(xySeries == null) {
					Log.v("graph", "xySeries is null");
				}
				
				//GraphicalView graphView = ChartFactory.getBarChartView(thisActivity, xySeries, renderer, Type.DEFAULT);
			    LinearLayout layout = (LinearLayout) findViewById( R.id.LinearLayoutGraph );
			    layout.removeAllViews();
			    layout.addView(graphView);
			}else {
				Toast t = Toast.makeText(thisActivity, "Fail to load graph", Toast.LENGTH_SHORT);
				t.show();
			}
			
		}
		 
	 }
}
