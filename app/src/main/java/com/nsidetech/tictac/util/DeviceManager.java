package com.nsidetech.tictac.util;

import android.app.Activity;

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

        String deviceId = null;
        if (dbManager.fetch().getCount() > 0)
        {
            deviceId = dbManager.fetch().getString(1);
        }

        if (deviceId == null)
        {
            deviceId = UUID.randomUUID().toString();
            dbManager.insert(deviceId);
        }
        dbManager.close();

        DEVICE_ID = deviceId;
    }
}
