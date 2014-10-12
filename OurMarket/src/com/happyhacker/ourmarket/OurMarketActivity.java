package com.happyhacker.ourmarket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.happyhacker.ourmarket.util.SystemUiHider;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class OurMarketActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 

        setContentView(R.layout.activity_our_market);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreen_content);
        
        // THIS WAS HERE WHEN I GOT HERE! PLS IGNORE
        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider.show();
        
        new JSONLoader().execute("http://ourmarket.org.uk/api/market-vendors");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }


    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
    
    
    class JSONLoader extends AsyncTask<String, Void, String> {

		@SuppressLint("NewApi")
		@Override
		protected String doInBackground(String... args){
			JSONArray vendors = null;
			try {
				vendors = new JSONArray(getJSON(args[0]));
				//Toast.makeText(OurMarketActivity.this, args[0], Toast.LENGTH_LONG).show();
				final String[] stallStrings = new String[vendors.length()];
				for(int i =0; i < vendors.length(); i++){
		        	JSONObject jObj = vendors.getJSONObject(i);

		        	stallStrings[i] = jObj.getString("stall_name");
		        	
		        }
				final ListView stallList = (ListView) findViewById(R.id.stall_list);
		        
		        final ListItem adapter = new ListItem(OurMarketActivity.this, stallStrings, vendors, OurMarketActivity.this);
		        
		        runOnUiThread(new Runnable() {
				     @Override
				     public void run() {

				    	 stallList.setAdapter(adapter);
				    	 //Add some code to the list items to when you hit them it changes screen
				         stallList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				             @Override
				             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				             	Intent intent = new Intent(OurMarketActivity.this, StallActivity.class);
				             	//Code to pass some text to the next page. Should pass ID
				                 try {
								 	intent.putExtra("JSON", adapter.getJSON(position).toString());
							 	} catch (JSONException e) {
							 		// TODO Auto-generated catch block
							 		e.printStackTrace();
							 	}
				                startActivity(intent);
				             }
					     });
				         
				         findViewById(R.id.loadingPanel).setVisibility(View.GONE);
				    	 

				    }
				});
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
		
		String getJSON(String url){
			StringBuilder builder = new StringBuilder();
    	    HttpClient client = new DefaultHttpClient();
    	    HttpGet httpGet = new HttpGet(url);
    	    try {
    	      HttpResponse response = client.execute(httpGet);
    	      StatusLine statusLine = response.getStatusLine();
    	      int statusCode = statusLine.getStatusCode();
    	      if (statusCode == 200) {
    	        HttpEntity entity = response.getEntity();
    	        InputStream content = entity.getContent();
    	        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
    	        String line;
    	        while ((line = reader.readLine()) != null) {
    	          builder.append(line);
    	        }
    	      } else {
    	        //Log.e(ParseJSON.class.toString(), "Failed to \wnload file");
    	      }
    	    } catch (ClientProtocolException e) {
    	      e.printStackTrace();
    	    } catch (IOException e) {
    	      e.printStackTrace();
    	    }
    	    return builder.toString();
		}
    	
    }
}
