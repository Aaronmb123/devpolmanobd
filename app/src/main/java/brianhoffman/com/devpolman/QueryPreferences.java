package brianhoffman.com.devpolman;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class QueryPreferences {

    private static final String PREF_IS_ALARM_ON = "isAlarmOn";
    private static final String PREF_IS_DEVICE_POLICY_MANAGER_ON = "isDevPolManOn";

    public static boolean isAlarmOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_ALARM_ON, false);
    }

    public static void setAlarmOn(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_ALARM_ON, isOn)
                .apply();
    }

    public static boolean isDevicePolicyManagerOn(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_DEVICE_POLICY_MANAGER_ON, false);
    }

    public static void setDevicePolicyManagerOn(Context context, boolean isOn) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_DEVICE_POLICY_MANAGER_ON, isOn)
                .apply();
    }

}
