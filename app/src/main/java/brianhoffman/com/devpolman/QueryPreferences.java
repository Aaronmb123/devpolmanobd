package brianhoffman.com.devpolman;

import android.content.Context;
import android.preference.Preference;
import android.preference.PreferenceManager;

public class QueryPreferences {

    private static final String PREF_IS_DEVICE_POLICY_MANAGER_ON = "isDevPolManOn";
    private static final String PREF_IS_SERVICE_RUNNING = "isServiceRunning";
    private static final String PREF_IS_PASSCODE_SET = "isPasscodeSet";
    private static final String PREF_HASHED_PASSCODE = "hashedPasscode";
    private static final String PREF_QUERY_INTERVAL = "queryInterval";

    public static boolean isServiceRunning(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_SERVICE_RUNNING, false);
    }

    public static void setServiceRunningState(Context context, boolean state) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putBoolean(PREF_IS_SERVICE_RUNNING, state)
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

    public static boolean isPasscodeSet(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_PASSCODE_SET, false);
    }

    public static void setPasscode(Context context, String passcode) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_HASHED_PASSCODE, passcode)
                .putBoolean(PREF_IS_PASSCODE_SET, true)
                .apply();
    }

    public static int getQueryInterval(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_QUERY_INTERVAL, 5000);
    }

    public static void setQueryInterval(Context context, int intervalInMillis) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putInt(PREF_QUERY_INTERVAL, intervalInMillis)
                .apply();
    }

}
