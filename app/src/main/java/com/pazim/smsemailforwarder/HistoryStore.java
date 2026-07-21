package com.pazim.smsemailforwarder;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public final class HistoryStore {

    private static final String FILE_NAME = "forward_history";
    private static final String KEY_ITEMS = "items";
    private static final int MAX_ITEMS = 100;

    private HistoryStore() {
    }

    public static synchronized String add(
            Context context,
            String channel,
            String destination,
            String sender,
            String message,
            String status,
            String detail
    ) {
        String id = UUID.randomUUID().toString();

        try {
            JSONArray oldItems = readArray(context);
            JSONArray newItems = new JSONArray();

            JSONObject item = new JSONObject();
            item.put("id", id);
            item.put("time", System.currentTimeMillis());
            item.put("channel", channel);
            item.put("destination", destination);
            item.put("sender", sender);
            item.put("message", message);
            item.put("status", status);
            item.put("detail", detail);

            newItems.put(item);

            for (
                    int i = 0;
                    i < oldItems.length() && newItems.length() < MAX_ITEMS;
                    i++
            ) {
                newItems.put(oldItems.get(i));
            }

            saveArray(context, newItems);

        } catch (Exception ignored) {
        }

        return id;
    }

    public static synchronized void updateStatus(
            Context context,
            String id,
            String status,
            String detail
    ) {
        try {
            JSONArray items = readArray(context);

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                if (id.equals(item.optString("id"))) {
                    item.put("status", status);
                    item.put("detail", detail);
                    break;
                }
            }

            saveArray(context, items);

        } catch (Exception ignored) {
        }
    }

    public static synchronized String formatted(Context context) {
        JSONArray items = readArray(context);

        if (items.length() == 0) {
            return "No forwarding attempts yet.";
        }

        StringBuilder output = new StringBuilder();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMM d, h:mm:ss a",
                Locale.getDefault()
        );

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.optJSONObject(i);

            if (item == null) {
                continue;
            }

            output.append(item.optString("channel", "?"))
                    .append(" • ")
                    .append(item.optString("status", "?"))
                    .append("\n");

            output.append(
                    dateFormat.format(
                            new Date(item.optLong("time"))
                    )
            );

            output.append(" • From: ")
                    .append(item.optString("sender", "Unknown"))
                    .append("\n");

            output.append("To: ")
                    .append(item.optString(
                            "destination",
                            "Not configured"
                    ))
                    .append("\n");

            output.append(
                    shorten(
                            item.optString("message", ""),
                            180
                    )
            );

            String detail = item.optString("detail", "");

            if (!detail.isEmpty()) {
                output.append("\n").append(detail);
            }

            output.append("\n\n");
        }

        return output.toString().trim();
    }

    public static synchronized void clear(Context context) {
        context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
        )
                .edit()
                .remove(KEY_ITEMS)
                .apply();
    }

    private static JSONArray readArray(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences(
                        FILE_NAME,
                        Context.MODE_PRIVATE
                );

        String json = preferences.getString(KEY_ITEMS, "[]");

        try {
            return new JSONArray(json);
        } catch (Exception exception) {
            return new JSONArray();
        }
    }

    private static void saveArray(
            Context context,
            JSONArray array
    ) {
        context.getSharedPreferences(
                FILE_NAME,
                Context.MODE_PRIVATE
        )
                .edit()
                .putString(KEY_ITEMS, array.toString())
                .apply();
    }

    private static String shorten(
            String value,
            int maximumLength
    ) {
        if (value == null) {
            return "";
        }

        if (value.length() <= maximumLength) {
            return value;
        }

        return value.substring(0, maximumLength) + "…";
    }
}
