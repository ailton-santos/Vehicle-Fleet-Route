package com.SEProject.fleetmanagement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.SEProject.helpers.GPSTracker;

public class MainActivity extends Activity {
	
	String STUDENT_USN="1PI12CS072";
	private volatile String Latitude1,Longitude1;
	public GPSTracker gps;
	TextView tvLat,tvLong;
	public Runnable runnable;Handler handler; getLocation gl;Boolean gettingLoc;
	InputStream is=null;String result=null;String line=null;int code;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startService=(Button)findViewById(R.id.bStartService);
        Button stopService=(Button)findViewById(R.id.bStopService);
        tvLat=(TextView)findViewById(R.id.tvLat);
        tvLong=(TextView)findViewById(R.id.tvLong);
        gettingLoc=false;
        
        android.app.ActionBar actionBar =  getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#c53e2b"));
		actionBar.setBackgroundDrawable(colorDrawable);
		
		gps = new GPSTracker(this);
		handler = new Handler();
		
		
		startService.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				gettingLoc=true;
			  gl= new getLocation();
			  gl.execute();
			}
				
		});
		
		stopService.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//handler.removeCallbacks(runnable);
				//gl.cancel(true);
				gettingLoc=false;
				System.out.println("Real Time Tracking has been stopped Succesfully!!");
			}
		});
		
		/*startService.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				   Use the LocationManager class to obtain GPS locations 
			      LocationManager mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

			      LocationListener mlocListener = new MyLocationListener();
			      mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
			   
				
				
				if(gps.canGetLocation()){  
					getGPS();
				}else{
					gps.showSettingsAlert();
					return;
				}
			}
		});
   */ 
		
    
    
    }
    
    
    public void getGPS()
	{
		double latitude = gps.getLatitude();
		Latitude1= String.valueOf(latitude);
		double longitude = gps.getLongitude(); 
		Longitude1= String.valueOf(longitude); 
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				insertLoc al=new insertLoc();
				al.execute();
				//insert(Latitude1,Longitude1);
		        
			}
		}).start();
		
		
		
		
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				tvLat.append("Latitude   "+Latitude1+"\n");
				tvLong.append("Longitude   "+Longitude1+"\n");
			}
		});
		
	}
    
    class getLocation extends AsyncTask<String,String,String>{

    	
    	
    	
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			Toast.makeText(getApplicationContext(), "Service has been started.", Toast.LENGTH_SHORT).show();
			
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					/*if(gps.canGetLocation()){  
						getGPS();
					}else{
						gps.showSettingsAlert();
						return;
					}
					*/
					
					handler.postDelayed(runnable, 5000);
					runnable = new Runnable() {
						   @Override
						   public void run() {
						      /* do what you need to do */
							   getGPS();
							   

						      /* and here comes the "trick" */
							   if(gettingLoc){System.out.println("itz called");
						      handler.postDelayed(this, 3000);}
						   }
						};
					
				}
			});
			
			return null;
		}
    	
    }
    
   public void insert(String latt,String longt)
   {
	   try{
		   System.out.println("In insert");
		   HttpClient client = new DefaultHttpClient();
		   String url="http://104.199.153.214/api1/api/participant/live/"+STUDENT_USN+"/"+latt+"/"+longt;
		   HttpGet request = new HttpGet(url);
		   HttpResponse response = client.execute(request);

		   // Get the response
		   BufferedReader rd = new BufferedReader
		     (new InputStreamReader(response.getEntity().getContent()));
		       
		   String line = "";
		   while ((line = rd.readLine()) != null) {
		     System.out.println("Response From HttpGet "+line);
		   } 
		   
	   }
	   catch(Exception e)
	   {
		    Log.e("HTTPGet","Updating to database failed!!");
	   }
   }
   
   
   public void insertsos(String latt,String longt)
   {
	   try{
		   System.out.println("SOS Called");
		   HttpClient client = new DefaultHttpClient();
		   HttpGet request = new HttpGet("http://104.199.153.214/api1/api/participant/sos/"+STUDENT_USN+"/"+latt+"/"+longt);
		   HttpResponse response = client.execute(request);

		   // Get the response
		   BufferedReader rd = new BufferedReader
		     (new InputStreamReader(response.getEntity().getContent()));
		       
		   String line = "";
		   while ((line = rd.readLine()) != null) {
		     System.out.println("Response From HttpGet(SOS)"+line);
		   } 
		   
	   }
	   catch(Exception e)
	   {
		    Log.e("HTTPGet","Updating to SOS failed!!");
	   }
   }
   
   
/*
    public void insert(String latt,String longt)
    {
    	HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://104.199.153.214/api1/api/participant/");
	    try {
	    	
	    	JSONObject jsonobj = new JSONObject();
	    	
	    	try {
	    		jsonobj.put("usn", STUDENT_USN);
	    		jsonobj.put("lat", latt);
		    	jsonobj.put("lon", longt);
		    	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        StringEntity se = new StringEntity(jsonobj.toString());
	        se.setContentType("application/json;charset=UTF-8");
	        se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));
	        
	        httppost.setEntity(se);
	        // Execute HTTP Post Request
	        HttpResponse response = httpclient.execute(httppost);
	        Log.d("Http Response:", response.toString());
	        System.out.println("Updated to Database sucessfully!!");
	    } catch (ClientProtocolException e) {
	        // TODO Auto-generated catch block
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	    }


}    
    
   */ 
 /*   
    public void insert(String latt,String longt)
    {
    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    
    nameValuePairs.add(new BasicNameValuePair("usn",STUDENT_USN));
   	nameValuePairs.add(new BasicNameValuePair("lat",latt));
   	nameValuePairs.add(new BasicNameValuePair("long",longt));
    	
    	try
    	{
		HttpClient httpclient = new DefaultHttpClient();
	      //  HttpPost httppost = new HttpPost("http://mathdemat.comuf.com/insert1.php");
		   HttpPost httppost = new HttpPost("http://104.199.153.214/api1/api/participant");
	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response = httpclient.execute(httppost); 
	        HttpEntity entity = response.getEntity();
	        is = entity.getContent();
	        Log.e("pass 1", "connection success ");
	        
	       HttpClient httpclient1 = new DefaultHttpClient();
	        HttpGet httpget = new HttpGet("http://mathdemat.comuf.com/insert1.php");
	        ((HttpResponse) httpget).setEntity(new UrlEncodedFormEntity(nameValuePairs));
	        HttpResponse response1 = httpclient1.execute(httppost); 
	        HttpEntity entity1 = response1.getEntity();
	        is = entity1.getContent();
	        
	        
	        
	}
        catch(Exception e)
	{
        	Log.e("Fail 1", e.toString());
	    	//Toast.makeText(getApplicationContext(), "Invalid IP Address",
			//Toast.LENGTH_LONG).show();
	}     
        
        try
        {
            BufferedReader reader = new BufferedReader
			(new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
	    {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
	    Log.e("pass 2", "connection success ");
	}
        catch(Exception e)
	{
            Log.e("Fail 2", e.toString());
	}     
       
	try
	{
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));
			
            if(code==1)
            {
            	//Toast.makeText(getBaseContext(), "Inserted Successfully",
            			//System.out.println("Inserted Sucessfully");
            }
            else
            {
			 Toast.makeText(getBaseContext(), "Sorry, Try Again",
			 Toast.LENGTH_LONG).show();
            	//System.out.println("Sorry Try again");
            }
	}
	catch(Exception e)
	{
            Log.e("Fail 3", e.toString());
	}
    }*/
    
 class insertLoc extends AsyncTask<String, String, String>{

	@Override
	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		insert(Latitude1,Longitude1);
		return null;
	}
 } 
 
 class insertSOS extends AsyncTask<String, String, String>{

	@Override
	protected String doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		insertsos(Latitude1,Longitude1);
		return null;
	}
 }
 
 
 public void onFeedbackClick(View v) 
 {
     // TODO Auto-generated method stub
     Intent intent=new Intent(MainActivity.this,FeedBackForm.class);
     intent.setData(Uri.parse("https://docs.google.com/forms/d/1LcyV3S3iZ7neKt9fmH3sVD_B1Z0twalEUxubiv14Ey8/viewform"));
     startActivity(intent);  
 }

 
 public void onSOSClick(View v) {
     Log.i("Send SMS", "");
     
     new insertSOS().execute();

     String phoneNo = "9611836018";
     String message = "Emergency Alert::\nhttps://www.google.com/maps/place/"+Latitude1+","+Longitude1;

     try {
        SmsManager smsManager = SmsManager.getDefault();
       // smsManager.sendTextMessage(phoneNo, null, message, null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.",
        Toast.LENGTH_LONG).show();
     } catch (Exception e) {
        Toast.makeText(getApplicationContext(),
        "SMS faild, please try again.",
        Toast.LENGTH_LONG).show();
        e.printStackTrace();
     }
  }


  
}
