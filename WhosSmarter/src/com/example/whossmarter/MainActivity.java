package com.example.whossmarter;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity  {

	// Debugging
    private static final String TAG = "BluetoothChat";
	
    public static Context context;
    
    static TextView txtData;
    
	private TextView mTitle1,mTitle2;

    // Unique UUID for this application (generated from the web)
 	private static final UUID MY_UUID = UUID.fromString("321cb8fa-9066-4f58-935e-ef55d1ae06ec");
 	//Friendly name to match while discovering
 	private static final String SEARCH_NAME = "WhosSmarterApi";
 	BluetoothAdapter mBtAdapter;
 	BluetoothSocket mBtSocket;
 	private static final int REQUEST_ENABLE = 1;
	private static final int REQUEST_DISCOVERABLE = 2;
	
    public static ProgressDialog progressDialog1;
	public static ProgressDialog progressDialog2;
	
	public static int numberOfQuestions;

	public static boolean locallyStoredQuest;
	
    //---thread for running the server socket---
    ServerThread serverThread;
    
  //---thread for connecting to the client socket---
    ConnectToServerThread connectToServerThread;
    
    
    public static ConnectedThread mConnectedThread;
    
    Handler mHandler = new Handler();
    String Message;
    static String temp;
    public static String flag = "C";
	public static String startflag = "s";
	public static String positiveflag = "p";
	public static String negativeflag = "n";
	public static String alreadyPressedFlag = "y";
	public static String NameAflag = "i";
	public static String lastQuestionflag = "j";
	public static String Flagback = "b";
	public static String flagLocal = "o";
	public int IsNotConnected;
	Button listenButton, scanButton, startGameButton, helpButton;
	
	public static boolean DownloadedCompleteA;
	public static boolean Player1HasName;
	public static boolean ButtonAlreadyPressedB;
	
	
	public static int not_enter, not_enter2;

	public static int numberOfPlayers;
    
	Button mButton, mButton1,ok,rButton1,tenQuest, wifi, localQuest, cancel, exit;
		
    Dialog dialog, dialog2, dialog3, dialog4;
    
    public static String name_of_player;
    public static String name_of_player2;
    public static ProgressBar mTitleProgressBar;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_main);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		mTitleProgressBar = (ProgressBar) findViewById(R.id.title_progress_bar);
		MainActivity.context = getApplicationContext();
		
		
		not_enter = 2;
		not_enter2 = 2;
		locallyStoredQuest = false;
		numberOfQuestions = 10-1;
		numberOfPlayers = 1;
		Player1HasName = false;
		
		Button mButton=(Button)findViewById(R.id.Button01);
		mButton.setText("Start Game / 1 Player");
		
		dialog = new Dialog(MainActivity.this);
		dialog = new Dialog(MainActivity.this,android.R.style.Theme_Translucent);
		dialog.setCancelable(true);
		dialog.setContentView(R.layout.dialog);
		
		
		txtData = (TextView) findViewById(R.id.textData);
		
				
        // Set up the custom title
        mTitle1 = (TextView) findViewById(R.id.title_left_text);
        mTitle1.setText(R.string.app_name);
        mTitle2 = (TextView) findViewById(R.id.title_right_text);
        mTitle2.setText("not connected");
		
		
        //Check the system status
  		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
  		if(mBtAdapter == null) {
  			Toast.makeText(this, "Bluetooth is not supported.", Toast.LENGTH_SHORT).show();
  			finish();
  			return;
  		}
  		if (!mBtAdapter.isEnabled()) {
  			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
  			startActivityForResult(enableIntent, REQUEST_ENABLE);
  		}
		        
	}
	
	
	
	@Override
	public void onResume() {
		super.onResume();
		//Register the activity for broadcast intents
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter);
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		registerReceiver(mReceiver, filter);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			if(mBtSocket != null) {
				mBtSocket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//---stop the thread running---
        if (serverThread!=null) serverThread.cancel();
        if (connectToServerThread!=null) connectToServerThread.cancel();

	}
	
	
	@Override
    public void onBackPressed() {
            //super.onBackPressed();
            
            final Dialog dialog4 = new Dialog(MainActivity.this,android.R.style.Theme_Translucent);
	        dialog4.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        dialog4.setCancelable(true);
	        dialog4.setContentView(R.layout.exitdialog);
	        dialog4.show();
	        
	        Button exit = (Button)dialog4.findViewById(R.id.exit);
	        exit.setOnClickListener(new View.OnClickListener() {
	         
		        @Override
		        public void onClick(View v) {
		        	
		        	dialog4.dismiss();
		            System.exit(0);
		        	
		        }
	        });     
	        
	        Button cancel = (Button)dialog4.findViewById(R.id.cancel);
	        cancel.setOnClickListener(new View.OnClickListener() {
	         
		        @Override
		        public void onClick(View v) {
		        
		        	dialog4.dismiss();
	
		        }
	        });     
            
    }
	
	public void exit(View view){
		final Dialog dialog4 = new Dialog(MainActivity.this,android.R.style.Theme_Translucent);
        dialog4.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog4.setCancelable(true);
        dialog4.setContentView(R.layout.exitdialog);
        dialog4.show();
        
        Button exit = (Button)dialog4.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
   
	        @Override
	        public void onClick(View v) {
	        	dialog4.dismiss();
	            System.exit(0);
	        }
        });     
        
        Button cancel = (Button)dialog4.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
         
	        @Override
	        public void onClick(View v) {
	        	dialog4.dismiss();
	        }
        });     
	}
	
	
	public void startGame(View view){
		internet_connection();
		if ((IsNotConnected != 1) && (IsNotConnected == 0)){
			
			if(numberOfPlayers == 1){
				final Dialog dialog = new Dialog(MainActivity.this,android.R.style.Theme_Translucent);
		        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		        dialog.setCancelable(true);
		        dialog.setContentView(R.layout.dialog);
		        dialog.show();
		        
		        Button ok = (Button)dialog.findViewById(R.id.ok);
		        ok.setOnClickListener(new View.OnClickListener() {
		         
			        @Override
			        public void onClick(View v) {
			        	EditText edit=(EditText)dialog.findViewById(R.id.name_of_player);
			            String text=edit.getText().toString();
			            name_of_player= text;
			            if(name_of_player.length() == 0){
			            	name_of_player = "unknown";
			            }
			            if(name_of_player.length() == 1){
			            	name_of_player = name_of_player + "_";
			            }
			        	continueGame();
		
			        }
		        });     
			}
			
			if(numberOfPlayers == 2){
				final Dialog dialog = new Dialog(MainActivity.this,android.R.style.Theme_Translucent);
		        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		        dialog.setCancelable(true);
		        dialog.setContentView(R.layout.dialog);
		        dialog.show();
		        
		        Button ok = (Button)dialog.findViewById(R.id.ok);
		        ok.setOnClickListener(new View.OnClickListener() {
		         
			        @Override
			        public void onClick(View v) {
			        	//byte[] send = NameAflag.getBytes();
				        //write(send);
			        	EditText edit=(EditText)dialog.findViewById(R.id.name_of_player);
			            String text=edit.getText().toString();
			            name_of_player= text;
			            if(name_of_player.length() == 0){
			            	name_of_player = "unknown";
			            }
			            if(name_of_player.length() == 1){
			            	name_of_player = name_of_player + "_";
			            }
			            byte[] send2 = name_of_player.getBytes();
				        write(send2);
			        	dialog.dismiss();
			        	progressDialog2 = new ProgressDialog(MainActivity.this);
			    		progressDialog2.setIndeterminate(true);
			    		progressDialog2.setCancelable(true);
			    		progressDialog2.show();
			            progressDialog2.setContentView(R.layout.my_progress_wait);
			        	new loopAsyncTaskMain().execute();
			        	
			        }
		        });  
			}
		}
		
		
		
		
	}
	
	
	public void continueGame(){
			
			if(numberOfPlayers == 1){
		        	
		        Intent localIntent = new Intent(MainActivity.this, SecondGameActivityOnePlayer.class);
		        startActivity(localIntent);
			        
			}
			
			if(numberOfPlayers == 2){
				
		        //buffer = null;
		        byte[] send = startflag.getBytes();
		        write(send);
		        //Intent localIntent = new Intent(MainActivity.this, SecondGameActivity.class);
		        //startActivity(localIntent);
			      	
			}
		}
	
	
	public void options(View view){
		final Dialog dialog = new Dialog(MainActivity.this,android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.option);
        dialog.show();
        
        Button tenQuest = (Button)dialog.findViewById(R.id.tenQuest);
        tenQuest.setOnClickListener(new View.OnClickListener() {
         
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        	numberOfQuestions = 10-1;
	        }
        });     
        
        Button twentyQuest = (Button)dialog.findViewById(R.id.twentyQuest);
        twentyQuest.setOnClickListener(new View.OnClickListener() {
         
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        	numberOfQuestions = 20-1;
	        }
        });     
	}
	
	
	public void DiscoverDevice(View view){
		
		if((not_enter % 2) != 0 ){
			
			numberOfPlayers = 1;
			//if (connectToServerThread!=null) connectToServerThread.cancel();
			mTitle2.setText("not connected");
			Button mButton=(Button)findViewById(R.id.Button01);
            mButton.setText("Start Game / 1 Player");
			Button mButton1=(Button)findViewById(R.id.Button02);
          	mButton1.setText("Discover Devices");
          	//---------debugging------------//Toast.makeText(this, "D",Toast.LENGTH_SHORT).show();
		}
		
		if((not_enter % 2) == 0){
			//---------debugging------------//Toast.makeText(this, "S",Toast.LENGTH_SHORT).show();
			if(numberOfPlayers == 1){
	        
				mBtAdapter.startDiscovery();
				mTitle2.setText("searching");
				//progressDialog1 = new ProgressDialog(MainActivity.this);
				//progressDialog1.setIndeterminate(true);
				//progressDialog1.setCancelable(true);
				//progressDialog1.show();
		        //progressDialog1.setContentView(R.layout.my_progress);
				mTitleProgressBar.setVisibility(View.VISIBLE);
		        Button mButton1=(Button)findViewById(R.id.Button02);
	          	mButton1.setText("Stop Searching");
	          	
			}
			
			if(numberOfPlayers == 2){
				
				//---stop the thread running---
		        //if (serverThread!=null) serverThread.cancel();
		        //connectToServerThread.cancel();
				//mConnectedThread.cancel();
				Button mButton=(Button)findViewById(R.id.Button01);
	            mButton.setText("Start Game / 1 Player");
				mTitle2.setText("not connected");
				Button mButton1=(Button)findViewById(R.id.Button02);
	          	mButton1.setText("Discover Devices");
				
			}
		}
	not_enter++;
	}

	
	public void MakeDiscoverable(View view){
		if((not_enter2 % 2) == 0){
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivityForResult(discoverableIntent, REQUEST_DISCOVERABLE);
		}else{
			startListening();
		}
	}
		
		
	public void internet_connection()
	  {
	    NetworkInfo localNetworkInfo = ((ConnectivityManager)getSystemService("connectivity")).getActiveNetworkInfo();
	    if ((localNetworkInfo != null) && (localNetworkInfo.isConnected()))
	    {
	      IsNotConnected = 0;
	      return;
	    }else{
	    	IsNotConnected = 1;
	    	//Toast.makeText(this, "Network Not Available",Toast.LENGTH_SHORT).show();
	    	final Dialog dialog3 = new Dialog(MainActivity.this,android.R.style.Theme_Translucent);
	        dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        dialog3.setCancelable(true);
	        dialog3.setContentView(R.layout.internet_connection);
	        dialog3.show();
	        
	        Button wifi = (Button)dialog3.findViewById(R.id.wifi);
	        wifi.setOnClickListener(new View.OnClickListener() {
	         
		        @Override
		        public void onClick(View v) {
		        	
		        	dialog3.dismiss();
		        	//Wi-Fi Setting
		        	startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
		        	
		        }
	        });     
	        
	        Button localQuest = (Button)dialog3.findViewById(R.id.localQuest);
	        localQuest.setOnClickListener(new View.OnClickListener() {
	         
		        @Override
		        public void onClick(View v) {
		        
		        	dialog3.dismiss();
		        	
		        	locallyStoredQuest = true;	
		        	if (locallyStoredQuest == true){
						
						if(numberOfPlayers == 1){
							final Dialog dialog = new Dialog(MainActivity.this,android.R.style.Theme_Translucent);
					        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					        dialog.setCancelable(true);
					        dialog.setContentView(R.layout.dialog);
					        dialog.show();
					        
					        Button ok = (Button)dialog.findViewById(R.id.ok);
					        ok.setOnClickListener(new View.OnClickListener() {
					         
						        @Override
						        public void onClick(View v) {
						        	EditText edit=(EditText)dialog.findViewById(R.id.name_of_player);
						            String text=edit.getText().toString();
						            name_of_player= text;
						            if(name_of_player.length() == 0){
						            	name_of_player = "unknown";
						            }
						        	continueGame();
					
						        }
					        });     
						}
						
						if(numberOfPlayers == 2){
							byte[] send = flagLocal.getBytes();
			                write(send);
							final Dialog dialog = new Dialog(MainActivity.this,android.R.style.Theme_Translucent);
					        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					        dialog.setCancelable(true);
					        dialog.setContentView(R.layout.dialog);
					        dialog.show();
					        
					        Button ok = (Button)dialog.findViewById(R.id.ok);
					        ok.setOnClickListener(new View.OnClickListener() {
					         
						        @Override
						        public void onClick(View v) {
						        	//byte[] send = NameAflag.getBytes();
							        //write(send);
						        	EditText edit=(EditText)dialog.findViewById(R.id.name_of_player);
						            String text=edit.getText().toString();
						            name_of_player= text;
						            if(name_of_player.length() == 0){
						            	name_of_player = "unknown";
						            }
						            byte[] send2 = name_of_player.getBytes();
							        write(send2);
						        	dialog.dismiss();
						        	progressDialog2 = new ProgressDialog(MainActivity.this);
						    		progressDialog2.setIndeterminate(true);
						    		progressDialog2.setCancelable(true);
						    		progressDialog2.show();
						            progressDialog2.setContentView(R.layout.my_progress_wait);
						        	new loopAsyncTaskMain().execute();
						        	
						        }
					        });  
						}
					}	
		        }
	        });     
	        
	        
	    	}
	  }
	
	//Start a server socket and listen
	private void startListening() {
		
        
		if((not_enter2 % 2) != 0){
			numberOfPlayers = 1;
			if (serverThread!=null) serverThread.cancel();
			mTitle2.setText("not connected");
			Button mButton=(Button)findViewById(R.id.Button01);
            mButton.setText("Start Game / 1 Player");
            mTitleProgressBar.setVisibility(View.INVISIBLE);//to exw allaksei kai edw to progress dialog, tesseris allages sunolika
			Button mButton1=(Button)findViewById(R.id.Button03);
          	mButton1.setText("Discoverable");
          //---------debugging------------//Toast.makeText(this, "D",Toast.LENGTH_SHORT).show();
          	
		}
		
		if((not_enter2 % 2) == 0){
			//---------debugging------------//Toast.makeText(this, "S",Toast.LENGTH_SHORT).show();
	        if(numberOfPlayers == 1){
	        	
	        	mTitle2.setText("listening");
	    		
	    		//progressDialog1 = new ProgressDialog(MainActivity.this);
	    		//progressDialog1.setIndeterminate(true);
	    		//progressDialog1.setCancelable(true);
	    		//progressDialog1.show();
	            //progressDialog1.setContentView(R.layout.my_progress);
	        	mTitleProgressBar.setVisibility(View.VISIBLE);
	            Button mButton1=(Button)findViewById(R.id.Button03);
	          	mButton1.setText("Stop Listening");
	            //---start the socket server---
	            serverThread = new ServerThread(mBtAdapter);
	            serverThread.start();
		        
			}
			
			if(numberOfPlayers == 2){
				
				//---stop the thread running---
		        if (serverThread!=null) serverThread.cancel();
		        //if (connectToServerThread!=null) connectToServerThread.cancel();
				mConnectedThread.cancel();
				Button mButton=(Button)findViewById(R.id.Button01);
	            mButton.setText("Start Game / 1 Player");
				mTitle2.setText("not connected");
				mTitleProgressBar.setVisibility(View.INVISIBLE); //to exw allaksei kai edw to progress dialog, tesseris allages sunolika
				Button mButton1=(Button)findViewById(R.id.Button03);
	          	mButton1.setText("Discoverable");
			}
		
		}
       not_enter2++; 
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
			case REQUEST_ENABLE:
				if(resultCode != Activity.RESULT_OK) {	
					Toast.makeText(this, "Bluetooth Not Enabled.",Toast.LENGTH_SHORT).show();
					finish();
			}
				break;
			case REQUEST_DISCOVERABLE:
				if(resultCode == Activity.RESULT_CANCELED) {
					Toast.makeText(this, "Must be discoverable.",Toast.LENGTH_SHORT).show();
				}else {
				startListening();
			}
				break;
			
			default:
				break;
			}
		}	

	
	
	public class ServerThread extends Thread {
	    //---the server socket---
	    private final BluetoothServerSocket bluetoothServerSocket;
	    String name = mBtAdapter.getName();
	    public ServerThread(BluetoothAdapter mBtAdapter) {
	        BluetoothServerSocket tmp = null;
	        try {
	        	mBtAdapter.setName(SEARCH_NAME);
	            //---UUID must be the same for both the client and the server---
	            tmp = mBtAdapter.listenUsingRfcommWithServiceRecord("BluetoothRecipe", MY_UUID);
	        } catch (IOException e) {
	            Log.d("ServerThread", e.getLocalizedMessage());
	        }
	        bluetoothServerSocket = tmp;
	    }

	    public void run() {
	        BluetoothSocket socket = null;
	        //---keep listening until exception occurs or a socket is returned---
	        while (true) {
	            try {
	                socket = bluetoothServerSocket.accept();
	                //Reset the BT adapter name
	                mBtAdapter.setName(name);
	            } catch (IOException e) {
	                Log.d("ServerThread", e.getLocalizedMessage());
	                break;
	            }
	            //---if a connection was accepted---
	            if (socket != null) {
	            	numberOfPlayers = 2;
	            	mHandler.post(new Runnable(){
	  		            public void run(){
	  		            	mTitle2.setText("connected");
	  		      		    Button mButton=(Button)findViewById(R.id.Button01);
	  		            	mButton.setText("Start Game / 2 Player");
	  		            	Button mButton1=(Button)findViewById(R.id.Button03);
	  		            	mButton1.setText("Disconnect");
	  		            }
  		            });
	            	
	    			mBtSocket = socket;
	    			//---create a separate thread to listen for incoming data---
	    			mConnectedThread = new ConnectedThread(mBtSocket);
	    	        mConnectedThread.start();
	    	        byte[] send = flag.getBytes();
	                write(send);
	            }
	        }
	    }

	    public void cancel() {
	        try {
	            bluetoothServerSocket.close();
	        } catch (IOException e) {
	            Log.d("ServerThread", e.getLocalizedMessage());
	        }
	    }
	}


	// The BroadcastReceiver that listens for discovered devices
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if(TextUtils.equals(device.getName(), SEARCH_NAME)) {
					
					//Matching device found, connect
					mBtAdapter.cancelDiscovery();
					//---if you are already talking to someone...---
			        if (connectToServerThread!=null) {
			            try {
			                //---close the connection first---
			                connectToServerThread.bluetoothSocket.close();
			            } catch (IOException e) {
			                Log.d("MainActivity", e.getLocalizedMessage());
			            }
			        }

			        connectToServerThread = new ConnectToServerThread(device, mBtAdapter);
			        connectToServerThread.start();

				}
			//When discovery is complete
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
				
			}
		}
	};


	public class ConnectToServerThread extends Thread {
	    //private BluetoothAdapter bluetoothAdapter;
	    public BluetoothSocket bluetoothSocket;
	    public ConnectToServerThread(BluetoothDevice device, BluetoothAdapter mBtAdapter) {
	    	BluetoothSocket tmp = null;
	    	//bluetoothAdapter = btAdapter;
	        //---get a BluetoothSocket to connect with the given BluetoothDevice---
	        try {
	            //---UUID must be the same for both the client and the server---
	            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
	        } catch (IOException e) {
	            Log.d("ConnectToServerThread", e.getLocalizedMessage());
	        }
	        bluetoothSocket = tmp;
	    }
	
	    public void run() {
	        //---cancel discovery because it will slow down the connection---
	    	mBtAdapter.cancelDiscovery();
	        try {
	            //---connect the device through the socket. This will
	            // block until it succeeds or throws an exception---
	        	bluetoothSocket.connect();
	 
	        	numberOfPlayers = 2;
	        	mHandler.post(new Runnable(){
  		            public void run(){
  		            	mTitle2.setText("connected");
  		            	Button mButton=(Button)findViewById(R.id.Button01);
  		            	mButton.setText("Start Game / 2 Player");
  		            	Button mButton1=(Button)findViewById(R.id.Button02);
  		            	mButton1.setText("Disconnect");
  		            }
		            });
	            //---create a thread for the communication channel---
	            mConnectedThread = new ConnectedThread(bluetoothSocket);
    	        mConnectedThread.start();
    	        byte[] send = flag.getBytes();
                write(send);
	            
	        } catch (IOException connectException) {
	            //---unable to connect; close the socket and get out---
	            try {
	            	bluetoothSocket.close();
	            } catch (IOException closeException) {
	                Log.d("ConnectToServerThread", closeException.getLocalizedMessage());
	            }
	            return;
	        }
	    }
	
	    public void cancel() {
	        try {
	        	bluetoothSocket.close();
	            if (mConnectedThread!=null) mConnectedThread.cancel();
	        } catch (IOException e) {
	            Log.d("ConnectToServerThread", e.getLocalizedMessage());
	        }
	    }
	}


	public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket sock) {
            Log.d(TAG, "create ConnectedThread");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = sock.getInputStream();
                tmpOut = sock.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            //---buffer store for the stream---
            byte[] buffer = new byte[1024];

            //---bytes returned from read()---
            int bytes;
            // Keep listening to the InputStream while connected
            while (true) {
                try {
                	bytes =  mmInStream.read(buffer);

                    //---update the main activity UI---
                    UIupdater.obtainMessage(0,bytes, -1,buffer).sendToTarget();
  		            
                   
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer); 
            } catch (IOException e) {
                Log.e(TAG, "Exception during write", e);
            }
        }
           
      //---call this from the main Activity to
        // shutdown the connection---
        public void cancel() {
            try {
            	mBtSocket.close();
            } catch (IOException e) {
                Log.d("CommsThread", e.getLocalizedMessage());
            }
        } 
    }
	
	
	
	//---used for updating the UI on the main activity---
    static Handler UIupdater = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int numOfBytesReceived = msg.arg1;
            byte[] buffer = (byte[]) msg.obj;
            //---convert the entire byte array to string---
            String strReceived = new String(buffer);
            //---extract only the actual string received---
            strReceived = strReceived.substring( 0, numOfBytesReceived);
            //---display the text received on the TextView---
            strReceived.trim();
            //---------debugging------------//txtData.setText(txtData.getText().toString() + strReceived);
            flagHandler(strReceived);
        }
    };
	
	
	/*private void displayMessage(String Messag)
	  {
	      Toast.makeText(this, Messag,Toast.LENGTH_SHORT).show();
	  }*/
	
	/**
   * Write to the ConnectedThread in an unsynchronized manner
   * @param out The bytes to write
   * @see ConnectedThread#write(byte[])
   */
  public static void write(byte[] out) {
      // Create temporary object
      ConnectedThread r;
      r = mConnectedThread;
      // Perform the write unsynchronized
      r.write(out);
  }
  
 
	
  private static void flagHandler(String Messag){
	  
	 if(Messag.equals("C")){
	  		
		 //progressDialog1.dismiss();
		 mTitleProgressBar.setVisibility(View.INVISIBLE);
 
	 }  
  	
  	if(Messag.equals("s")){
  		
  		        Intent localIntent = new Intent(MainActivity.context, SecondGameActivity.class);
  		        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		        MainActivity.context.startActivity(localIntent);
  		
  	}
  	if(Messag.equals("n")){
  		ButtonAlreadyPressedB = true;
  		SecondGameActivity.counter = SecondGameActivity.counter+1;
  		if(ButtonAlreadyPressedB == true && SecondGameActivity.ButtonAlreadyPressedA == true){
  			SecondGameActivity.counter = SecondGameActivity.counter-1;
  			byte[] send = alreadyPressedFlag.getBytes();
      		write(send);
  		}
  		SecondGameActivity.score_B--;
  		SecondGameActivity.setScore();
  		SecondGameActivity.printQuestion();
  		SecondGameActivity.printAnswer();
  	}
  	
  	if(Messag.equals("n") && SecondGameActivity.counter > numberOfQuestions){
  		ButtonAlreadyPressedB = true;
  		SecondGameActivity.counter = SecondGameActivity.counter+1;
  		if(ButtonAlreadyPressedB == true && SecondGameActivity.ButtonAlreadyPressedA == true){
  			SecondGameActivity.counter = SecondGameActivity.counter-1;
  			byte[] send = alreadyPressedFlag.getBytes();
      		write(send);
  		}
  		//SecondGameActivity.score_B--;
  		SecondGameActivity.setScore();
  		
  		Intent localIntent = new Intent(SecondGameActivity.context, finalScoreTwoPlayers.class);  	  
  		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  		Bundle b1 = new Bundle();
		b1.putInt("scoreA", SecondGameActivity.score_A);
		localIntent.putExtras(b1);
		Bundle b2 = new Bundle();
		b2.putInt("scoreB", SecondGameActivity.score_B);
		localIntent.putExtras(b2);
		Bundle b3 = new Bundle();
		b3.putString("nameA", name_of_player);
		localIntent.putExtras(b3);
		Bundle b4 = new Bundle();
		b4.putString("nameB", name_of_player2);
		localIntent.putExtras(b4);
	    SecondGameActivity.context.startActivity(localIntent);
	    System.exit(0);
	    
  	}
  	
  	if(Messag.equals("y")){
  		SecondGameActivity.counter = SecondGameActivity.counter-1;
  	}
  	
  	if(Messag.equals("p")){
  		ButtonAlreadyPressedB = true;
  		SecondGameActivity.counter = SecondGameActivity.counter+1;
  		if(ButtonAlreadyPressedB == true && SecondGameActivity.ButtonAlreadyPressedA == true){
  			SecondGameActivity.counter = SecondGameActivity.counter-1;
				byte[] send = alreadyPressedFlag.getBytes();
      		write(send);
  		}
  		SecondGameActivity.score_B++;
  		SecondGameActivity.setScore();
  		SecondGameActivity.printQuestion();
  		SecondGameActivity.printAnswer();
  	}
  	
  	if(Messag.equals("p") && SecondGameActivity.counter > numberOfQuestions){
  		ButtonAlreadyPressedB = true;
  		SecondGameActivity.counter = SecondGameActivity.counter+1;
  		if(ButtonAlreadyPressedB == true && SecondGameActivity.ButtonAlreadyPressedA == true){
  			SecondGameActivity.counter = SecondGameActivity.counter-1;
  			byte[] send = alreadyPressedFlag.getBytes();
      		write(send);
  		}
  		SecondGameActivity.score_B++;
  		SecondGameActivity.setScore();
  		
  		Intent localIntent = new Intent(SecondGameActivity.context, finalScoreTwoPlayers.class);  	  
  		localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
  		Bundle b1 = new Bundle();
		b1.putInt("scoreA", SecondGameActivity.score_A);
		localIntent.putExtras(b1);
		Bundle b2 = new Bundle();
		b2.putInt("scoreB", SecondGameActivity.score_B);
		localIntent.putExtras(b2);
		Bundle b3 = new Bundle();
		b3.putString("nameA", name_of_player);
		localIntent.putExtras(b3);
		Bundle b4 = new Bundle();
		b4.putString("nameB", name_of_player2);
		localIntent.putExtras(b4);
	    SecondGameActivity.context.startActivity(localIntent);
	    System.exit(0);
	    
  	}
  	
  	
  	
  	if(Messag.equals("a")){
  		
  		DownloadedCompleteA = true;
  		
  	}
  	
  	
  	if(Messag.equals("b")){
  		
  		System.exit(0);
  		
  	}
  	
  	
  	if(Messag.equals("o")){
  		
  		locallyStoredQuest = true;	
  		
  	}
  	
  	
  	if(Messag.length()>1){
  		
  		Player1HasName = true;
  		name_of_player2 = Messag;
  		
  	}
  	
  	
  	if(Messag.equals("t")){
  		SecondGameActivity.counter = SecondGameActivity.counter+1;
  		SecondGameActivity.score_B--;
  		SecondGameActivity.setScore();
  		SecondGameActivity.printQuestion();
  		SecondGameActivity.printAnswer();
  	}
  	
  }
  
  
  public class loopAsyncTaskMain extends AsyncTask<Void, Void, Void> {

	  @Override
	  protected void onPostExecute(Void result) {
          progressDialog2.dismiss();
          continueGame();
	  }

	  @Override
	  protected Void doInBackground(Void... params) {
	  // TODO Auto-generated method stub
		while(true){
  	        if(	Player1HasName == true){
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
