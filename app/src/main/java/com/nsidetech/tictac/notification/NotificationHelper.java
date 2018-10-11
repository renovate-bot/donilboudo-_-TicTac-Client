package com.nsidetech.tictac.notification;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by fabrice on 2017-06-09.
 */
public class NotificationHelper {
    private static final String NEWS_TOPIC = "news";

    private static NotificationHelper instance = new NotificationHelper();

    public static NotificationHelper getInstance() {
        return instance;
    }

    private NotificationHelper() {
    }

    public void subscribeToTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(NEWS_TOPIC);
    }
}
