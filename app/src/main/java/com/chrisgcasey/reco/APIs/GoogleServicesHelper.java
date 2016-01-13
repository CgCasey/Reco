package com.chrisgcasey.reco.APIs;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;

import com.chrisgcasey.reco.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.*;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.plus.Plus;

import java.util.concurrent.TimeUnit;

/**
 * Created by chris on 1/7/2016.
 */
public class GoogleServicesHelper implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_CODE_RESOLUTION = -100;
    private static final int REQUEST_CODE_AVAILABILITY = -101;
    private GoogleServicesListener listener;
    private GoogleApiClient apiClient;
    private static Activity activity;

    public GoogleServicesHelper(Activity activity, GoogleServicesListener listener) {
        this.listener = listener;
        this.activity = activity;
        this.apiClient = new GoogleApiClient.Builder(activity)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API, Plus.PlusOptions.builder()
                .setServerClientId(Constants.GOOGLE_CLIENT_ID).build())
        .build();
    }

    public void connect() {
        if (isGooglePlayServicesAvailable()) {
            apiClient.connect();
        }
    }

    public void disconnect() {
        if (isGooglePlayServicesAvailable()) {
            apiClient.disconnect();
        }
    }
    //method to check availablility of google play services
    public static boolean isGooglePlayServicesAvailable(){
        // if play services are available pass return true
        int availablity = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        switch (availablity){
            case ConnectionResult.SUCCESS:
                return true;
            // otherwise let user know with dialog that there is an error
            case ConnectionResult.SERVICE_DISABLED:
            case ConnectionResult.SERVICE_INVALID:
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                GooglePlayServicesUtil.getErrorDialog(
                        availablity, activity, REQUEST_CODE_AVAILABILITY).show();
                return false;
            default:
                return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        //connection successful; inform activity
        listener.onConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //connection not successful; inform activity
        listener.onDisconnected();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //if connection fails check to see if there is a resolution
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(activity, REQUEST_CODE_RESOLUTION);
            } catch (IntentSender.SendIntentException e) {
                connect();
                e.printStackTrace();
            }
        }else {
            // otherwise operate without play services
            listener.onDisconnected();
        }

    }
    //handle the response from the calls to startactivityforresult()
    public void handleActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_CODE_RESOLUTION || requestCode == REQUEST_CODE_AVAILABILITY){
            //if the resultcode is "ok" try to connect again
            if (resultCode == Activity.RESULT_OK) {
                connect();
            }
        }
        else { //otherwise operate without play services
            listener.onDisconnected();
        }

    }

    //interface for communicating connection status with mainactivity
    public interface GoogleServicesListener {
        void onConnected();
        void onDisconnected();

    }
}
