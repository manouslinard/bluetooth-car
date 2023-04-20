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
import android.widget.Toast;
import android.Manifest;


import java.io.IOException;
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
        initMovementButtons();
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
            btSocket.connect(); // connect to the device
            Toast.makeText(MainActivity.this, "Connected to device", Toast.LENGTH_SHORT).show();
            outputStream = null;
            try {
                outputStream = btSocket.getOutputStream();
                startIdle();
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
    }
}
