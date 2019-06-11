package com.nsidetech.tictac.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.nsidetech.tictac.activities.SplashActivity;
import com.nsidetech.tictac.database.DBManager;

import java.util.UUID;

/**
 * Created by fabrice on 2017-05-28.
 */

public class DeviceManager {
    private static DeviceManager instance;
    public static String DEVICE_ID;

    private DeviceManager() {
    }

    public static DeviceManager getInstance() {
        if (instance == null)
            instance = new DeviceManager();

        return instance;
    }

    public String getDeviceId(Activity activity) {
        if (DEVICE_ID == null)
        {
            createDeviceId(activity);
        }

        return DEVICE_ID;
    }

    public void createDeviceId(Activity activity) {
        DBManager dbManager = new DBManager(activity);
        dbManager.open();

        String deviceId = dbManager.fetch().getString(1);

        if (deviceId == null)
        {
            deviceId = UUID.randomUUID().toString();
            dbManager.insert(deviceId);
        }
        dbManager.close();

        DEVICE_ID = deviceId;
    }
}
