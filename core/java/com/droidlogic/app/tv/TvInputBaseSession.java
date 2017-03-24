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
    }

    public void doAppPrivateCmd(String action, Bundle bundle) {
        if (DroidLogicTvUtils.ACTION_ATV_AUTO_SCAN.equals(action)) {
            mTvControlManager.AtvAutoScan(
                (bundle == null ? TvControlManager.ATV_VIDEO_STD_PAL
                    : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA1, TvControlManager.ATV_VIDEO_STD_PAL)),
                (bundle == null ? TvControlManager.ATV_AUDIO_STD_DK
                    : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA2, TvControlManager.ATV_AUDIO_STD_DK)),
                (bundle == null ? 0 : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA3, 0)),
                (bundle == null ? 1 : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA4, 1)));
        } else if (DroidLogicTvUtils.ACTION_ATV_MANUAL_SCAN.equals(action)) {
            if (bundle != null) {
                mTvControlManager.AtvManualScan(
                    bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA1, 0),
                    bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA2, 0),
                    bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA3, TvControlManager.ATV_VIDEO_STD_PAL),
                    bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA4, TvControlManager.ATV_AUDIO_STD_DK));
            }
        } else if (DroidLogicTvUtils.ACTION_DTV_AUTO_SCAN.equals(action)) {
            mTvControlManager.DtvSetTextCoding("GB2312");
            int dtvMode = (bundle == null ? TVChannelParams.MODE_DTMB
                    : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_MODE, TVChannelParams.MODE_DTMB));
            TvControlManager.TvMode tvMode = TvControlManager.TvMode.fromMode(dtvMode);
            if ((tvMode.getExt() & 1) != 0) {//ADTV
                int atvScanType = (bundle == null ? TvControlManager.ScanType.SCAN_ATV_NONE
                        : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_TYPE_ATV, TvControlManager.ScanType.SCAN_ATV_NONE));
                int dtvScanType = (bundle == null ? TvControlManager.ScanType.SCAN_DTV_NONE
                        : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_TYPE_DTV, TvControlManager.ScanType.SCAN_DTV_NONE));
                int atvFreq1 = (bundle == null ? 0 : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA1, 0));
                int atvFreq2 = (bundle == null ? 0 : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA2, 0));
                int dtvFreq = (bundle == null ? 0 : bundle.getInt(DroidLogicTvUtils.PARA_MANUAL_SCAN, 0));
                int atvVideoStd = (bundle == null ? TvControlManager.ATV_VIDEO_STD_PAL
                        : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA3, TvControlManager.ATV_VIDEO_STD_PAL));
                int atvAudioStd = (bundle == null ? TvControlManager.ATV_AUDIO_STD_DK
                        : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA4, TvControlManager.ATV_AUDIO_STD_DK));
                TvControlManager.FEParas fe = new TvControlManager.FEParas();
                fe.setMode(tvMode);
                fe.setVideoStd(atvVideoStd);
                fe.setAudioStd(atvAudioStd);
                TvControlManager.ScanParas scan = new TvControlManager.ScanParas();
                scan.setMode(TvControlManager.ScanParas.MODE_DTV_ATV);
                scan.setAtvMode(atvScanType);
                scan.setDtvMode(dtvScanType);
                scan.setAtvFrequency1(atvFreq1);
                scan.setAtvFrequency2(atvFreq2);
                scan.setDtvFrequency1(dtvFreq);
                scan.setDtvFrequency2(dtvFreq);
                mTvControlManager.TvScan(fe, scan);
            } else {
                mTvControlManager.DtvAutoScan(dtvMode);
            }
        } else if (DroidLogicTvUtils.ACTION_DTV_MANUAL_SCAN.equals(action)) {
            mTvControlManager.DtvSetTextCoding("GB2312");
            int dtvMode = (bundle == null ? TVChannelParams.MODE_DTMB
                    : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_MODE, TVChannelParams.MODE_DTMB));
            TvControlManager.TvMode tvMode = TvControlManager.TvMode.fromMode(dtvMode);
            if ((tvMode.getExt() & 1) != 0) {//ADTV
                int atvScanType = (bundle == null ? TvControlManager.ScanType.SCAN_ATV_NONE
                        : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_TYPE_ATV, TvControlManager.ScanType.SCAN_ATV_NONE));
                int dtvScanType = (bundle == null ? TvControlManager.ScanType.SCAN_DTV_NONE
                        : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_TYPE_DTV, TvControlManager.ScanType.SCAN_DTV_NONE));
                int atvFreq1 = (bundle == null ? 0 : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA1, 0));
                int atvFreq2 = (bundle == null ? 0 : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA2, 0));
                int dtvFreq = (bundle == null ? 0 : bundle.getInt(DroidLogicTvUtils.PARA_MANUAL_SCAN, 0));
                int atvVideoStd = (bundle == null ? TvControlManager.ATV_VIDEO_STD_PAL
                        : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA3, TvControlManager.ATV_VIDEO_STD_PAL));
                int atvAudioStd = (bundle == null ? TvControlManager.ATV_AUDIO_STD_DK
                        : bundle.getInt(DroidLogicTvUtils.PARA_SCAN_PARA4, TvControlManager.ATV_AUDIO_STD_DK));

                if (atvScanType != TvControlManager.ScanType.SCAN_ATV_NONE
                        && (atvFreq1 == 0 || atvFreq2 == 0)) {
                    atvFreq1 = dtvFreq - 9750000;
                    atvFreq2 = dtvFreq + 1250000;
                }
                TvControlManager.FEParas fe = new TvControlManager.FEParas();
                fe.setMode(tvMode);
                fe.setVideoStd(atvVideoStd);
                fe.setAudioStd(atvAudioStd);
                TvControlManager.ScanParas scan = new TvControlManager.ScanParas();
                scan.setMode(TvControlManager.ScanParas.MODE_DTV_ATV);
                scan.setAtvMode(atvScanType);
                scan.setDtvMode(dtvScanType);
                scan.setAtvFrequency1(atvFreq1);
                scan.setAtvFrequency2(atvFreq2);
                scan.setDtvFrequency1(dtvFreq);
                scan.setDtvFrequency2(dtvFreq);
                //scan.setDtvStandard(TvControlManager.ScanParas.DTVSTD_ATSC);
                mTvControlManager.TvScan(fe, scan);
            } else {
                mTvControlManager.DtvManualScan(
                    bundle.getInt(DroidLogicTvUtils.PARA_SCAN_MODE, TVChannelParams.MODE_DTMB),
                    bundle.getInt(DroidLogicTvUtils.PARA_MANUAL_SCAN, 0)
                );
            }
        } else if (DroidLogicTvUtils.ACTION_STOP_SCAN.equals(action)) {
            mTvControlManager.DtvStopScan();
        } else if (DroidLogicTvUtils.ACTION_ATV_PAUSE_SCAN.equals(action)) {
            mTvControlManager.AtvDtvPauseScan();
        } else if (DroidLogicTvUtils.ACTION_ATV_RESUME_SCAN.equals(action)) {
            mTvControlManager.AtvDtvResumeScan();
        }
    }

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
        // TODO Auto-generated method stub
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
