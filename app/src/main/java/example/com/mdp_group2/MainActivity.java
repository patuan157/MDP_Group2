package example.com.mdp_group2;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "BluetoothChat";
    private static final boolean D = true;

    LinearLayout mapPanel;
    MapSurface mapSurface;

    Button moveForward;
    Button reverse;
    Button turnRight;
    Button turnLeft;

    private EditText robotRow;
    private EditText robotCol;
    Button initializeRobot;


    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    BluetoothConnect BC;
    FunctionPreference functionPref;
    TextView robotStatus;           // Show Robot Status
    TextView timer1;                // Show Exploration Time

    Button btnF1;                   // F1
    Button btnF2;                   // F2

    Button exploreStart;            // Exploration Start Button
    Button exploreStop;             // Exploration Stop Button
    Button exploreReset;            // Exploration Reset Button

    ToggleButton toggleMode;        // Toggle the "MANUAL" or "AUTO" mode.
    Button btnUpdate;               // Update the map
    long startTime1 = 0;            // Count run time for explore

    float x1;
    float x2;
    float y1;
    float y2;

    Handler timerHandler1 = new Handler();          // Handle Explore run time
    Runnable timerRunnable1 = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime1;
            int seconds = (int) (millis / 1000.0);
            //int minutes = seconds / 60;
            seconds = seconds % 60;

            timer1.setText(String.format("%d:%d", ((int)seconds), ((long)millis%100)));

            timerHandler1.postDelayed(this, 0);
        }
    };

    Handler mapHandler = new Handler();                 // Mode : Manual or Auto
    Runnable mapRunnable = new Runnable() {
        public void run() {                      // Delay : 5(s) until new Map updated
            //btnUpdate.performClick();
            onBtnUpdatePressed(null);

            mapHandler.postDelayed(this, 5000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BC = (BluetoothConnect) getApplication();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, 1);

        mapPanel = (LinearLayout) findViewById(R.id.mapPanel);
        mapSurface = new MapSurface(MainActivity.this);
        mapPanel.addView(mapSurface);
        mapPanel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);

                switch(action) {
                    case (MotionEvent.ACTION_DOWN) :
                        Log.d(TAG,"Action was DOWN");
                        x1 = event.getX();
                        y1 = event.getY();
                        return true;
                    case (MotionEvent.ACTION_UP) :
                        Log.d(TAG,"Action was UP");
                        x2 = event.getX();
                        y2 = event.getY();
                        Direction direction = getDirection(x1,y1,x2,y2);
                        robotStatus.setText(direction.toString());
                        switch(direction.toString()){
                            case "up":
                                mapSurface.moveForward();
                                break;
                            case "down":
                                mapSurface.reverse();
                                break;
                            case "left":
                                mapSurface.turnLeft();
                                break;
                            case "right":
                                mapSurface.turnRight();
                                break;
                        }

                        return true;
                    default :
                        return true;
                }
            }
        });

        moveForward = (Button) findViewById(R.id.moveForward);
        moveForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapSurface.moveForward();
                sendMessage("forward");
                robotStatus.setText(R.string.move_forward);
            }
        });

        reverse = (Button) findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapSurface.reverse();
                sendMessage("reverse");
                robotStatus.setText(R.string.move_reverse);
            }
        });

        turnLeft = (Button) findViewById(R.id.turnLeft);
        turnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapSurface.turnLeft();
                sendMessage("turnleft");
                robotStatus.setText(R.string.turn_left);
            }
        });

        turnRight = (Button) findViewById(R.id.turnRight);
        turnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapSurface.turnRight();
                sendMessage("turnright");
                robotStatus.setText(R.string.turn_right);
            }
        });

        robotCol = (EditText) findViewById(R.id.colPos);
        robotRow = (EditText) findViewById(R.id.rowPos);
        initializeRobot = (Button) findViewById(R.id.setPos);
        initializeRobot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String x = robotCol.getText().toString();
                String y = robotRow.getText().toString();
                if (checkPos(x,y)) {
                    //sendMessage("sendArena");

                    mapSurface.setCoordinate(Integer.valueOf(x), Integer.valueOf(y));
                    sendMessage("coordinate (" + x + "," + y + ")");
                    // Re-locate the virtual robot as the same with in Android Map

                    int row = Integer.valueOf(x);
                    int col = Integer.valueOf(y);


                    for (int i = 0; i < row-1; i ++){
                        try {
                            Thread.sleep(200);
                            sendMessage("forward");
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    for (int j=0; j<col-1; j++){
                        try {
                            Thread.sleep(200);
                            sendMessage("sr");
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    //mapSurface.setCoordinate(Integer.valueOf(x), Integer.valueOf(y));
                }
            }
        });

        robotStatus = (TextView) findViewById(R.id.robotStatus);
        timer1 = (TextView) findViewById(R.id.timer1);

        btnF1 = (Button) findViewById(R.id.btnF1);
        btnF1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String function_pref_string_f1 = functionPref.getFunctionsDetails().get("f1");
                sendMessage(function_pref_string_f1);
            }
        });

        btnF2 = (Button) findViewById(R.id.btnF2);
        btnF2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String function_pref_string_f2 = functionPref.getFunctionsDetails().get("f2");
                sendMessage(function_pref_string_f2);
            }
        });

        exploreStart = (Button) findViewById(R.id.btnExploreStart);
        exploreStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("beginExplore");
                robotStatus.setText("exploring");
                startTime1 = System.currentTimeMillis();
                timerHandler1.postDelayed(timerRunnable1, 0);
                exploreStart.setEnabled(false);
                exploreStop.setEnabled(true);
            }
        });

        exploreStop = (Button) findViewById(R.id.btnExploreStop);
        exploreStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("STOP");
                timerHandler1.removeCallbacks(timerRunnable1);
                exploreStart.setEnabled(true);
                exploreStop.setEnabled(false);
            }
        });

        exploreReset = (Button) findViewById(R.id.btnExploreReset);
        exploreReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("RESET");
                robotStatus.setText("reset");
                timer1.setText(R.string.default_time);
            }
        });
        toggleMode = (ToggleButton) findViewById(R.id.toggleMode);


        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        functionPref = new FunctionPreference(getApplicationContext());
        exploreStop.setEnabled(false);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bluetooth_connect:
                Intent bluetoothConnectIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(bluetoothConnectIntent, 1);
                return true;
            case R.id.bluetooth_chat:
                Intent bluetoothChatIntent = new Intent(this, BluetoothChat.class);
                startActivity(bluetoothChatIntent);
                return true;
            case R.id.buttons_config:
                Intent functionButtonsConfig = new Intent(this, PreferenceActivity.class);
                startActivity(functionButtonsConfig);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(D) Log.e(TAG, "++ ON START ++");


        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null) //setupChat();
            {
                mChatService = new BluetoothChatService(this, mHandler);
            }
        }
    }


    @Override
    protected synchronized void onResume() {
        super.onResume();
        if (D) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }

        if (BC.getBluetoothConnectedThread() != null) {
            //mHandler = BC.getHandler();
            mChatService = BC.getBluetoothConnectedThread();
            //mHandler = BC.getHandler();
            mChatService.setHandler(mHandler);
            mHandler = BC.getHandler();
        }
    }
    @Override
    public synchronized void onPause() {
        super.onPause();
        if (D) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D) Log.e(TAG, "-- ON STOP --");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (D) Log.e(TAG, "--- ON DESTROY ---");

        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();

    }

    private final void setStatus(int resId) {
        //final ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setSubtitle(resId);
    }

    private final void setStatus(CharSequence subTitle) {
        //final ActionBar actionBar = getActionBar();
        getSupportActionBar().setSubtitle(subTitle);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            setStatus(getString(R.string.title_connected_to, mConnectedDeviceName));
                            BC.setDeviceName(mConnectedDeviceName);
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    //byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    //String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    //mapSurface.decodeAction(readMessage);

                    // Display Robot Status or decode the string for map display
                    if (readMessage.contains("moving forward")) {
                        robotStatus.setText(R.string.moving_forward);
                    } else if (readMessage.contains("reversing")) {
                        robotStatus.setText(R.string.reversing);
                    } else if (readMessage.contains("left")) {
                        robotStatus.setText(R.string.turing_left);
                    } else if (readMessage.contains("right")) {
                        robotStatus.setText(R.string.turing_right);
                    } else if (readMessage.contains("exploring")){
                        robotStatus.setText(R.string.exploring);
                    } else {
                        // Decode action from robot to send back to the Android Tablet
                        // Message contain GRID mean arenaInfo
                        if (readMessage.contains("GRID"))
                            mapSurface.decodeMessage(readMessage);
                    }


                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public boolean checkPos(String x, String y){
        if (x != null && y != null){
            try{
                int col = Integer.valueOf(x);
                int row = Integer.valueOf(y);
                return ((0 < col && col < 13) && (0 < row && row < 18));
            } catch(Exception e){
                return false;
            }
        }
        return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    //setupChat();
                    mChatService = new BluetoothChatService(this, mHandler);
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);


        System.out.println("BLUETOOTHCONNECT " + BC);
        if (BC.getBluetoothConnectedThread() == null) {
            BC.setBluetoothConnectedThread(mChatService, mHandler);

            System.out.println("BLUETOOTHCONNECT " + "entered ");
        } else {
            System.out.println("BLUETOOTHCONNECT " + "problem");
        }
    }

    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }

    public void onToggleBtnUpdatePressed(View view) {
        if(toggleMode.isChecked()) {
            //isChecked == true == auto
            btnUpdate.setEnabled(false);
            mapHandler.post(mapRunnable);
        } else {
            //isChecked == false == manual
            btnUpdate.setEnabled(true);
            mapHandler.removeCallbacks(mapRunnable);

        }
    }

    public void onBtnUpdatePressed(View view) {
        //request map from rpi/pc
        sendMessage("sendArena");
    }

    // Sensor part

    /**
     * Given two points in the plane p1=(x1, x2) and p2=(y1, y1), this method
     * returns the direction that an arrow pointing from p1 to p2 would have.
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the direction
     */
    public Direction getDirection(float x1, float y1, float x2, float y2){
        double angle = getAngle(x1, y1, x2, y2);
        return Direction.get(angle);
    }

    /**
     *
     * Finds the angle between two points in the plane (x1,y1) and (x2, y2)
     * The angle is measured with 0/360 being the X-axis to the right, angles
     * increase counter clockwise.
     *
     * @param x1 the x position of the first point
     * @param y1 the y position of the first point
     * @param x2 the x position of the second point
     * @param y2 the y position of the second point
     * @return the angle between two points
     */
    public double getAngle(float x1, float y1, float x2, float y2) {

        double rad = Math.atan2(y1-y2,x2-x1) + Math.PI;
        return (rad*180/Math.PI + 180)%360;
    }


    public enum Direction{
        up,
        down,
        left,
        right;

        /**
         * Returns a direction given an angle.
         * Directions are defined as follows:
         *
         * Up: [45, 135]
         * Right: [0,45] and [315, 360]
         * Down: [225, 315]
         * Left: [135, 225]
         *
         * @param angle an angle from 0 to 360 - e
         * @return the direction of an angle
         */
        public static Direction get(double angle){
            if(inRange(angle, 45, 135)){
                return Direction.up;
            }
            else if(inRange(angle, 0,45) || inRange(angle, 315, 360)){
                return Direction.right;
            }
            else if(inRange(angle, 225, 315)){
                return Direction.down;
            }
            else{
                return Direction.left;
            }

        }

        /**
         * @param angle an angle
         * @param init the initial bound
         * @param end the final bound
         * @return returns true if the given angle is in the interval [init, end).
         */
        private static boolean inRange(double angle, float init, float end){
            return (angle >= init) && (angle < end);
        }
    }
}

