package com.evertecinc.athmovil.sdk;

import android.app.Application;

public class App extends Application {

    public App() { super(); }

    private static App applicationInstance;

    public static App getApplicationInstance() {
        return applicationInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationInstance = this;
    }
}
