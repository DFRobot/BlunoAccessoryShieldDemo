package com.example.bluno;

import com.example.bluno.BleCmd;
import com.example.bluno.ProgressWheel;
import com.example.bluno.DeviceControlActivity;
import com.larswerkman.colorpicker.ColorPicker;
import com.larswerkman.colorpicker.ColorPicker.OnColorChangedListener;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class BLUNOActivity extends BlunoLibrary {
	
    public final static String TAG = DeviceControlActivity.class.getSimpleName();

    private connectionStateEnum mConnectionState=connectionStateEnum.isNull;
	private PlainProtocol mPlainProtocol= new PlainProtocol();
	private ProgressWheel progressWheel;
	private Typeface txtotf;
	private boolean isColorChange=false;
	private ImageView titleImageView;
	private ImageView ledImage = null;
	private ImageView joystickImage = null;
	private ImageView PotentiometerImage = null;
	private ImageView arduinoinputdispArea = null;
	private EditText oledSubmitEditText = null;
	private me.imid.view.SwitchButton buzzerSwitch, relaySwitch;
	private ImageView oledSubmitButton,oledClearButton;
	private TextView analogTextDisp;
    private TextView txtTemp;
    private TextView txtHumidity;
	private ColorPicker picker;
    
    public static final int LEDMode=0;
    public static final int RockerMode=1;
    public static final int KnobMode=2;
    private byte Modestates = LEDMode;

	private static Handler receivedHandler = new Handler();
	private Runnable PotentiometerRunnable = new Runnable() {
		@Override
		public void run() {
			if(Modestates == KnobMode)
			{
				serialSend(mPlainProtocol.write(BleCmd.Knob));
				System.out.println("update BleCmdReadPotentiometer");
			}
			receivedHandler.postDelayed(PotentiometerRunnable, 50);
		}
	};
	
	
	private Runnable temperatureRunnable = new Runnable() {
		
		@Override
		public void run() {
			serialSend(mPlainProtocol.write(BleCmd.Temperature));
			System.out.println("update temperature");
			receivedHandler.postDelayed(temperatureRunnable, 1000);
		}
	};
	private Runnable humidityRunnable = new Runnable() {
		
		@Override
		public void run() {
			serialSend(mPlainProtocol.write(BleCmd.Humidity));
			System.out.println("update humidity");
			receivedHandler.postDelayed(humidityRunnable, 1000);
		}
	};

	private boolean isLastSwitchOn=false;
	private Runnable colorRunnable = new Runnable() {
		
		@Override
		public void run() {
			if(Modestates == LEDMode)
			{
				if(picker.mIsSwitchOn)
				{
					if(isColorChange || (isLastSwitchOn==false))
					{
						serialSend(mPlainProtocol.write(BleCmd.RGBLed,
									((picker.getColor() & 0x00ff0000)>>16),
									((picker.getColor() & 0x0000ff00)>>8),
									((picker.getColor() & 0x000000ff)>>0)
									));
					}
					isColorChange=false;
					isLastSwitchOn=true;
				}else{
					if(isLastSwitchOn)
					{
						serialSend(mPlainProtocol.write(BleCmd.RGBLed,0,0,0));
					}
					isLastSwitchOn=false;
				}
			}
//			System.out.println("update color");
			receivedHandler.postDelayed(colorRunnable, 50);
		}
	};
	

	
	public void onSerialReceived(String theString) {
		System.out.println("displayData "+theString);
    	
    	mPlainProtocol.mReceivedframe.append(theString) ;
    	System.out.print("mPlainProtocol.mReceivedframe:");
    	System.out.println(mPlainProtocol.mReceivedframe.toString());

    	while(mPlainProtocol.available())
    	{
        	System.out.print("receivedCommand:");
        	System.out.println(mPlainProtocol.receivedCommand);
    		
    		if(mPlainProtocol.receivedCommand.equals(BleCmd.Rocker))
    		{
    			if(Modestates == RockerMode)
        		{
            		System.out.println("received Rocker");
            		
            		switch(mPlainProtocol.receivedContent[0]){
		        	case 0:	//None input
		        		arduinoinputdispArea.setImageResource(R.drawable.inputbutton_none);
		        		break;
		        	case 1:	//center button pressed 
		        		arduinoinputdispArea.setImageResource(R.drawable.inputbutton_right);
		        		break;
		        	case 2:	//up button pressed 
		        		arduinoinputdispArea.setImageResource(R.drawable.inputbutton_up);
		        		break;
		        	case 3:	//left button pressed 
		        		arduinoinputdispArea.setImageResource(R.drawable.inputbutton_left);
		        		break;
		        	case 4:	//down button pressed 
		        		arduinoinputdispArea.setImageResource(R.drawable.inputbutton_down);
		        		break;
		        	case 5:	//right button pressed 
		        		arduinoinputdispArea.setImageResource(R.drawable.inputbutton_center);
		        		break;
		        	default:
		        		Log.e(getLocalClassName(), "Unkown joystick state: " + mPlainProtocol.receivedContent[0]);
		        		break;
		        	}
            	}
    		}
    		else if(mPlainProtocol.receivedCommand.equals(BleCmd.Temperature))
    		{
        		System.out.println("received Temperature");
        		txtTemp.setText(mPlainProtocol.receivedContent[0] + "¡ C");
    		}
        	else if(mPlainProtocol.receivedCommand.equals(BleCmd.Humidity)){
        		System.out.println("received Humidity");
        		txtHumidity.setText(mPlainProtocol.receivedContent[0] + " %");
        	}
        	else if(mPlainProtocol.receivedCommand.equals(BleCmd.Knob)){
        		System.out.println("received Knob");            
        		float pgPos = mPlainProtocol.receivedContent[0] / 3.75f;//Adjust display value to the angle
        		progressWheel.setProgress(Math.round(pgPos));
        		analogTextDisp.setText(String.valueOf(mPlainProtocol.receivedContent[0]));
        	}
    	}
		
	}
    
    //configure the buzzer switch and the relay switch 
	private void controlSwitch() {
		buzzerSwitch = (me.imid.view.SwitchButton)this.findViewById(R.id.buzzerSwitch);
		buzzerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            // The toggle is enabled
		        	serialSend(mPlainProtocol.write(BleCmd.Buzzer, 1));
		        } else {
		            // The toggle is disabled
		        	serialSend(mPlainProtocol.write(BleCmd.Buzzer, 0));
		        }
		    }
		});
		
		
		relaySwitch = (me.imid.view.SwitchButton)this.findViewById(R.id.relaySwitch);
		relaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		            // The toggle is enabled
		        	serialSend(mPlainProtocol.write(BleCmd.Relay, 1));

		        } else {
		            // The toggle is disabled
		        	serialSend(mPlainProtocol.write(BleCmd.Relay, 0));

		        }
		    }
		});
	}

	//configure the oled Submit component
	private void oledSubmitEditArea() {
		oledSubmitEditText = (EditText) this.findViewById(R.id.editArea);
		oledSubmitEditText.setBackgroundResource(R.drawable.edittext);
		
		oledSubmitEditText.setTypeface(txtotf);
		//Clear testing text
		oledSubmitEditText.setText("");
		oledSubmitEditText.setOnEditorActionListener(new OnEditorActionListener(){

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				
				serialSend(mPlainProtocol.write(BleCmd.Disp + oledSubmitEditText.getText(), 0,0));
				return false;
			}
		});

		oledSubmitButton = (ImageView)this.findViewById(R.id.uploadbutton);
		oledSubmitButton.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
				Log.i(getLocalClassName(),"oled submit text");
				//Note: edit BLE submission function here
				serialSend(mPlainProtocol.write(BleCmd.Disp + oledSubmitEditText.getText(), 0,0));
	        }
	    });
		
		oledClearButton = (ImageView)this.findViewById(R.id.clearbutton);
		
//		oledClearButton.setOnTouchListener(new OnTouchListener(){
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				if(event.getActionMasked() == MotionEvent.ACTION_DOWN)
//				{
//					if(mBluetoothLeService!= null && mSCharacteristic != null){
//						mBleCmd = BleCmd.Relay;
//						mSCharacteristic.setValue(mPlainProtocol.write(mBleCmd, 1));
//						mBluetoothLeService.writeCharacteristic(mSCharacteristic);
//					}
//				}
//				if(event.getActionMasked() == MotionEvent.ACTION_UP)
//				{
//					if(mBluetoothLeService!= null && mSCharacteristic != null){
//						mBleCmd = BleCmd.Relay;
//						mSCharacteristic.setValue(mPlainProtocol.write(mBleCmd, 0));
//						mBluetoothLeService.writeCharacteristic(mSCharacteristic);
//					}
//				}
//				return false;
//			}
//			
//		});
		
		oledClearButton.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
				Log.i(getLocalClassName(),"Clear Text");
				//Clear text after submission
				serialSend(mPlainProtocol.write(BleCmd.Disp, 0,0));
				oledSubmitEditText.setText("");
	        }
	    });
		
		
	}
	
	//configure the color Picker wheel
	private void CreatePicker() {

		picker = (ColorPicker) findViewById(R.id.picker);
		
		picker.setOnColorChangedListener(new OnColorChangedListener(){

			@Override
			public void onColorChanged(int color) {
				isColorChange=true;
			}
		});
		

	}
    
	//configure the font
	private void FontConfig() {
		
		// Font path
		String fontPath = "fonts/yueregular.otf";
        txtTemp = (TextView) findViewById(R.id.tempdisp);
        txtHumidity = (TextView) findViewById(R.id.humiditydisp);
        
        txtotf = Typeface.createFromAsset(getAssets(), fontPath);
        txtTemp.setTypeface(txtotf);
        txtHumidity.setTypeface(txtotf);
        
        analogTextDisp = (TextView) findViewById(R.id.analogTextDisp);
        analogTextDisp.setTypeface(txtotf);

	}
	
	
	//configure the Image View switching part in the center of the UI
	public void imageConfig(){	

		ledImage = (ImageView)this.findViewById(R.id.ledbutton);
		joystickImage = (ImageView)this.findViewById(R.id.joystickbutton);
		PotentiometerImage = (ImageView)this.findViewById(R.id.potbutton);
		
		arduinoinputdispArea = (ImageView)this.findViewById(R.id.pot_input_Area);
		
		ledImage.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
				Log.i(getLocalClassName(),v.getDrawableState().toString());
				
				ledImage.setImageResource(R.drawable.led_tab_seleted);
				joystickImage.setImageResource(R.drawable.joystick_unselected);
				PotentiometerImage.setImageResource(R.drawable.pot_unseleted);

				// modeManager area - LED controller mode
				picker.setVisibility(View.VISIBLE);
				arduinoinputdispArea.setVisibility(View.INVISIBLE);
				analogTextDisp.setVisibility(View.INVISIBLE);

				Modestates = LEDMode;

				progressWheel.setVisibility(View.INVISIBLE);
				
	    		isLastSwitchOn=false;

				if(mConnected)
				{
		    		receivedHandler.post(colorRunnable);
		    		receivedHandler.removeCallbacks(PotentiometerRunnable);
				}

	        }
	    });
		joystickImage.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
				Log.i(getLocalClassName(),v.getDrawableState().toString());
				
				ledImage.setImageResource(R.drawable.led_tab_unseleted);
				joystickImage.setImageResource(R.drawable.joystick_selecte);
				PotentiometerImage.setImageResource(R.drawable.pot_unseleted);

				// modeManager area - joystick state display Mode
				picker.setVisibility(View.INVISIBLE);
        		arduinoinputdispArea.setImageResource(R.drawable.inputbutton_none);
				arduinoinputdispArea.setVisibility(View.VISIBLE);
				analogTextDisp.setVisibility(View.INVISIBLE);
				// check the testingThread function for the display control code
				Modestates = RockerMode;
				
				progressWheel.setVisibility(View.INVISIBLE);
				
				if(mConnectionState==connectionStateEnum.isConnected)
				{
		    		receivedHandler.removeCallbacks(colorRunnable);
		    		serialSend(mPlainProtocol.write(BleCmd.RGBLed,0,0,0));
		    		receivedHandler.removeCallbacks(PotentiometerRunnable);
				}
	        }
	    });
		
		PotentiometerImage.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
				Log.i(getLocalClassName(),v.getDrawableState().toString());
				
				ledImage.setImageResource(R.drawable.led_tab_unseleted);
				picker.setVisibility(View.INVISIBLE);
				
				joystickImage.setImageResource(R.drawable.joystick_unselected);
				
				PotentiometerImage.setImageResource(R.drawable.pot_selected);
				
        		arduinoinputdispArea.setImageResource(R.drawable.potmeter);

				arduinoinputdispArea.setVisibility(View.INVISIBLE);

				// modeManager area - joystick state display Mode
				picker.setVisibility(View.INVISIBLE);
				arduinoinputdispArea.setVisibility(View.VISIBLE);
				analogTextDisp.setVisibility(View.VISIBLE);
				// check the testingThread function for the display control code
				Modestates = KnobMode;
				
				//Add-ons progress wheel effect
				progressWheel.setVisibility(View.VISIBLE);
				
				//circleBounds.width()/2) + rimWidth + paddingLeft, 
				//(circleBounds.height()/2) + rimWidth + paddingTop, 
				//circleRadius
				if(mConnectionState==connectionStateEnum.isConnected)
				{
		    		receivedHandler.post(PotentiometerRunnable);
		    		receivedHandler.removeCallbacks(colorRunnable);
		    		serialSend(mPlainProtocol.write(BleCmd.RGBLed,0,0,0));
				}
	        }
	    });
	}
	
	//configure the progress Wheel of the Potentiometer
	private void progressWheelConfig() {
		progressWheel = (ProgressWheel) findViewById(R.id.pw_spinner);
		
	}

	//configure the title image which shows the connection state
	void titleImageConfig()
	{
        titleImageView =  (ImageView)findViewById(R.id.title_image_view);
        titleImageView.setImageResource(R.drawable.title_scan);
        titleImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				buttonScanOnClickProcess();
			}
		});
	}
	

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("BLUNOActivity onCreate");
		setContentView(R.layout.activity_bluno);
		
		//set the Baudrate of the Serial port
		serialBegin(115200);
		
        onCreateProcess();
		imageConfig();
		FontConfig();
		progressWheelConfig();
		titleImageConfig();
		CreatePicker();
		oledSubmitEditArea();
		controlSwitch();
		
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("BlUNOActivity onResume");
		onResumeProcess();		
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		onActivityResultProcess(requestCode, resultCode, data);

		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		System.out.println("BLUNOActivity onPause");
		receivedHandler.removeCallbacks(humidityRunnable); 
		receivedHandler.removeCallbacks(temperatureRunnable); 
		receivedHandler.removeCallbacks(colorRunnable); 
		receivedHandler.removeCallbacks(PotentiometerRunnable);
        onPauseProcess();
	}
	
	protected void onStop() {
		super.onStop();
		onStopProcess();
		System.out.println("MiUnoActivity onStop");
		

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("MiUnoActivity onDestroy");
        onDestroyProcess();

	}

	@Override
	public void onConectionStateChange(connectionStateEnum theConnectionState) {
	
		mConnectionState=theConnectionState;
		switch (mConnectionState) {
		case isScanning:
	        titleImageView.setImageResource(R.drawable.title_scanning);
			break;

		case isConnected:
	        titleImageView.setImageResource(R.drawable.title_connected);
			receivedHandler.post(humidityRunnable); 
			receivedHandler.post(temperatureRunnable); 
			switch (Modestates) {
			case LEDMode:
				receivedHandler.post(colorRunnable); 
				break;
			case RockerMode:
				
				break;
			case KnobMode:
				receivedHandler.post(PotentiometerRunnable);
				break;

			default:
				break;
			}
			
			break;
		case isConnecting:
	        titleImageView.setImageResource(R.drawable.title_connecting);
			break;
		case isToScan:
	        titleImageView.setImageResource(R.drawable.title_scan);
	        
			receivedHandler.removeCallbacks(humidityRunnable); 
			receivedHandler.removeCallbacks(temperatureRunnable); 
			receivedHandler.removeCallbacks(colorRunnable); 
			receivedHandler.removeCallbacks(PotentiometerRunnable);
			break;
		default:
			break;
		}
	}
	

	
	
}
