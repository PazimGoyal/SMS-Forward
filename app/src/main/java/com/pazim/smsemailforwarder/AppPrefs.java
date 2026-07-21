package com.pazim.smsemailforwarder;

import android.content.Context;
import android.content.SharedPreferences;

public final class AppPrefs {

    private static final String FILE_NAME = "forwarder_settings";

    private static final String KEY_EMAIL_ENABLED = "email_enabled";
    private static final String KEY_SMS_ENABLED = "sms_enabled";
    private static final String KEY_PHONE = "destination_phone";

    private static final String KEY_EMAIL_USERNAME = "email_username";
    private static final String KEY_EMAIL_PASSWORD = "email_password";
    private static final String KEY_DESTINATION_EMAIL = "destination_email";

    private AppPrefs() {
    }

    private static SharedPreferences preferences(Context context) {
        return context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
        );
    }

    public static boolean email(Context context) {
        return preferences(context).getBoolean(
                KEY_EMAIL_ENABLED,
                true
        );
    }

    public static void email(Context context, boolean enabled) {
        preferences(context)
                .edit()
                .putBoolean(KEY_EMAIL_ENABLED, enabled)
                .apply();
    }

    public static boolean sms(Context context) {
        return preferences(context).getBoolean(
                KEY_SMS_ENABLED,
                false
        );
    }

    public static void sms(Context context, boolean enabled) {
        preferences(context)
                .edit()
                .putBoolean(KEY_SMS_ENABLED, enabled)
                .apply();
    }

    public static String phone(Context context) {
        return preferences(context).getString(
                KEY_PHONE,
                ""
        );
    }

    public static void phone(Context context, String value) {
        preferences(context)
                .edit()
                .putString(KEY_PHONE, value.trim())
                .apply();
    }

    public static String emailUsername(Context context) {
        return preferences(context).getString(
                KEY_EMAIL_USERNAME,
                ""
        );
    }

    public static void emailUsername(Context context, String value) {
        preferences(context)
                .edit()
                .putString(KEY_EMAIL_USERNAME, value.trim())
                .apply();
    }

    public static String emailPassword(Context context) {
        return preferences(context).getString(
                KEY_EMAIL_PASSWORD,
                ""
        );
    }

    public static void emailPassword(Context context, String value) {
        String cleanedPassword = value
                .replace(" ", "")
                .trim();

        preferences(context)
                .edit()
                .putString(KEY_EMAIL_PASSWORD, cleanedPassword)
                .apply();
    }

    public static String destinationEmail(Context context) {
        return preferences(context).getString(
                KEY_DESTINATION_EMAIL,
                ""
        );
    }

    public static void destinationEmail(
            Context context,
            String value
    ) {
        preferences(context)
                .edit()
                .putString(KEY_DESTINATION_EMAIL, value.trim())
                .apply();
    }
}
