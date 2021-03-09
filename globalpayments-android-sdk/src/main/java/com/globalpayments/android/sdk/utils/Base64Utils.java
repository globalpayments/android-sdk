package com.globalpayments.android.sdk.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Base64;

import java.io.InputStream;

import static com.globalpayments.android.sdk.utils.Strings.EMPTY;

public class Base64Utils {

    public static String getBase64EncodedContent(Uri uri, Context context) {
        String base64EncodedContent = EMPTY;

        InputStream inputStream = ContextUtils.getInputStreamForUri(uri, context);
        byte[] byteArray = FileUtils.readIntoByteArray(inputStream);

        if (byteArray != null) {
            base64EncodedContent = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

        return base64EncodedContent;
    }
}
