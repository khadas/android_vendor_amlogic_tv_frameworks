package com.droidlogic.app.tv;

import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiTvClient;
import android.content.Context;
import android.os.SystemProperties;
import android.util.Slog;

public class TvHdmiArc {
    private static final String TAG = "TvHdmiArc";
    private Context mContext = null;

    public TvHdmiArc(Context context) {
        mContext = context;
    }

    public void setArcEnable(boolean enable) {
        HdmiControlManager hcm = (HdmiControlManager) mContext.getSystemService(Context.HDMI_CONTROL_SERVICE);
        HdmiTvClient tv = hcm.getTvClient();
        if (tv == null)
            return;
        // String s = String.valueOf(enable);
        // SystemProperties.set("persist.sys.arc.enable", s);
        Slog.d(TAG, "setArcEnable:" + enable);
        tv.setArcMode(enable);
    }

    public boolean getArcEnabled() {
        boolean arcEnable = SystemProperties.getBoolean("persist.sys.arc.enable", true);
        Slog.d(TAG, "getArcEnabled:" + arcEnable);
        return arcEnable;
    }
}
