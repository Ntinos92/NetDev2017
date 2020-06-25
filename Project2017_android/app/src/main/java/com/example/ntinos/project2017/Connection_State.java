package com.example.ntinos.project2017;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by MANOS on 14/2/2018.
 */

public class Connection_State {
    public Context context;
    public Connection_State(Context c){
        this.context=c;
    }
    public boolean isConnectedWifi(){
        ConnectivityManager Connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = Connectivity.getActiveNetworkInfo();
        if (info != null  && info.getType() == ConnectivityManager.TYPE_WIFI){
            return true;
        }
        return false;
    }

}
