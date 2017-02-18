package com.minimalart.studentlife.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.minimalart.studentlife.R;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by ytgab on 08.02.2017.
 */

/**
 * Setting up ACRA for sending email crash reports
 */
@ReportsCrashes(mailTo = "ytgabi98@gmail.com",
        customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.toast_crash) //you get to define resToastText
public class ApplicationMain extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //Initializing ACRA
        ACRA.init(this);
    }
}
