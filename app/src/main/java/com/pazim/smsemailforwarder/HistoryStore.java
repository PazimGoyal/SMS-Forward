package com.pazim.smsemailforwarder;
import android.content.*; import org.json.*; import java.text.*; import java.util.*;
public final class HistoryStore { private static final String F="history",K="items"; private HistoryStore(){}
 private static JSONArray a(Context c){try{return new JSONArray(c.getSharedPreferences(F,0).getString(K,"[]"));}catch(Exception e){return new JSONArray();}}
 private static void s(Context c,JSONArray a){c.getSharedPreferences(F,0).edit().putString(K,a.toString()).apply();}
 public static synchronized String add(Context c,String channel,String dest,String sender,String msg,String status,String detail){String id=UUID.randomUUID().toString();try{JSONArray old=a(c),n=new JSONArray();JSONObject o=new JSONObject();o.put("id",id);o.put("time",System.currentTimeMillis());o.put("channel",channel);o.put("dest",dest);o.put("sender",sender);o.put("msg",msg);o.put("status",status);o.put("detail",detail);n.put(o);for(int i=0;i<old.length()&&n.length()<100;i++)n.put(old.get(i));s(c,n);}catch(Exception ignored){}return id;}
 public static synchronized void update(Context c,String id,String status,String detail){try{JSONArray x=a(c);for(int i=0;i<x.length();i++){JSONObject o=x.getJSONObject(i);if(id.equals(o.optString("id"))){o.put("status",status);o.put("detail",detail);break;}}s(c,x);}catch(Exception ignored){}}
 public static synchronized String text(Context c){JSONArray x=a(c);if(x.length()==0)return "No forwarding attempts yet.";StringBuilder b=new StringBuilder();SimpleDateFormat f=new SimpleDateFormat("MMM d, h:mm:ss a",Locale.getDefault());for(int i=0;i<x.length();i++){JSONObject o=x.optJSONObject(i);if(o==null)continue;String m=o.optString("msg","");if(m.length()>180)m=m.substring(0,180)+"…";b.append(o.optString("channel")).append(" • ").append(o.optString("status")).append("
").append(f.format(new Date(o.optLong("time")))).append(" • From: ").append(o.optString("sender")).append("
To: ").append(o.optString("dest")).append("
").append(m);String d=o.optString("detail","");if(!d.isEmpty())b.append("
").append(d);b.append("

");}return b.toString().trim();}
 public static void clear(Context c){c.getSharedPreferences(F,0).edit().remove(K).apply();}
}
