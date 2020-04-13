package yoosin.paddy.itemrental.sharedPreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPref {
    private static final String MY_PREFS_NAME = "my_location";
    static Double myLatitude;
    private static double myLongitude;
    public static Double[] getMyLocation(Context context){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        myLatitude = Double.parseDouble(prefs.getString("latitude", "0.0"));//"No name defined" is the default value.
        myLongitude = Double.parseDouble(prefs.getString("longitude", "0.0"));//"No name defined" is the default value.
        Double[] mylocation= new Double[]{myLatitude, myLongitude};
        return mylocation;
    }
    public static boolean hasLocation(Context context){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        return !prefs.getString("latitude", "0.0").contentEquals("0.0") && !prefs.getString("longitude", "0.0").contentEquals("0.0");

    }
    public static void setMyLocation(Context myLocation, String latitude, String longitude) {
        SharedPreferences.Editor editor = myLocation.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("latitude", latitude);
        editor.putString("longitude", longitude);
        editor.apply();
    }
}
