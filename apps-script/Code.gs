const DESTINATION_EMAIL = "YOUR_EMAIL@gmail.com";
const SECRET = "CHANGE_THIS_TO_A_LONG_RANDOM_SECRET";

function doPost(e) {
  try {
    const p = e.parameter || {};

    if (!p.secret || p.secret !== SECRET) {
      return textResponse("Unauthorized", 401);
    }

    const sender = String(p.sender || "Unknown");
    const message = String(p.message || "");
    const timestamp = Number(p.timestamp || Date.now());
    const receivedAt = new Date(timestamp);

    if (!message) {
      return textResponse("Missing message", 400);
    }

    const subject = "SMS from " + sender;
    const body =
      "From: " + sender + "\n" +
      "Received: " + receivedAt.toString() + "\n\n" +
      message;

    MailApp.sendEmail({
      to: DESTINATION_EMAIL,
      subject: subject,
      body: body
    });

    return textResponse("OK", 200);
  } catch (error) {
    console.error(error);
    return textResponse("Error", 500);
  }
}

function textResponse(message, statusCode) {
  // Apps Script ContentService does not allow setting an HTTP status directly.
  // The text value is still useful for logging and testing.
  return ContentService
    .createTextOutput(message)
    .setMimeType(ContentService.MimeType.TEXT);
}
