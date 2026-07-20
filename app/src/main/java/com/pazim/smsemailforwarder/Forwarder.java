package com.pazim.smsemailforwarder;

import android.content.Context;
import android.util.Log;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class Forwarder {
    private static final String TAG = "SmsForwarder";
    private static final ExecutorService EXECUTOR =
            Executors.newSingleThreadExecutor();

    private Forwarder() {}

    public static void sendAsync(Context context, String sender, String message) {
        EXECUTOR.execute(() -> send(sender, message));
    }

    private static void send(String sender, String message) {
        if (!Config.WEB_APP_URL.startsWith("https://")) {
            Log.e(TAG, "Configure WEB_APP_URL before testing");
            return;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(Config.WEB_APP_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setDoOutput(true);
            connection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded; charset=UTF-8"
            );

            String form = "secret=" + encode(Config.SHARED_SECRET)
                    + "&sender=" + encode(sender)
                    + "&message=" + encode(message)
                    + "&timestamp=" + encode(String.valueOf(System.currentTimeMillis()));

            byte[] bytes = form.getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(bytes.length);

            try (OutputStream output = connection.getOutputStream()) {
                output.write(bytes);
            }

            int status = connection.getResponseCode();
            Log.i(TAG, "Relay response status: " + status);
        } catch (Exception exception) {
            Log.e(TAG, "Unable to forward SMS", exception);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static String encode(String value) throws Exception {
        return java.net.URLEncoder.encode(value, StandardCharsets.UTF_8.name());
    }
}
