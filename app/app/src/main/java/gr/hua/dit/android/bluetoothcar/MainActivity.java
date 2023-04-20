package gr.hua.dit.android.bluetoothcar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private Button fwd_btn;
    private Button back_btn;
    private Button left_btn;
    private Button right_btn;
    private Switch autoSwitch;
    private Switch lineSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initMovementButtons();

        ArrayList<String> bluetoothList = new ArrayList<>(); // TODO: initialize bluetooth list.
        bluetoothList.add("No Devices Available");  // remove this when bluetooth devices found (use bluetoothList.remove(0);)
        initBluetoothList(bluetoothList);   // sets the values of the list to dropdown list in gui.
    }

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

        // Basic Movement Buttons Initialized =================
        fwd_btn.setOnClickListener(v -> {
            // TODO: Send 'F' character.
        });

        back_btn.setOnClickListener(v -> {
            // TODO: Send 'B' character.
        });

        left_btn.setOnClickListener(v -> {
            // TODO: Send 'L' character.
        });

        right_btn.setOnClickListener(v -> {
            // TODO: Send 'R' character.
        });
        // ====================================================

        // Switches Initialized ===============================
        autoSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch is ON
                handleButtons(lineSwitch, false);
                // TODO: Send 'V' character.
            } else {
                // Switch is OFF
                handleButtons(lineSwitch, true);
                // TODO: Send 'v' character.
            }
        });
        lineSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Switch is ON
                handleButtons(autoSwitch, false);
                // TODO: Send 'X' character.
            } else {
                // Switch is OFF
                handleButtons(autoSwitch, true);
                // TODO: Send 'x' character.
            }
        });
        // =====================================================

    }

    private void initBluetoothList(ArrayList<String> bluetoothList) {
        /**
         * Initializes the dropdown list (spinner) with available bluetooth devices.
         * Param: ArrayList<String> bluetoothList: the list of available bluetooth devices to set to dropdown list.
         */
        Spinner spinner = findViewById(R.id.bluet_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, bluetoothList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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

}