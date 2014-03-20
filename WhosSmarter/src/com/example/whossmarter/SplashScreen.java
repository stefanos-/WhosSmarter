package com.example.whossmarter;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreen extends Activity
{
	 protected int _splashTime = 5000; 
     
     private Thread splashTread;
     
	/** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashscreen);
        
        final SplashScreen sPlashScreen = this; 
        
        // thread for displaying the SplashScreen
        splashTread = new Thread() {
            @Override
            public void run() {
                try {                       
                    synchronized(this){
                            wait(_splashTime);
                    }
                    
                } catch(InterruptedException e) {} 
                finally {
                    finish();
                    
                    Intent i = new Intent();
                    i.setClass(sPlashScreen, MainActivity.class);
                    startActivity(i);
                    
                    //stop();
                }
            }
        };
        
        splashTread.start();
    }
}