package com.mentorandroid.beerorcoffee.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by Bruno Hauck on 16/12/2015.
 * This is a class all global project static functions
 */
public class Util {

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return conectivtyManager.getActiveNetworkInfo() != null && conectivtyManager.getActiveNetworkInfo().isAvailable();
    }

}
