package com.pazim.smsemailforwarder;
import android.app.PendingIntent; import android.content.*; import android.telephony.SmsManager; import java.io.*; import java.net.*; import java.nio.charset.StandardCharsets; import java.util.*; import java.util.concurrent.*;
public final class Forwarder { private static final ExecutorService E=Executors.newSingleThreadExecutor(); private Forwarder(){}
 public static void forward(Context c,String sender,String msg){boolean any=false;if(AppPrefs.email(c)){sendEmail(c,sender,msg);any=true;}if(AppPrefs.sms(c)){sendSms(c,sender,msg);any=true;}if(!any)HistoryStore.add(c,"SYSTEM","",sender,msg,"SKIPPED","Both switches are off.");}
 public static void sendSms(Context c,String sender,String msg){String dest=AppPrefs.phone(c);if(dest.isEmpty()){HistoryStore.add(c,"SMS","",sender,msg,"FAILED","Destination phone is empty.");return;}String id=HistoryStore.add(c,"SMS",dest,sender,msg,"SENDING","Submitted to Android SMS service.");try{SmsManager m=SmsManager.getDefault();ArrayList<String> parts=m.divideMessage("Forwarded SMS\nFrom: " + sender + "\n\n" + msg);ArrayList<PendingIntent> sent=new ArrayList<>();for(int n=0;n<parts.size();n++){Intent x=new Intent(c,SmsStatusReceiver.class);x.putExtra("id",id);x.putExtra("part",n);x.putExtra("count",parts.size());sent.add(PendingIntent.getBroadcast(c,(id+n).hashCode(),x,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE));}m.sendMultipartTextMessage(dest,null,parts,sent,null);}catch(Exception e){HistoryStore.updateStatus(c,id,"FAILED",e.getClass().getSimpleName()+": "+e.getMessage());}}
 private static String enc(String v)throws Exception{return URLEncoder.encode(v==null?"":v,StandardCharsets.UTF_8.name());}
 private static String read(InputStream in)throws Exception{if(in==null)return "(empty response)";BufferedReader br=new BufferedReader(new InputStreamReader(in,StandardCharsets.UTF_8));StringBuilder b=new StringBuilder();String l;while((l=br.readLine())!=null)b.append(l);br.close();return b.toString().trim();}
public static void sendEmail(
        Context context,
        String sender,
        String message
) {
    DirectEmailSender.send(
            context,
            sender,
            message
    );
}
                             }
