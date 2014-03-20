package com.example.whossmarter;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class finalScoreTwoPlayers extends Activity
{
	 protected int _splashTime = 10000; 
	 public TextView score1, score2, nameA, nameB;
     private Thread splashTread;
     public ImageView first,second;
     
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.finalscore2players);
        Bundle b1 = getIntent().getExtras();
        int value1 = b1.getInt("scoreA", 0);
        
        Bundle b2 = getIntent().getExtras();
        int value2 = b2.getInt("scoreB", 0);
        
        Bundle b3 = getIntent().getExtras();
        String value3 = b3.getString("nameA");
        
        Bundle b4 = getIntent().getExtras();
        String value4 = b4.getString("nameB");
        
        if(value1>value2){
        	
        	first = (ImageView)findViewById(R.id.imageView1);
        	first.setBackgroundResource(R.drawable.first);
        	
        	second = (ImageView)findViewById(R.id.imageView2);
        	second.setBackgroundResource(R.drawable.second);
        	
        	nameA  = (TextView)findViewById(R.id.textView2);
    		nameA.setText(value3);
    		
    		nameB  = (TextView)findViewById(R.id.textView4);
    		nameB.setText(value4);
        	
        	score1  = (TextView)findViewById(R.id.textView3);
    		score1.setText(String.valueOf(value1*20));
    		
    		score2  = (TextView)findViewById(R.id.textView5);
    		score2.setText(String.valueOf(value2*20));
        	
        }
        
        if(value2>value1){
        	
        	first = (ImageView)findViewById(R.id.imageView1);
        	first.setBackgroundResource(R.drawable.first);
        	
        	second = (ImageView)findViewById(R.id.imageView2);
        	second.setBackgroundResource(R.drawable.second);
        	
        	nameA  = (TextView)findViewById(R.id.textView2);
    		nameA.setText(value4);
    		
    		nameB  = (TextView)findViewById(R.id.textView4);
    		nameB.setText(value3);
        	
        	score1  = (TextView)findViewById(R.id.textView3);
    		score1.setText(String.valueOf(value2*20));
    		
    		score2  = (TextView)findViewById(R.id.textView5);
    		score2.setText(String.valueOf(value1*20));
        	
        }
        
        
        if(value2 == value1){
        	
        	first = (ImageView)findViewById(R.id.imageView1);
        	first.setBackgroundResource(R.drawable.first);
        	
        	second = (ImageView)findViewById(R.id.imageView2);
        	second.setBackgroundResource(R.drawable.first);
        	
        	nameA  = (TextView)findViewById(R.id.textView2);
    		nameA.setText(value3);
    		
    		nameB  = (TextView)findViewById(R.id.textView4);
    		nameB.setText(value4);
        	
        	score1  = (TextView)findViewById(R.id.textView3);
    		score1.setText(String.valueOf(value2*20));
    		
    		score2  = (TextView)findViewById(R.id.textView5);
    		score2.setText(String.valueOf(value1*20));
        	
        }
        
                
        // thread for displaying the SplashScreen
       /* splashTread = new Thread() {
            @Override
            public void run() {
                try {                       
                    synchronized(this){
                            wait(_splashTime);
                    }
                    
                } catch(InterruptedException e) {} 
                finally {
                    
                    
                    System.exit(0);
                    
                    //stop();
                }
            }
        };
	
        splashTread.start();*/
    }
}