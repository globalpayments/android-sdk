package com.globalpayments.android.sdk.utils;

import android.app.Activity;
import android.content.Intent;

public class IntentUtils {

    public static boolean canBeHandled(Intent intent, Activity activity) {
        return intent.resolveActivity(activity.getPackageManager()) != null;
    }
}
