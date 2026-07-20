# SMS Email Forwarder — no Android Studio required

This project:
1. receives normal cellular SMS messages;
2. posts the sender and message over HTTPS to Google Apps Script;
3. emails the message to your chosen address.

It does not forward WhatsApp, RCS-only chats, MMS pictures, or messages already
received before installation.

## Part 1 — Create the email relay

1. Open https://script.google.com while signed into the Gmail account that will send the emails.
2. Select **New project**.
3. Open `apps-script/Code.gs` from this project and copy all its contents into the Apps Script editor.
4. Change:
   - `DESTINATION_EMAIL`
   - `SECRET`
5. Use a long random secret, such as 40+ random letters and numbers.
6. Click **Deploy > New deployment**.
7. Select **Web app**.
8. Execute as: **Me**
9. Who has access: **Anyone**
10. Click **Deploy**, authorize the script, then copy the URL ending in `/exec`.

The URL is public, but requests cannot send email without your matching secret.
Do not share the URL or secret.

## Part 2 — Configure the Android app

Open:
`app/src/main/java/com/pazim/smsemailforwarder/Config.java`

Replace:
- `PASTE_YOUR_GOOGLE_APPS_SCRIPT_EXEC_URL_HERE`
- `CHANGE_THIS_TO_A_LONG_RANDOM_SECRET`

The secret must be exactly the same as in Apps Script.

## Part 3 — Build without Android Studio

### GitHub Codespaces

1. Create a new empty GitHub repository.
2. Upload all files from this project, preserving folders.
3. Commit to the `main` branch.
4. Open the repository's **Actions** tab.
5. Open **Build Android APK**.
6. Run the workflow, or push a small change.
7. After it succeeds, open the workflow run.
8. Under **Artifacts**, download `SmsEmailForwarder-debug`.
9. Unzip it to get `app-debug.apk`.

## Part 4 — Install on the Samsung phone

1. Transfer `app-debug.apk` to the phone.
2. Open it.
3. Android may ask permission to install unknown apps for the browser or file manager.
4. Enable that permission only for the app you are using to open the APK.
5. Install and open **SMS Email Forwarder**.
6. Tap **Grant SMS Permission** and allow it.
7. Tap **Send Test Email**.
8. Send a normal SMS to the phone from another phone.

## Samsung reliability settings

On the Samsung phone:
1. Settings > Apps > SMS Email Forwarder > Battery.
2. Choose **Unrestricted**.
3. Settings > Battery > Background usage limits.
4. Add the app to **Never auto sleeping apps** if that option is shown.

## Security notes

- Use only on a phone you own or administer with the owner's clear consent.
- SMS can contain bank codes, password-reset links, medical details, and personal messages.
- Anyone who obtains your APK can extract its embedded relay URL and secret.
- Do not publish this APK or send it to other people.
- For stronger security, replace the shared-secret relay with authenticated user login.
- This personal sideloaded app is not designed for Google Play publication.

## Limitations

- Requires internet access at the moment the SMS arrives.
- This minimal version does not retry failed deliveries.
- It handles multipart SMS by joining the message segments.
- It does not forward MMS media or RCS messages.
