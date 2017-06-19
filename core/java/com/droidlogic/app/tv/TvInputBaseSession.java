package com.droidlogic.app.tv;

import android.content.Context;
import android.media.tv.TvContentRating;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.media.tv.TvStreamConfig;
import android.media.tv.TvInputManager.Hardware;
import android.provider.Settings;
//import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;

import com.droidlogic.app.tv.DroidLogicTvUtils;
import com.droidlogic.app.tv.TvControlManager;

import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiTvClient;
import android.provider.Settings.Global;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiTvClient.SelectCallback;

public abstract class TvInputBaseSession extends TvInputService.Session implements Handler.Callback {
    private static final boolean DEBUG = true;
    private static final String TAG = "TvInputBaseSession";

    private static final int MSG_DO_PRI_CMD = 9;

    private Context mContext;
    public int mId;
    private String mInputId;
    private int mDeviceId;
    private TvInputManager mTvInputManager;
    private boolean mHasRetuned = false;
    private Handler mSessionHandler;
    private TvControlManager mTvControlManager;

    public TvInputBaseSession(Context context, String inputId, int deviceId) {
        super(context);
        mContext = context;
        mInputId = inputId;
        mDeviceId = deviceId;

        mTvControlManager = TvControlManager.getInstance();
        mSessionHandler = new Handler(context.getMainLooper(), this);
    }

    public void setSessionId(int id) {
        mId = id;
    }

    public int getSessionId() {
        return mId;
    }

    public String getInputId() {
        return mInputId;
    }

    public int getDeviceId() {
        return mDeviceId;
    }

    public void doRelease() {
        Log.d(TAG, "doRelease");
        SystemProperties.set("persist.sys.tvview.blocked", "false");
    }

    public void doAppPrivateCmd(String action, Bundle bundle) {}
    public void doUnblockContent(TvContentRating rating) {}

    @Override
    public void onRelease() {
        doRelease();
    }

    @Override
    public void onSurfaceChanged(int format, int width, int height) {
    }

    @Override
    public void onSetStreamVolume(float volume) {
        if (DEBUG)
            Log.d(TAG, "onSetStreamVolume volume = " + volume);

        if ( 0.0 == volume ) {
            SystemProperties.set("persist.sys.tvview.blocked", "true");
            mTvControlManager.SetAudioMuteForTv(TvControlManager.AUDIO_MUTE_FOR_TV);
            mTvControlManager.SetAudioMuteKeyStatus(TvControlManager.AUDIO_MUTE_FOR_TV);
        } else {
            SystemProperties.set("persist.sys.tvview.blocked", "false");
            mTvControlManager.SetAudioMuteForTv(TvControlManager.AUDIO_UNMUTE_FOR_TV);
            mTvControlManager.SetAudioMuteKeyStatus(TvControlManager.AUDIO_UNMUTE_FOR_TV);
        }
    }

    @Override
    public void onAppPrivateCommand(String action, Bundle data) {
        if (DEBUG)
            Log.d(TAG, "onAppPrivateCommand, action = " + action);

        if (mSessionHandler == null)
            return;
        Message msg = mSessionHandler.obtainMessage(MSG_DO_PRI_CMD);
        msg.setData(data);
        msg.obj = action;
        msg.sendToTarget();
    }

    @Override
    public void onSetCaptionEnabled(boolean enabled) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onUnblockContent(TvContentRating unblockedRating) {
        if (DEBUG)
            Log.d(TAG, "onUnblockContent");

        doUnblockContent(unblockedRating);
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (DEBUG)
            Log.d(TAG, "handleMessage, msg.what=" + msg.what);
        switch (msg.what) {
            case MSG_DO_PRI_CMD:
                doAppPrivateCmd((String)msg.obj, msg.getData());
                break;
        }
        return false;
    }
}
