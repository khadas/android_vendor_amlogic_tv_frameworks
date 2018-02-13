package com.droidlogic.app.tv;

import android.content.Context;
import android.provider.Settings;
import android.os.SystemClock;
import android.util.Log;

import java.util.Date;

import com.droidlogic.app.SystemControlManager;
import com.droidlogic.app.DaylightSavingTime;

public class TvTime{
    private long diff = 0;
    private Context mContext;
    private SystemControlManager mSystemControlManager;

    private final static String TV_KEY_TVTIME = "dtvtime";
    private final static String PROP_SET_SYSTIME_ENABLED = "persist.sys.getdtvtime.isneed";


    public TvTime(Context context){
        mContext = context;
        mSystemControlManager = new SystemControlManager(mContext);
    }

    public synchronized void setTime(long time){
        Date sys = new Date();

        diff = time - sys.getTime();
        if (mSystemControlManager.getPropertyBoolean(PROP_SET_SYSTIME_ENABLED, false)
                && (Math.abs(diff) > 1000)) {
            SystemClock.setCurrentTimeMillis(time);
            diff = 0;
            DaylightSavingTime daylightSavingTime = DaylightSavingTime.getInstance();
            daylightSavingTime.updateDaylightSavingTimeForce();
            Settings.Global.putInt(mContext.getContentResolver(), Settings.Global.AUTO_TIME, 0);
            Log.d("DroidLogic", "setTime");
        }

        Settings.System.putLong(mContext.getContentResolver(), TV_KEY_TVTIME, diff);
    }


    public synchronized long getTime(){
        Date sys = new Date();
        diff = Settings.System.getLong(mContext.getContentResolver(), TV_KEY_TVTIME, 0);

        return sys.getTime() + diff;
    }


    public synchronized long getDiffTime(){
        return Settings.System.getLong(mContext.getContentResolver(), TV_KEY_TVTIME, 0);
    }

    public synchronized void setDiffTime(long diff){
        this.diff = diff;
        Settings.System.putLong(mContext.getContentResolver(), TV_KEY_TVTIME, this.diff);
    }
}

