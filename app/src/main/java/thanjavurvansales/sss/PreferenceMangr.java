package thanjavurvansales.sss;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceMangr {
    public static SharedPreferences sharedPreferences=null;

    public PreferenceMangr(Context ctx){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public void pref_putString(String key,String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public String pref_getString(String key){
        String value="";
        if(!sharedPreferences.getString(key,"").equals("")) {
            value=sharedPreferences.getString(key, "");
        }else{
            value="";
        }
        return value;
    }

    public void pref_putBoolean(String key,Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean pref_getBoolean(String key){
        Boolean value=false;
        value=sharedPreferences.getBoolean(key, false);
        return value;
    }

    public static String prefer_getString(String key,Context context){
        String value="";
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!sharedPreferences.getString(key,"").equals("")) {
            value=sharedPreferences.getString(key, "");
        }else{
            value="";
        }
        return value;
    }
}
