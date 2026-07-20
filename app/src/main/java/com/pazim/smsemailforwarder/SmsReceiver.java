package com.pazim.smsemailforwarder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
            return;
        }

        SmsMessage[] messages =
                Telephony.Sms.Intents.getMessagesFromIntent(intent);

        StringBuilder body = new StringBuilder();
        String sender = "Unknown";

        for (SmsMessage message : messages) {
            if (message.getDisplayOriginatingAddress() != null) {
                sender = message.getDisplayOriginatingAddress();
            }
            if (message.getDisplayMessageBody() != null) {
                body.append(message.getDisplayMessageBody());
            }
        }

        Forwarder.sendAsync(context.getApplicationContext(), sender, body.toString());
    }
}
