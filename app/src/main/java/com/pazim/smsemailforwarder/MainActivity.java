package com.pazim.smsemailforwarder;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final int SMS_PERMISSION_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int padding = (int) (24 * getResources().getDisplayMetrics().density);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(padding, padding, padding, padding);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("SMS Email Forwarder");
        title.setTextSize(26);
        title.setGravity(Gravity.CENTER);

        TextView info = new TextView(this);
        info.setText(
                "\n1. Configure the Apps Script URL and secret.\n" +
                "2. Grant SMS permission.\n" +
                "3. Keep this app installed.\n\n" +
                "Incoming SMS messages will be posted securely to your email relay."
        );
        info.setTextSize(17);

        Button permissionButton = new Button(this);
        permissionButton.setText("Grant SMS Permission");
        permissionButton.setOnClickListener(v -> requestSmsPermission());

        Button testButton = new Button(this);
        testButton.setText("Send Test Email");
        testButton.setOnClickListener(v -> {
            String deviceName = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
            Forwarder.sendAsync(
                    getApplicationContext(),
                    "TEST",
                    "Test message from " + deviceName
            );
            Toast.makeText(this, "Test request started", Toast.LENGTH_SHORT).show();
        });

        layout.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        layout.addView(info, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        layout.addView(permissionButton);
        layout.addView(testButton);

        setContentView(layout);
    }

    private void requestSmsPermission() {
        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS permission is already granted",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        requestPermissions(
                new String[]{Manifest.permission.RECEIVE_SMS},
                SMS_PERMISSION_REQUEST
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST) {
            boolean granted = grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            Toast.makeText(
                    this,
                    granted ? "SMS forwarding enabled" : "SMS permission denied",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
