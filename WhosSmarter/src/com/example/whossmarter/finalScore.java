package com.example.whossmarter;


import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class finalScore extends Activity
{
	 protected int _splashTime = 5000; 
	 public TextView score;
     private Thread splashTread;
     public ImageView first,second,third;
     
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.finalscore);
        Bundle b = getIntent().getExtras();
        int value = b.getInt("key", 0);
        
        score  = (TextView)findViewById(R.id.textView1);
		score.setText(String.valueOf(value*20));
                
		if((value*20)<= 0){
			first = (ImageView)findViewById(R.id.imageView1);
        	first.setBackgroundResource(R.drawable.thumbsdown);
		}
		
		if((value*20) > 0 && (value*20) <= 30){
			second = (ImageView)findViewById(R.id.imageView1);
        	second.setBackgroundResource(R.drawable.what);
		}
		
		if((value*20) > 30 ){
			third = (ImageView)findViewById(R.id.imageView1);
        	third.setBackgroundResource(R.drawable.thumbsup);
		}
		
       /* // thread for displaying the SplashScreen
        splashTread = new Thread() {
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