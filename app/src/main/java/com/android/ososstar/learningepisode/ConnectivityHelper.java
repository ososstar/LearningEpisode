package com.android.ososstar.learningepisode;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ConnectivityHelper {
    /**
     * check if network is Connected or not
     */

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return (activeNetwork != null && activeNetwork.isConnected());
        } else {
            return false;
        }
    }


//    public static boolean isNetworkAvailable(Context context) {
////        ConnectivityManager connectivityManager = (ConnectivityManager) context
////                .getSystemService(Context.CONNECTIVITY_SERVICE);
////        return (connectivityManager
////                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
////                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
////                || (connectivityManager
////                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
////                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
////                .getState() == NetworkInfo.State.CONNECTED);
//        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//
//        if (activeNetwork != null) {
//            // connected to the internet
//            // connected to wifi
//            return activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
//        } else {
//            // not connected to the internet
//            return false;
//        }
//
//    }

}
