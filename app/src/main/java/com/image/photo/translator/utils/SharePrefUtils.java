package com.image.photo.translator.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

public class SharePrefUtils {

    private static SharedPreferences mSharePref;
    public static void init(Context context) {
        if (mSharePref == null) {
            mSharePref = PreferenceManager.getDefaultSharedPreferences(context);
        }
    }

    public static int  getCountOpenApp(Context context) {
        SharedPreferences pre = context.getSharedPreferences("dataAudio", Context.MODE_PRIVATE);
        return pre.getInt("counts", 0);
    }
    public static void increaseCountOpenApp(Context context) {
        SharedPreferences pre = context.getSharedPreferences("dataAudio", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("counts", pre.getInt("counts", 0) + 1);
        editor.apply();
    }

    public static int  getCountOpenFirstHelp(Context context) {
        SharedPreferences pre = context.getSharedPreferences("dataAudio", Context.MODE_PRIVATE);
        return pre.getInt("first", 0);
    }
    public static void increaseCountFirstHelp(Context context) {
        SharedPreferences pre = context.getSharedPreferences("dataAudio", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("first", pre.getInt("first", 0) + 1);
        editor.apply();
    }

    private static void LogTroasFirebaseAdRevenueEvent(Context context, float TroasCache) {
        Bundle bundle = new Bundle();
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, TroasCache);//(Required)tROAS event must include Double Value
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "USD");//put in the correct currency
        FirebaseAnalytics.getInstance(context).logEvent("Daily_Ads_Revenue", bundle);
    }

    public static boolean isRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getBoolean("rated", false);
    }

    public static void forceRated(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putBoolean("rated", true);
        editor.apply();
    }

    public static void setCountFirstBackHome(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("counts_back_home", 0);
        editor.apply();
    }

    public static int getCountBackHome(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        return pre.getInt("counts_back_home", 0);
    }

    public static void increaseCountBackHome(Context context) {
        SharedPreferences pre = context.getSharedPreferences("data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.putInt("counts_back_home", pre.getInt("counts_back_home", 0) + 1);
        editor.apply();
    }

    public static void setFirstClick(Context context, boolean open) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("KEY_OPEN_FIRST_CLICK", open);
        editor.apply();
    }
    public static void setFirstClick2(Context context, boolean open) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("KEY_OPEN_FIRST_CLICK2", open);
        editor.apply();
    }
    public static void setFirstClick3(Context context, boolean open) {
        SharedPreferences preferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("KEY_OPEN_FIRST_CLICK3", open);
        editor.apply();
    }
    public static boolean getFirstClick(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        return preferences.getBoolean("KEY_OPEN_FIRST_CLICK", false);
    }
    public static boolean getFirstClick2(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        return preferences.getBoolean("KEY_OPEN_FIRST_CLICK2", false);
    }
    public static boolean getFirstClick3(Context mContext) {
        SharedPreferences preferences = mContext.getSharedPreferences(mContext.getPackageName(), Context.MODE_PRIVATE);
        return preferences.getBoolean("KEY_OPEN_FIRST_CLICK3", false);
    }
}
