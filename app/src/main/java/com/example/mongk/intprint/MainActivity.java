package com.example.mongk.intprint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.mocoo.hang.rtprinter.driver.Contants;
import com.mocoo.hang.rtprinter.driver.HsBluetoothPrintDriver;
import java.lang.ref.WeakReference;

import printproject.com.printproject.R;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "BloothPrinterActivity";
    private static BluetoothDevice device;
    private BluetoothAdapter mBluetoothAdapter = null;
    private static HsBluetoothPrintDriver BLUETOOTH_PRINTER = null;

    private AlertDialog.Builder alertDlgBuilder;

    private static Context CONTEXT;
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    //uis
    private static Button mBtnConnetBluetoothDevice = null;
    private static Button mBtnPrint = null;
    private static TextView txtPrinterStatus = null;
    private static ImageView mImgPosPrinter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set view
        setContentView(R.layout.activity_main);

        CONTEXT = getApplicationContext();
        alertDlgBuilder  = new AlertDialog.Builder(MainActivity.this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not available in your device
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;

        }

        initializeBluetoothDevice();


        //inital ui
        txtPrinterStatus = (TextView) findViewById(R.id.txtPrinerStatus);
        mBtnConnetBluetoothDevice = (Button)findViewById(R.id.btn_connect_bluetooth_device);
        mBtnConnetBluetoothDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If bluetooth is disabled then ask user to enable it again
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                }else{//If the connection is lost with last connected bluetooth printer
                    if(BLUETOOTH_PRINTER.IsNoConnection()){
                        //serverIntent = new Intent(MainActivity.this, DeviceListActivity.class);
                        //startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                    }else{ //If an existing connection is still alive then ask user to kill it and re-connect again


                    }
                }

            }
        });
        mBtnPrint = (Button)findViewById(R.id.btn_print);
        mBtnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mImgPosPrinter = (ImageView)findViewById(R.id.printer_imgPOSPrinter);


        //intent sent
        String action = getIntent().getAction();
        if(Intent.ACTION_SEND == action){


        }

    }

    private void initializeBluetoothDevice(){
        Log.d(TAG, "setup Bluetooth");

        BLUETOOTH_PRINTER = HsBluetoothPrintDriver.getInstance();
        BLUETOOTH_PRINTER.setHandler(new BluetoothHandler(MainActivity.this));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(BLUETOOTH_PRINTER.IsNoConnection())
            BLUETOOTH_PRINTER.stop();
    }

    @Override
    public void onStart(){
        super.onStart();

        if(!mBluetoothAdapter.isEnabled()){
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        else{

            if(BLUETOOTH_PRINTER.IsNoConnection()){
                //present no connection
            }
            else{
                txtPrinterStatus.setText(device.getName());
            }
        }
    }

    static class BluetoothHandler extends Handler {
        private final WeakReference<MainActivity> myWeakReference;

        //Creating weak reference of BluetoothPrinterActivity class to avoid any leak
        BluetoothHandler(MainActivity weakReference) {
            myWeakReference = new WeakReference<MainActivity>(weakReference);
        }
        @Override
        public void handleMessage(Message msg)
        {
            MainActivity bluetoothPrinterActivity = myWeakReference.get();
            if (bluetoothPrinterActivity != null) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                switch (data.getInt("flag")) {
                    case Contants.FLAG_STATE_CHANGE:
                        int state = data.getInt("state");
                        Log.i(TAG, "MESSAGE_STATE_CHANGE: " + state);
                        switch (state) {
                            case HsBluetoothPrintDriver.CONNECTED_BY_BLUETOOTH:
                                //txtPrinterStatus.setText(R.string.title_connected_to);
                                //txtPrinterStatus.append(device.getName());
                                // StaticValue.isPrinterConnected=true;
                                // Toast.makeText(CONTEXT,"Connection successful.", Toast.LENGTH_SHORT).show();
                                // mImgPosPrinter.setImageResource(R.drawable.pos_printer);
                                break;
                            case HsBluetoothPrintDriver.FLAG_SUCCESS_CONNECT:
                                // txtPrinterStatus.setText(R.string.title_connecting);
                                break;

                            case HsBluetoothPrintDriver.UNCONNECTED:
                                //txtPrinterStatus.setText(R.string.no_printer_connected);
                                break;
                        }
                        break;
                    case Contants.FLAG_SUCCESS_CONNECT:
                        //txtPrinterStatus.setText(R.string.title_connecting);
                        break;
                    case Contants.FLAG_FAIL_CONNECT:
                        //Toast.makeText(CONTEXT,"Connection failed.",Toast.LENGTH_SHORT).show();
                        //mImgPosPrinter.setImageResource(R.drawable.pos_printer_offliine);
                        break;
                    default:
                        break;

                }
            }
        };
    }
}
