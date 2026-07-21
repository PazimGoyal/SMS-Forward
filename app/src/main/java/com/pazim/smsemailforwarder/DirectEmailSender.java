package com.pazim.smsemailforwarder;

import android.content.Context;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public final class DirectEmailSender {

    private static final ExecutorService EXECUTOR =
            Executors.newSingleThreadExecutor();

    private DirectEmailSender() {
    }

    public static void send(
            Context context,
            String smsSender,
            String smsMessage
    ) {
        Context appContext = context.getApplicationContext();

        String emailUsername =
                AppPrefs.emailUsername(appContext);

        String destinationEmail =
                AppPrefs.destinationEmail(appContext);

        String historyId = HistoryStore.add(
                appContext,
                "EMAIL",
                destinationEmail,
                smsSender,
                smsMessage,
                "SENDING",
                "Connecting directly to Gmail SMTP..."
        );

        EXECUTOR.execute(() ->
                sendInternal(
                        appContext,
                        historyId,
                        smsSender,
                        smsMessage
                )
        );
    }

    private static void sendInternal(
            Context context,
            String historyId,
            String smsSender,
            String smsMessage
    ) {
        try {
            String emailUsername =
                    AppPrefs.emailUsername(context);

            String emailPassword =
                    AppPrefs.emailPassword(context);

            String destinationEmail =
                    AppPrefs.destinationEmail(context);

            validateSettings(
                    emailUsername,
                    emailPassword,
                    destinationEmail
            );

            Properties properties = new Properties();

            properties.put(
                    "mail.smtp.host",
                    "smtp.gmail.com"
            );

            properties.put(
                    "mail.smtp.port",
                    "465"
            );

            properties.put(
                    "mail.smtp.auth",
                    "true"
            );

            properties.put(
                    "mail.smtp.ssl.enable",
                    "true"
            );

            properties.put(
                    "mail.smtp.ssl.trust",
                    "smtp.gmail.com"
            );

            properties.put(
                    "mail.smtp.connectiontimeout",
                    "15000"
            );

            properties.put(
                    "mail.smtp.timeout",
                    "15000"
            );

            properties.put(
                    "mail.smtp.writetimeout",
                    "15000"
            );

            Session session = Session.getInstance(
                    properties,
                    new Authenticator() {
                        @Override
                        protected PasswordAuthentication
                        getPasswordAuthentication() {
                            return new PasswordAuthentication(
                                    emailUsername,
                                    emailPassword
                            );
                        }
                    }
            );

            MimeMessage emailMessage =
                    new MimeMessage(session);

            emailMessage.setFrom(
                    new InternetAddress(
                            emailUsername,
                            "SMS Forwarder"
                    )
            );

            emailMessage.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(
                            destinationEmail,
                            false
                    )
            );

            emailMessage.setSubject(
                    "SMS from " + smsSender,
                    "UTF-8"
            );

            String body =
                    "Sender: " + smsSender + "\n" +
                    "Forwarded using SMS Forwarder\n\n" +
                    smsMessage;

            emailMessage.setText(
                    body,
                    "UTF-8"
            );

            Transport.send(emailMessage);

            HistoryStore.updateStatus(
                    context,
                    historyId,
                    "SENT",
                    "Email sent directly through Gmail SMTP."
            );

        } catch (Exception exception) {
            String errorMessage = exception.getMessage();

            if (errorMessage == null
                    || errorMessage.trim().isEmpty()) {
                errorMessage = exception.toString();
            }

            HistoryStore.updateStatus(
                    context,
                    historyId,
                    "FAILED",
                    exception.getClass().getSimpleName()
                            + ": "
                            + errorMessage
            );
        }
    }

    private static void validateSettings(
            String username,
            String password,
            String destination
    ) {
        if (username == null
                || !username.contains("@")) {
            throw new IllegalStateException(
                    "Enter a valid sender Gmail address."
            );
        }

        if (password == null
                || password.replace(" ", "").length() < 16) {
            throw new IllegalStateException(
                    "Enter the 16-character Gmail app password."
            );
        }

        if (destination == null
                || !destination.contains("@")) {
            throw new IllegalStateException(
                    "Enter a valid destination email."
            );
        }
    }
}
