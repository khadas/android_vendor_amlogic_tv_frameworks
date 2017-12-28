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
import android.view.View;
import android.view.LayoutInflater;

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
    protected static final int MSG_SUBTITLE_SHOW = 10;
    protected static final int MSG_SUBTITLE_HIDE = 11;

    private Context mContext;
    public int mId;
    private String mInputId;
    private int mDeviceId;
    private TvInputManager mTvInputManager;
    private boolean mHasRetuned = false;
    protected Handler mSessionHandler;
    private TvControlManager mTvControlManager;
    protected DroidLogicOverlayView mOverlayView = null;

    protected boolean isBlockNoRatingEnable = false;
    protected boolean isUnlockCurrent_NR = false;

    public TvInputBaseSession(Context context, String inputId, int deviceId) {
        super(context);
        mContext = context;
        mInputId = inputId;
        mDeviceId = deviceId;

        mTvControlManager = TvControlManager.getInstance();
        mSessionHandler = new Handler(context.getMainLooper(), this);
        int block_norating = Settings.System.getInt(mContext.getContentResolver(), DroidLogicTvUtils.BLOCK_NORATING, 0);
        isBlockNoRatingEnable = block_norating == 0 ? false : true;
        if (DEBUG)
            Log.d(TAG, "isBlockNoRatingEnable = " + isBlockNoRatingEnable);
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
        //setAudiodMute(false);
        setOverlayViewEnabled(false);
        if (mOverlayView != null) {
            mOverlayView.releaseResource();
            mOverlayView = null;
        }
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

        setAudiodMute(0.0 == volume);
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

    public void initOverlayView(int resId) {
        LayoutInflater inflater = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mOverlayView = (DroidLogicOverlayView)inflater.inflate(resId, null);
        setOverlayViewEnabled(true);
    }

    @Override
    public View onCreateOverlayView() {
        return mOverlayView;
    }

    @Override
    public void onOverlayViewSizeChanged(int width, int height) {
        Log.d(TAG, "onOverlayViewSizeChanged: "+width+","+height);
    }

    @Override
    public void notifyVideoAvailable() {
        Log.d(TAG, "notifyVideoAvailable ");
        super.notifyVideoAvailable();
        if (mOverlayView != null) {
            mOverlayView.setImageVisibility(false);
            mOverlayView.setTextVisibility(false);
        }
    }

    @Override
    public void notifyVideoUnavailable(int reason) {
        Log.d(TAG, "notifyVideoUnavailable: "+reason);
        super.notifyVideoUnavailable(reason);
        if (mOverlayView != null) {
            mOverlayView.setImageVisibility(true);
            mOverlayView.setTextVisibility(false);
        }
    }

    public void hideUI() {
        if (mOverlayView != null) {
            mOverlayView.setImageVisibility(false);
            mOverlayView.setTextVisibility(false);
            mOverlayView.setSubtitleVisibility(false);
        }
    }

    private void setAudiodMute(boolean mute) {
        if (mute) {
            //SystemProperties.set("persist.sys.tvview.blocked", "true");
            mTvControlManager.setAmAudioPreMute(TvControlManager.AUDIO_MUTE_FOR_TV);
        } else {
            //SystemProperties.set("persist.sys.tvview.blocked", "false");
            mTvControlManager.setAmAudioPreMute(TvControlManager.AUDIO_UNMUTE_FOR_TV);
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (DEBUG)
            Log.d(TAG, "handleMessage, msg.what=" + msg.what);
        switch (msg.what) {
            case MSG_DO_PRI_CMD:
                doAppPrivateCmd((String)msg.obj, msg.getData());
                break;
            case MSG_SUBTITLE_SHOW:
                if (mOverlayView != null) {
                    mOverlayView.setSubtitleVisibility(true);
                }
                break;
            case MSG_SUBTITLE_HIDE:
                if (mOverlayView != null) {
                    mOverlayView.setSubtitleVisibility(false);
                }
                break;
        }
        return false;
    }
}
