package com.pazim.smsemailforwarder;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int RECEIVE_SMS_REQUEST = 2001;
    private static final int SEND_SMS_REQUEST = 2002;

    private EditText phone;
    private EditText senderEmail;
    private EditText emailPassword;
    private EditText destinationEmail;

    private TextView history;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        int padding = dp(20);

        ScrollView scrollView = new ScrollView(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        scrollView.addView(root);

        TextView title = new TextView(this);
        title.setText("SMS Forwarder");
        title.setTextSize(27);
        root.addView(title);

        TextView warning = new TextView(this);
        warning.setText(
                "Use only with the phone owner's clear consent. " +
                "Use a Gmail app password, not the normal Gmail password."
        );
        warning.setPadding(
                0,
                dp(8),
                0,
                dp(12)
        );
        root.addView(warning);

        Switch emailSwitch = new Switch(this);
        emailSwitch.setText("Forward to email");
        emailSwitch.setChecked(
                AppPrefs.email(this)
        );
        emailSwitch.setOnCheckedChangeListener(
                (buttonView, checked) ->
                        AppPrefs.email(this, checked)
        );
        root.addView(emailSwitch);

        senderEmail = new EditText(this);
        senderEmail.setHint("Sender Gmail address");
        senderEmail.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );
        senderEmail.setText(
                AppPrefs.emailUsername(this)
        );
        root.addView(senderEmail);

        emailPassword = new EditText(this);
        emailPassword.setHint(
                "16-character Gmail app password"
        );
        emailPassword.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_PASSWORD
        );
        emailPassword.setText(
                AppPrefs.emailPassword(this)
        );
        root.addView(emailPassword);

        destinationEmail = new EditText(this);
        destinationEmail.setHint(
                "Destination email address"
        );
        destinationEmail.setInputType(
                InputType.TYPE_CLASS_TEXT
                        | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        );
        destinationEmail.setText(
                AppPrefs.destinationEmail(this)
        );
        root.addView(destinationEmail);

        Switch smsSwitch = new Switch(this);
        smsSwitch.setText("Forward by SMS");
        smsSwitch.setChecked(
                AppPrefs.sms(this)
        );
        smsSwitch.setOnCheckedChangeListener(
                (buttonView, checked) ->
                        AppPrefs.sms(this, checked)
        );
        root.addView(smsSwitch);

        phone = new EditText(this);
        phone.setHint(
                "Destination phone, e.g. +14165551234"
        );
        phone.setInputType(
                InputType.TYPE_CLASS_PHONE
        );
        phone.setText(
                AppPrefs.phone(this)
        );
        root.addView(phone);

        Button saveButton =
                createButton("Save settings");

        saveButton.setOnClickListener(view ->
                saveSettings()
        );

        root.addView(saveButton);

        Button receivePermissionButton =
                createButton("Allow receiving SMS");

        receivePermissionButton.setOnClickListener(
                view -> requestReceiveSmsPermission()
        );

        root.addView(receivePermissionButton);

        Button sendPermissionButton =
                createButton("Allow sending SMS");

        sendPermissionButton.setOnClickListener(
                view -> requestSendSmsPermission()
        );

        root.addView(sendPermissionButton);

        Button testEmailButton =
                createButton("Test email");

        testEmailButton.setOnClickListener(view -> {
            saveSettings();

            Forwarder.sendEmail(
                    getApplicationContext(),
                    "TEST",
                    "Android direct SMTP email test"
            );

            refreshLater();
        });

        root.addView(testEmailButton);

        Button testSmsButton =
                createButton("Test SMS");

        testSmsButton.setOnClickListener(view -> {
            saveSettings();

            if (checkSelfPermission(
                    Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestSendSmsPermission();
                return;
            }

            if (phone.getText()
                    .toString()
                    .trim()
                    .isEmpty()) {

                Toast.makeText(
                        this,
                        "Enter a destination phone number",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            Forwarder.sendSms(
                    getApplicationContext(),
                    "TEST",
                    "Android SMS test"
            );

            refreshLater();
        });

        root.addView(testSmsButton);

        TextView historyTitle =
                new TextView(this);

        historyTitle.setText(
                "Forwarding history"
        );

        historyTitle.setTextSize(21);

        historyTitle.setPadding(
                0,
                dp(18),
                0,
                dp(5)
        );

        root.addView(historyTitle);

        Button refreshButton =
                createButton("Refresh history");

        refreshButton.setOnClickListener(
                view -> refreshHistory()
        );

        root.addView(refreshButton);

        Button clearButton =
                createButton("Clear history");

        clearButton.setOnClickListener(view -> {
            HistoryStore.clear(this);
            refreshHistory();
        });

        root.addView(clearButton);

        history = new TextView(this);
        history.setTextIsSelectable(true);
        history.setPadding(
                0,
                dp(10),
                0,
                dp(30)
        );

        root.addView(history);

        setContentView(scrollView);

        refreshHistory();
    }

    private void saveSettings() {
        AppPrefs.emailUsername(
                this,
                senderEmail.getText()
                        .toString()
                        .trim()
        );

        AppPrefs.emailPassword(
                this,
                emailPassword.getText()
                        .toString()
        );

        AppPrefs.destinationEmail(
                this,
                destinationEmail.getText()
                        .toString()
                        .trim()
        );

        AppPrefs.phone(
                this,
                phone.getText()
                        .toString()
                        .trim()
        );

        Toast.makeText(
                this,
                "Settings saved",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void requestReceiveSmsPermission() {
        if (checkSelfPermission(
                Manifest.permission.RECEIVE_SMS
        ) == PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(
                    this,
                    "Receive SMS permission already granted",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        requestPermissions(
                new String[]{
                        Manifest.permission.RECEIVE_SMS
                },
                RECEIVE_SMS_REQUEST
        );
    }

    private void requestSendSmsPermission() {
        if (checkSelfPermission(
                Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(
                    this,
                    "Send SMS permission already granted",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        requestPermissions(
                new String[]{
                        Manifest.permission.SEND_SMS
                },
                SEND_SMS_REQUEST
        );
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        boolean granted =
                grantResults.length > 0
                        && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED;

        if (requestCode == RECEIVE_SMS_REQUEST) {
            Toast.makeText(
                    this,
                    granted
                            ? "Receive SMS permission granted"
                            : "Receive SMS permission denied",
                    Toast.LENGTH_LONG
            ).show();
        }

        if (requestCode == SEND_SMS_REQUEST) {
            Toast.makeText(
                    this,
                    granted
                            ? "Send SMS permission granted"
                            : "Send SMS permission denied",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHistory();
    }

    private void refreshHistory() {
        if (history != null) {
            history.setText(
                    HistoryStore.formatted(this)
            );
        }
    }

    private void refreshLater() {
        refreshHistory();

        if (history != null) {
            history.postDelayed(
                    this::refreshHistory,
                    2000
            );

            history.postDelayed(
                    this::refreshHistory,
                    6000
            );
        }
    }

    private Button createButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        return button;
    }

    private int dp(int value) {
        return (int) (
                value
                        * getResources()
                        .getDisplayMetrics()
                        .density
        );
    }
}
