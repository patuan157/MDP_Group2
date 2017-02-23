package example.com.mdp_group2;

import android.app.Application;
import android.os.Handler;


public class BluetoothConnect extends Application{

    private BluetoothChatService mBluetoothConnectedThread;
    private Handler mHandler;
    private String device_name;
    private String device_id;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public BluetoothChatService getBluetoothConnectedThread() {
        return mBluetoothConnectedThread;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void setBluetoothConnectedThread(BluetoothChatService mBluetoothConnectedThread, Handler mHandler) {
        this.mBluetoothConnectedThread = mBluetoothConnectedThread;
        this.mHandler = mHandler;
    }

    //get device name/id
    public void setDeviceName(String device_name) {
        this.device_name = device_name;
    }

    public void setDeviceID(String device_id) {
        this.device_id = device_id;
    }

    public String getDeviceName() {
        return device_name;
    }

    public String getDeviceID() {
        return device_id;
    }
}
