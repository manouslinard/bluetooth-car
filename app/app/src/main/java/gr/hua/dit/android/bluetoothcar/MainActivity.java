package gr.hua.dit.android.bluetoothcar;

import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class MainActivity extends AppCompatActivity {

    private Button fwd_btn;
    private Button back_btn;
    private Button left_btn;
    private Button right_btn;
    private Button connect_btn;

    private Button top_left_plus;

    private Button top_left_minus;

    private Button bottom_left_plus;

    private Button bottom_left_minus;

    private Button top_right_plus;

    private Button top_right_minus;

    private Button bottom_right_plus;

    private Button bottom_right_minus;

    private TextView top_left_text_sp;
    private TextView top_right_text_sp;
    private TextView bottom_left_text_sp;
    private TextView bottom_right_text_sp;

    private Switch autoSwitch;
    private Switch lineSwitch;
    private int REQUEST_ENABLE_BT = 1;
    private ArrayList<String> bluetoothList = new ArrayList<>();
    private int REQUEST_BLUETOOTH_PERMISSION = 1;
    private static final String BLUETOOTH_PERMISSION = Manifest.permission.BLUETOOTH;
    private Map<String, BluetoothDevice> bluetoothDevices;
    private UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");    // default value for bluetooth module
    private OutputStream outputStream = null;
    private String selectedItem;
    private boolean buttonIsPressed = false;
    private BluetoothSocket btSocket;
    private boolean idle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bluetoothDevices = new HashMap<String, BluetoothDevice>();
        selectedItem = null;

        // Request Bluetooth permission if not granted already
        if (ContextCompat.checkSelfPermission(this, BLUETOOTH_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{BLUETOOTH_PERMISSION}, REQUEST_BLUETOOTH_PERMISSION);
        } else {
            // Bluetooth permission already granted, proceed with Bluetooth operations
            // ...
        }
        bluetoothList.add("No Devices Available");  // remove this when bluetooth devices found (use bluetoothList.remove(0);)

        initBluetoothList(bluetoothList);   // sets the values of the list to dropdown list in gui.
        initMovementButtons();
        initSpeedButtons();
        handleAllButtons(false);
    }


    private void handleAllButtons(boolean state){
        /**
         * Handles the state of all buttons, switches and velocity textviews.
         * Param: state: if true, enables all buttons, switches & velocity textviews, else it disables them.
         */
        runOnUiThread(() -> {
            fwd_btn.setEnabled(state);
            back_btn.setEnabled(state);
            left_btn.setEnabled(state);
            right_btn.setEnabled(state);
            autoSwitch.setEnabled(state);
            lineSwitch.setEnabled(state);
            top_left_text_sp.setEnabled(state);
            top_right_text_sp.setEnabled(state);
            bottom_left_text_sp.setEnabled(state);
            bottom_right_text_sp.setEnabled(state);
            top_left_plus.setEnabled(state);
            top_left_minus.setEnabled(state);
            bottom_left_plus.setEnabled(state);
            bottom_left_minus.setEnabled(state);
            top_right_plus.setEnabled(state);
            top_right_minus.setEnabled(state);
            bottom_right_plus.setEnabled(state);
            bottom_right_minus.setEnabled(state);
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initMovementButtons() {
        /**
         * Initializes all the movement button click listeners.
         */
        fwd_btn = findViewById(R.id.fwd_button);
        back_btn = findViewById(R.id.back_button);
        left_btn = findViewById(R.id.left_button);
        right_btn = findViewById(R.id.right_button);
        autoSwitch = findViewById(R.id.auto_switch);
        lineSwitch = findViewById(R.id.line_switch);
        connect_btn = findViewById(R.id.connect_button);

        // Basic Movement Buttons Initialized =================
        fwd_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed
                        startAction('F');
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Button released
                        stopAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved while pressed
                        return true;
                }
                return false;
            }
        });

        back_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed
                        startAction('B');
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Button released
                        stopAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved while pressed
                        return true;
                }
                return false;
            }
        });

        left_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed
                        startAction('L');
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Button released
                        stopAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved while pressed
                        return true;
                }
                return false;
            }
        });


        right_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed
                        startAction('R');
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Button released
                        stopAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved while pressed
                        return true;
                }
                return false;
            }
        });
        // ====================================================

        connect_btn.setOnClickListener(v -> {
            if (connect_btn.getText().equals("Disconnect")){
                try {
                    stopIdle();
                    btSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                connect_btn.setText("Connect");
                handleAllButtons(false);
                return;
            }
            if (selectedItem == null) {
                return;
            }
            BluetoothDevice b = bluetoothDevices.get(selectedItem);
            if (b != null) {
                connectToAddress(b.getAddress());
            }
        });

        // Switches Initialized ===============================
        autoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch is ON
                handleButtons(lineSwitch, false);
                sendCharToArduino('V');
            } else {
                // Switch is OFF
                handleButtons(lineSwitch, true);
                sendCharToArduino('v');
            }
        });
        lineSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch is ON
                handleButtons(autoSwitch, false);
                sendCharToArduino('X');
            } else {
                // Switch is OFF
                handleButtons(autoSwitch, true);
                sendCharToArduino('x');
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initSpeedButtons(){
        top_left_text_sp = findViewById(R.id.top_left_text);
        top_right_text_sp = findViewById(R.id.top_right_text);
        bottom_left_text_sp = findViewById(R.id.bottom_left_text);
        bottom_right_text_sp = findViewById(R.id.bottom_right_text);
        top_left_plus = findViewById(R.id.top_left_plus);
        top_left_minus = findViewById(R.id.top_left_minus);
        bottom_left_plus = findViewById(R.id.bottom_left_plus);
        bottom_left_minus = findViewById(R.id.bottom_left_minus);
        top_right_plus = findViewById(R.id.top_right_plus);
        top_right_minus = findViewById(R.id.top_right_minus);
        bottom_right_plus = findViewById(R.id.bottom_right_plus);
        bottom_right_minus = findViewById(R.id.bottom_right_minus);
        top_left_plus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed
                        startAction('1');
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Button released
                        stopAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved while pressed
                        return true;
                }
                return false;
            }
        });

        top_left_minus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed
                        startAction('0');
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Button released
                        stopAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved while pressed
                        return true;
                }
                return false;
            }
        });

        bottom_left_plus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed
                        startAction('7');
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Button released
                        stopAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved while pressed
                        return true;
                }
                return false;
            }
        });

        bottom_left_minus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed
                        startAction('6');
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Button released
                        stopAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved while pressed
                        return true;
                }
                return false;
            }
        });

        top_right_plus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed
                        startAction('3');
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Button released
                        stopAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved while pressed
                        return true;
                }
                return false;
            }
        });

        top_right_minus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed
                        startAction('2');
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Button released
                        stopAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved while pressed
                        return true;
                }
                return false;
            }
        });

        bottom_right_plus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed
                        startAction('5');
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Button released
                        stopAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved while pressed
                        return true;
                }
                return false;
            }
        });

        bottom_right_minus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Button pressed
                        startAction('4');
                        return true;
                    case MotionEvent.ACTION_UP:
                        // Button released
                        stopAction();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        // Button moved while pressed
                        return true;
                }
                return false;
            }
        });

    }
    private void startAction(char c) {
        buttonIsPressed = true;
        idle = false;
        new Thread(new Runnable() {
            public void run() {
                while (buttonIsPressed) {
                    // Execute your action repeatedly here
                    sendCharToArduino(c);
                    System.out.println("Button pressed " + c);
                    try {
                        Thread.sleep(100); // Wait for 200 milliseconds before executing again
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void stopAction() {
        buttonIsPressed = false;
        if (btSocket.isConnected()) {
            startIdle();
        }
        // Stop executing the action here
        System.out.println("Button released");
    }

    private void startIdle() {
        idle = true;
        new Thread(new Runnable() {
            public void run() {
                while (idle) {
                    // Execute your action repeatedly here
                    sendCharToArduino('S');
                    try {
                        Thread.sleep(100); // Wait for 200 milliseconds before executing again
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private void stopIdle() {
        idle = false;
        // Stop executing the action here
        System.out.println("Button released");
    }



    private void connectToAddress(String deviceAddress) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceAddress); // deviceAddress is the address of the Bluetooth device
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        btSocket = null; // uuid is the UUID of the Bluetooth service that the device uses
        try {
            ParcelUuid[] uuids = bluetoothDevice.getUuids();
            if (uuids != null) {
                for (ParcelUuid u : uuids) {
                    uuid = u.getUuid();
                }
            }
            btSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //bluetoothAdapter.cancelDiscovery(); // cancel discovery as it will slow down the connection
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            Toast.makeText(MainActivity.this, "Connecting...", Toast.LENGTH_SHORT).show();
            btSocket.connect(); // connect to the device
            Toast.makeText(MainActivity.this, "Connected to device", Toast.LENGTH_SHORT).show();
            handleAllButtons(true);
            outputStream = null;
            try {
                outputStream = btSocket.getOutputStream();
                startIdle();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(btSocket.getInputStream()));
                new Thread(() -> {
                    while (true) {
                        try {
                            Thread.sleep(1000); // Wait for 1 second before checking the connection
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // Check if the connection is lost
                        try {
                            String receivedData = bufferedReader.readLine();
                            try {
                                JSONObject jsonObject = new JSONObject(receivedData);
                                int motor1Speed = jsonObject.getInt("motor1_speed");
                                int motor2Speed = jsonObject.getInt("motor2_speed");
                                int motor3Speed = jsonObject.getInt("motor3_speed");
                                int motor4Speed = jsonObject.getInt("motor4_speed");
                                if (Integer.parseInt(top_left_text_sp.getText().toString()) != motor1Speed || Integer.parseInt(top_right_text_sp.getText().toString()) != motor4Speed || Integer.parseInt(bottom_right_text_sp.getText().toString()) != motor3Speed || Integer.parseInt(bottom_left_text_sp.getText().toString()) != motor2Speed) {
                                    runOnUiThread(() -> {
                                        // Toast.makeText(MainActivity.this, "Received: " + motor1Speed, Toast.LENGTH_SHORT).show();
                                        top_left_text_sp.setText("" + motor1Speed);
                                        top_right_text_sp.setText("" + motor4Speed);
                                        bottom_right_text_sp.setText("" + motor3Speed);
                                        bottom_left_text_sp.setText("" + motor2Speed);
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            runOnUiThread(() -> {
                                if (idle) { // if still idle & error in inputStream -> connection lost.
                                    Toast.makeText(MainActivity.this, "Connection Lost.", Toast.LENGTH_SHORT).show();
                                    stopIdle();
                                    connect_btn.setText("Connect");
                                    handleAllButtons(false);
                                }
                            });
                            break;
                        }
                    }
                }).start();
                connect_btn.setText("Disconnect");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Could not connect to device", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // Method to send a character to Arduino via Bluetooth
    public void sendCharToArduino(char c) {
        if (outputStream == null) {
            return;
        }
        try {
            outputStream.write(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initBluetoothList(ArrayList<String> bluetoothList) {
        Spinner spinner = findViewById(R.id.bluet_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, bluetoothList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                // Get the selected item
                selectedItem = (String) adapterView.getItemAtPosition(i);
                // Do something with the selected item, such as connecting to the Bluetooth device
                connectBluetooth(selectedItem);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }

    private void connectBluetooth(final String selectedItem) {
        final BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                        REQUEST_BLUETOOTH_PERMISSION);
            } else {
                // Start a new thread to connect to the selected Bluetooth device

                BluetoothDevice selectedDevice = null;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    bluetoothList.clear(); // Clear the list before adding new devices
                    for (BluetoothDevice device : pairedDevices) {
                        bluetoothList.add(device.getName());
                        bluetoothDevices.put(device.getName(), device);
                    }
                }

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Spinner spinner = findViewById(R.id.bluet_list);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item, bluetoothList);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            } else {
                // Permission denied, inform the user
                Toast.makeText(this, "Bluetooth permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void handleButtons(Switch s, boolean enable) {
        /**
         * Method to enable - disable all the buttons and a y switch (use when switch x is pressed).
         * Param: Switch s: the y switch to be disabled.
         *        boolean enable: if true, enables all buttons and y switch. Else, disables them.
         */
        fwd_btn.setEnabled(enable);
        back_btn.setEnabled(enable);
        right_btn.setEnabled(enable);
        left_btn.setEnabled(enable);
        s.setEnabled(enable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopIdle();
        handleAllButtons(false);
    }
}
