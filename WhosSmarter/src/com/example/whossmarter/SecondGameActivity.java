package com.example.whossmarter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.whossmarter.SecondGameActivityOnePlayer.MyCounter;
import com.example.whossmarter.SecondGameActivityOnePlayer.RequestTask;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public  class SecondGameActivity extends Activity implements OnClickListener{
	
	public static Context context;
	public static int counter;
	public static ProgressDialog progressDialog;
	public static String URI = "http://150.140.210.26:8080/WebServiceRest/webresources/service/1";
	public static ProgressBar progress;
	ArrayList<String> generated = new ArrayList<String>();
	
	public static boolean ButtonAlreadyPressedA;
	
	public static TextView questTV, tv2 , score1, score2, player01, player02;
	public static Button button1, button2, button3, button4, button5;
	
	public String lastQuestionflag = "j";
	public String Flagback = "b";
	public String positiveflag = "p";
	public String negativeflag = "n";
	public static String FlagA = "a";
	public static String timeProgrBarflag = "t";
	public static int right_answer;
	
	public static boolean StopThread;
	
	private JSONObject jsonObject;
    String strParsedValue = null;

    public static String[][] ParsedJsonArray= new String[20][6];	
    
	static JSONObject jObj = null;
    static String json = "";
	
    // contacts JSONArray
    JSONArray Questions = null;
	
    //Score
    public static int score_A;
    public static int score_B;
    
    public static int progressValue = 0;
    
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//hide titlebar from this Activity
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_game_two_players);
		
		SecondGameActivity.context = getApplicationContext();
		
		MainActivity.DownloadedCompleteA = false;
		
		StopThread = false;
		
		counter = 0;
		score_A = 0;
		score_B = 0;
	
		 
		button1 = (Button)findViewById(R.id.button1);
		button1.setOnClickListener(this);
		button1.setBackgroundResource(R.drawable.btn);
		
		button2 = (Button)findViewById(R.id.button2);
		button2.setOnClickListener(this);
		button2.setBackgroundResource(R.drawable.btn);
		
		button3 = (Button)findViewById(R.id.button3);
		button3.setOnClickListener(this);
		button3.setBackgroundResource(R.drawable.btn);
		
		button4 = (Button)findViewById(R.id.button4);
		button4.setOnClickListener(this);
		button4.setBackgroundResource(R.drawable.btn);
		
		button5 = (Button)findViewById(R.id.button5);
		button5.setOnClickListener(this);
		button5.setBackgroundResource(R.drawable.roundbtn);
		
		
		player01  = (TextView)findViewById(R.id.textView5);
		player01.setText(MainActivity.name_of_player);
		player02  = (TextView)findViewById(R.id.textView6);
		player02.setText(MainActivity.name_of_player2);
		
		score1  = (TextView)findViewById(R.id.textView2);
		score1.setText("0");
		score2  = (TextView)findViewById(R.id.textView3);
		score2.setText("0");
		questTV  = (TextView)findViewById(R.id.QuestTV);
		tv2  = (TextView)findViewById(R.id.textView1);
        tv2.setText("3"); // startting from 3.
        
        final MyCounter timer = new MyCounter(4000,1000);
	    timer.start();
        
        //final ProgressBarCounter timer2 = new ProgressBarCounter(4000,1000);

        progress = (ProgressBar)findViewById(R.id.progress);
        
	}
	

    
	@Override
    public void onBackPressed() {
            super.onBackPressed();
            byte[] send = Flagback.getBytes();
    		MainActivity.write(send);
            StopThread = true;
            this.finish();
            System.exit(0);
    }
	
	
	public class RequestTask extends AsyncTask<String, String, String>{
		
	    @Override
	    protected String doInBackground(String... uri) {
	        HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	            response = httpclient.execute(new HttpGet(uri[0]));
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	            } else{
	                //Closes the connection.
	                response.getEntity().getContent().close();
	                throw new IOException(statusLine.getReasonPhrase());
	            }
	        } catch (ClientProtocolException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
	        return responseString;
	    }
	 
	    @Override
	    protected void onPostExecute(String result) {
	        super.onPostExecute(result);
	        //Do anything with response..
	        if(result != null){
		        try {
		        	parseJSON(result);
	
			        } catch (JSONException e) {
			        	// TODO Auto-generated catch block
			        	e.printStackTrace();
		        	}
	        }else{
	        	StopThread = true;
	            System.exit(0);
	            Toast.makeText(SecondGameActivity.this, "Problem with question downloading",Toast.LENGTH_SHORT).show();
	        }
	     }
	 }
	
	
	
	public void mReadJsonData() {
		//Get Data From Text Resource File Contains Json Data.

        InputStream inputStream = getResources().openRawResource(R.raw.questions);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        int ctr;
        try {
            ctr = inputStream.read();
            while (ctr != -1) {
                byteArrayOutputStream.write(ctr);
                ctr = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("Text Data", byteArrayOutputStream.toString());
        try {

            // Parse the data into jsonobject to get original data in form of json.  
            JSONObject jObject = new JSONObject(byteArrayOutputStream.toString());
            JSONObject jObjectResult = jObject.getJSONObject("Categories");
            JSONArray jArray = jObjectResult.getJSONArray("Category");
           
            for (int i = 0; i < jArray.length(); i++) {
            	// Creating JSONObject from JSONArray
    			JSONObject jsonObj = jArray.getJSONObject(i);
    	
    			// Getting data from individual JSONObject
    			String right_answer = jsonObj.getString("right_answer");
    			String question = jsonObj.getString("question");
    		    String Answer_A = jsonObj.getString("A");
    			String Answer_B = jsonObj.getString("B");
    			String Answer_C = jsonObj.getString("C");
    			String Answer_D = jsonObj.getString("D");
    			
    			ParsedJsonArray[i][0] = right_answer;
    	        ParsedJsonArray[i][1] = question;
    	        ParsedJsonArray[i][2] = Answer_A;
    	        ParsedJsonArray[i][3] = Answer_B;
    	        ParsedJsonArray[i][4] = Answer_C;
    	        ParsedJsonArray[i][5] = Answer_D;
    	        
    		}
            setScore();
    		printQuestion();
      		printAnswer();
         

        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	public void parseJSON(String results) throws JSONException
    {
		// Try to parse JSON
		try {
		// Creating JSONObject from String
		JSONObject jsonObjMain = new JSONObject(results);

		// Creating JSONArray from JSONObject
		JSONArray jsonArray = jsonObjMain.getJSONArray("Questions");

		// JSONArray has four JSONObject
		for (int i = 0; i < jsonArray.length(); i++) {

			// Creating JSONObject from JSONArray
			JSONObject jsonObj = jsonArray.getJSONObject(i);
	
			// Getting data from individual JSONObject
			String right_answer = jsonObj.getString("right_answer");
			String question = jsonObj.getString("question");
		    String Answer_A = jsonObj.getString("A");
			String Answer_B = jsonObj.getString("B");
			String Answer_C = jsonObj.getString("C");
			String Answer_D = jsonObj.getString("D");
			
			ParsedJsonArray[i][0] = right_answer;
	        ParsedJsonArray[i][1] = question;
	        ParsedJsonArray[i][2] = Answer_A;
	        ParsedJsonArray[i][3] = Answer_B;
	        ParsedJsonArray[i][4] = Answer_C;
	        ParsedJsonArray[i][5] = Answer_D;

	        //questTV.setText(ParsedJsonArray[1][5]);
	        
		}

		byte[] send = FlagA.getBytes();
		MainActivity.write(send);
		new loopAsyncTask().execute();
		
		} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}

    }
	
	

	public static void printQuestion(){
		
		questTV.setText(ParsedJsonArray[counter][1]);
		progressValue = 0;
        progress.setProgress(progressValue);
		new Thread(myThread).start();
		
	}
    
	
	public static void printAnswer(){
		
    	button1.setText(ParsedJsonArray[counter][2]);
    	button2.setText(ParsedJsonArray[counter][3]);
    	button3.setText(ParsedJsonArray[counter][4]);
    	button4.setText(ParsedJsonArray[counter][5]);
    	button5.setText(String.valueOf(counter+1));
    	ButtonAlreadyPressedA = false;
    	MainActivity.ButtonAlreadyPressedB = false;
    	
	}

	
	public static void setScore(){
    	score1.setText(String.valueOf(score_A*20));
    	score2.setText(String.valueOf(score_B*20));
    	
	}
	
	
	@Override
    public void onClick(View v) {
		
		if(ButtonAlreadyPressedA == false && MainActivity.ButtonAlreadyPressedB == false ){
			ButtonAlreadyPressedA = true;
        switch (v.getId()) {
        case R.id.button1:
        	String id1 = "A";
        	if(id1.equals(ParsedJsonArray[counter][0])){
        		button1.setBackgroundColor( -16711936);
  	            score_A++;
        	}
        	if(!id1.equals(ParsedJsonArray[counter][0])){
        		button1.setBackgroundColor( -65536 );
        		score_A--;
        	}
        	
        		final Handler handler = new Handler();
        		handler.postDelayed(new Runnable() {
        		  @Override
        		  public void run() {
        			  String id1 = "A";
        	        	if(id1.equals(ParsedJsonArray[counter][0])){
        	        		byte[] send = positiveflag.getBytes();
        	        		MainActivity.write(send);
        	        		
        	  	            
        	        	}
        	        	if(!id1.equals(ParsedJsonArray[counter][0])){
        	        		byte[] send = negativeflag.getBytes();
        	  	            MainActivity.write(send);
        	        		
        	        	}
        	          setScore();	
        			  button1.setBackgroundResource(R.drawable.btn);
    				  //RequestTask task = (RequestTask) new RequestTask().execute(URI+questArray[counter]);
        			  if(counter<MainActivity.numberOfQuestions){	  
        			  	  counter = counter+1;
	    				  printQuestion();
	    			      printAnswer();
	        		  }else{
	        			  Intent localIntent = new Intent(SecondGameActivity.context, finalScoreTwoPlayers.class);	        			  
	        			  Bundle b1 = new Bundle();
	        			  b1.putInt("scoreA", score_A);
	        			  localIntent.putExtras(b1);
	        			  Bundle b2 = new Bundle();
	        			  b2.putInt("scoreB", score_B);
	        			  localIntent.putExtras(b2);
	        			  Bundle b3 = new Bundle();
	        			  b3.putString("nameA", MainActivity.name_of_player);
	        			  localIntent.putExtras(b3);
	        			  Bundle b4 = new Bundle();
	        			  b4.putString("nameB", MainActivity.name_of_player2);
	        			  localIntent.putExtras(b4);
	        			  startActivity(localIntent);
	      		          System.exit(0);
	        		  }
        		  }
        		  
        		}, 1000);
        		
        	
        		
            break;
        case R.id.button2:
        	String id2 = "B";
        	if(id2.equals(ParsedJsonArray[counter][0])){
        		button2.setBackgroundColor( -16711936);
        		score_A++;
        	}
        	if(!id2.equals(ParsedJsonArray[counter][0])){
        		button2.setBackgroundColor( -65536 );	
        		score_A--;
        	}
        	
        		final Handler handler2 = new Handler();
        		handler2.postDelayed(new Runnable() {
        		  @Override
        		  public void run() {
        			  String id2 = "B";
        	        	if(id2.equals(ParsedJsonArray[counter][0])){
        	        		byte[] send = positiveflag.getBytes();
        	  	            MainActivity.write(send);
        	        	}
        	        	if(!id2.equals(ParsedJsonArray[counter][0])){
        	        		byte[] send = negativeflag.getBytes();
        	  	            MainActivity.write(send);
        	        		
        	        	}
        	        	setScore();
        				button2.setBackgroundResource(R.drawable.btn);
        				if(counter<MainActivity.numberOfQuestions){	  
          			  	  counter = counter+1;
  	    				  printQuestion();
  	    			      printAnswer();
  	        		  }else{
  	        			  Intent localIntent = new Intent(SecondGameActivity.context, finalScoreTwoPlayers.class);
  	        			  Bundle b1 = new Bundle();
	        			  b1.putInt("scoreA", score_A);
	        			  localIntent.putExtras(b1);
	        			  Bundle b2 = new Bundle();
	        			  b2.putInt("scoreB", score_B);
	        			  localIntent.putExtras(b2);
	        			  Bundle b3 = new Bundle();
	        			  b3.putString("nameA", MainActivity.name_of_player);
	        			  localIntent.putExtras(b3);
	        			  Bundle b4 = new Bundle();
	        			  b4.putString("nameB", MainActivity.name_of_player2);
	        			  localIntent.putExtras(b4);
  	      		          startActivity(localIntent);
  	      		          System.exit(0);
  	        		  }

        		  }
        		}, 1000);
        	    
        	break;
        case R.id.button3:
        	String id3 = "C";
        	if(id3.equals(ParsedJsonArray[counter][0])){
        		button3.setBackgroundColor( -16711936);
        		score_A++;
        	}
        	if(!id3.equals(ParsedJsonArray[counter][0])){
        		button3.setBackgroundColor( -65536 );
        		score_A--;
        	}
        	
        		final Handler handler3 = new Handler();
        		handler3.postDelayed(new Runnable() {
        		  @Override
        		  public void run() {
        			  String id3 = "C";
        	        	if(id3.equals(ParsedJsonArray[counter][0])){
        	        		byte[] send = positiveflag.getBytes();
        	  	            MainActivity.write(send);
        	        	}
        	        	if(!id3.equals(ParsedJsonArray[counter][0])){
        	        		byte[] send = negativeflag.getBytes();
        	  	            MainActivity.write(send);
        	        		
        	        	}
        	        	setScore();
        				button3.setBackgroundResource(R.drawable.btn);
        				if(counter<MainActivity.numberOfQuestions){	  
          			  	  counter = counter+1;
  	    				  printQuestion();
  	    			      printAnswer();
  	        		  }else{
  	        			  
  	        			  Intent localIntent = new Intent(SecondGameActivity.context, finalScoreTwoPlayers.class);  	        			Bundle b1 = new Bundle();
	        			  b1.putInt("scoreA", score_A);
	        			  localIntent.putExtras(b1);
	        			  Bundle b2 = new Bundle();
	        			  b2.putInt("scoreB", score_B);
	        			  localIntent.putExtras(b2);
	        			  Bundle b3 = new Bundle();
	        			  b3.putString("nameA", MainActivity.name_of_player);
	        			  localIntent.putExtras(b3);
	        			  Bundle b4 = new Bundle();
	        			  b4.putString("nameB", MainActivity.name_of_player2);
	        			  localIntent.putExtras(b4);
  	      		          startActivity(localIntent);
  	      		          System.exit(0);
  	        		  }
        		  }
        		}, 1000);
        	
        	break;
        case R.id.button4:
        	String id4 = "D";
        	if(id4.equals(ParsedJsonArray[counter][0])){
        		button4.setBackgroundColor( -16711936);   //prasino
        		score_A++;
        	}
        	if(!id4.equals(ParsedJsonArray[counter][0])){
        		button4.setBackgroundColor( -65536 );      //kokkino	
        		score_A--;
        	}
        	
        		final Handler handler4 = new Handler();
        		handler4.postDelayed(new Runnable() {
        		  @Override
        		  public void run() {
        			  String id4 = "D";
        	        	if(id4.equals(ParsedJsonArray[counter][0])){
        	        		byte[] send = positiveflag.getBytes();
        	  	            MainActivity.write(send);
        	        	}
        	        	if(!id4.equals(ParsedJsonArray[counter][0])){
        	        		byte[] send = negativeflag.getBytes();
        	  	            MainActivity.write(send);
        	        		
        	        	}
        	        	setScore();
        				button4.setBackgroundResource(R.drawable.btn);
        				if(counter<MainActivity.numberOfQuestions){	  
          			  	  counter = counter+1;
  	    				  printQuestion();
  	    			      printAnswer();
  	        		  }else{
  	        			  Intent localIntent = new Intent(SecondGameActivity.context, finalScoreTwoPlayers.class);  	        			Bundle b1 = new Bundle();
	        			  b1.putInt("scoreA", score_A);
	        			  localIntent.putExtras(b1);
	        			  Bundle b2 = new Bundle();
	        			  b2.putInt("scoreB", score_B);
	        			  localIntent.putExtras(b2);
	        			  Bundle b3 = new Bundle();
	        			  b3.putString("nameA", MainActivity.name_of_player);
	        			  localIntent.putExtras(b3);
	        			  Bundle b4 = new Bundle();
	        			  b4.putString("nameB", MainActivity.name_of_player2);
	        			  localIntent.putExtras(b4);
  	      		          startActivity(localIntent);
  	      		          System.exit(0);
  	        		  }
        		   }
        		}, 1000);
        	
        	break;    
        }
	}
    }
	   
    public class MyCounter extends CountDownTimer{
 
        public MyCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
 
        @Override
        public void onFinish() {
        	
        	tv2.setText(" ");
            if(MainActivity.locallyStoredQuest == true){
            	
            	mReadJsonData();
            	
            }else{	
            	
            	RequestTask task = (RequestTask) new RequestTask().execute(URI);
                progressDialog = new ProgressDialog(SecondGameActivity.this);
    			progressDialog.setIndeterminate(true);
    			progressDialog.setCancelable(true);
    			progressDialog.show();
    	        progressDialog.setContentView(R.layout.my_progress_downloading);
            	
            }
            
        	//System.out.println(tv2);
        	
        }
        
 
        @Override
        public void onTick(long millisUntilFinished) {
        	tv2.setText((millisUntilFinished/1000)+"");
            System.out.println("Timer  : " + (millisUntilFinished/1000));
        }
    }
    
    
    
    public static Runnable myThread = new Runnable(){
    	@Override
	    public void run() {
	    	// TODO Auto-generated method stub
		    while (progressValue<100 && StopThread == false){
		    	try{
			    	myHandle.sendMessage(myHandle.obtainMessage());
			    	Thread.sleep(200+(counter*2)*50);
		    	}
		    	catch(Throwable t){
		    	}
	    	}
    	}

	    Handler myHandle = new Handler(){
	
	    	@Override
		    public void handleMessage(Message msg) {
		    	// TODO Auto-generated method stub
		    	progressValue++;
		    	progress.setProgress(progressValue);
		    	if(progressValue>=100){
		    		
			        if(counter<MainActivity.numberOfQuestions){	
			        	byte[] send = timeProgrBarflag.getBytes();
		  	            MainActivity.write(send);
			        	score_A--;
			    		counter = counter+1;
			    		setScore();
						printQuestion();
				        printAnswer();
	        		  }else{
	        			  
	        			  Intent localIntent = new Intent(SecondGameActivity.context, finalScoreTwoPlayers.class);
	        			  localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        			  Bundle b1 = new Bundle();
	        			  b1.putInt("scoreA", score_A);
	        			  localIntent.putExtras(b1);
	        			  Bundle b2 = new Bundle();
	        			  b2.putInt("scoreB", score_B);
	        			  localIntent.putExtras(b2);
	        			  Bundle b3 = new Bundle();
	        			  b3.putString("nameA", MainActivity.name_of_player);
	        			  localIntent.putExtras(b3);
	        			  Bundle b4 = new Bundle();
	        			  b4.putString("nameB", MainActivity.name_of_player2);
	        			  localIntent.putExtras(b4);
	        			  SecondGameActivity.context.startActivity(localIntent);
	      		          System.exit(0);
	        		  }
		    	}
	    	}
    	};
    };
    
    
    
    public static class loopAsyncTask extends AsyncTask<Void, Void, Void> {

    	  @Override
    	  protected void onPostExecute(Void result) {
	    	   // TODO Auto-generated method stub
	    	  printQuestion();
	          printAnswer();
	          progressDialog.dismiss();
    	  }


    	  @Override
    	  protected Void doInBackground(Void... params) {
    	  // TODO Auto-generated method stub
    		while(true){
	  	        if(	MainActivity.DownloadedCompleteA == true){
	  		        break;
	  		        }else{
	  		        	//continue loop
	  		        	continue;
	  		        }
  	        }
    	   return null;
    	  }
    }
    
    
   
}
