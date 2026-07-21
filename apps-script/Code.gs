const DESTINATION_EMAIL = "pazimgoyal@gmail.com";
const SECRET = "pazimgoyal123123pazimgoyal";
function authorizeAndTest(){MailApp.sendEmail({to:DESTINATION_EMAIL,subject:"SMS Forwarder relay test",body:"The relay is authorized and can send email."});}
function doGet(){return out("OK: relay is running");}
function doPost(e){try{const p=(e&&e.parameter)?e.parameter:{};if(!p.secret||p.secret!==SECRET)return out("ERROR: invalid secret");const sender=String(p.sender||"Unknown"),message=String(p.message||"");if(!message)return out("ERROR: empty message");MailApp.sendEmail({to:DESTINATION_EMAIL,subject:"SMS from "+sender,body:"From: "+sender+"
Received: "+new Date().toString()+"

"+message});return out("OK: email sent");}catch(error){console.error(error);return out("ERROR: "+error.message);}}
function out(v){return ContentService.createTextOutput(v).setMimeType(ContentService.MimeType.TEXT);}
