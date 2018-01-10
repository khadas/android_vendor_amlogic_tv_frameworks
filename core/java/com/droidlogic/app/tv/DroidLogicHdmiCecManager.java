package com.droidlogic.app.tv;

import android.content.Context;
import android.hardware.hdmi.HdmiControlManager;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiTvClient;
import android.hardware.hdmi.HdmiTvClient.SelectCallback;
import android.provider.Settings;
import android.provider.Settings.Global;
import android.util.Log;

import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputManager;
import android.os.Handler;

import java.util.List;
import java.util.ArrayList;
import android.hardware.hdmi.HdmiDeviceInfo;

public class DroidLogicHdmiCecManager {
    private static final String TAG = "DroidLogicHdmiCecManager";

    private Context mContext;
    private HdmiControlManager mHdmiControlManager;
    private HdmiTvClient mTvClient;
    private int mSelectPort = -1;
    private final Handler mHandler = new Handler();
    private int mSourceType = 0;

    private final Object mLock = new Object();

    private static DroidLogicHdmiCecManager mInstance = null;
    private TvInputManager mTvInputManager;
    private static boolean DEBUG = true;

    public static synchronized DroidLogicHdmiCecManager getInstance(Context context) {
        if (mInstance == null) {
            Log.d(TAG, "mInstance is null...");
            mInstance = new DroidLogicHdmiCecManager(context);
        }
        Log.d(TAG, "mInstance is not null");
        return mInstance;
    }

    public DroidLogicHdmiCecManager(Context context) {
        mContext = context;
        mHdmiControlManager = (HdmiControlManager) context.getSystemService(Context.HDMI_CONTROL_SERVICE);

        if (mHdmiControlManager != null)
            mTvClient = mHdmiControlManager.getTvClient();

        if (mTvInputManager == null)
            mTvInputManager = (TvInputManager) context.getSystemService(Context.TV_INPUT_SERVICE);
    }

    /**
     * select hdmi cec port.
     * @param deviceId defined in {@link DroidLogicTvUtils} {@code DEVICE_ID_HDMI1} {@code DEVICE_ID_HDMI2}
     * {@code DEVICE_ID_HDMI3} or 0(TV).
     * @return {@value true} indicates has select device successfully, otherwise {@value false}.
     */
    public boolean selectHdmiDevice(final int deviceId) {
        synchronized (mLock) {
            getInputSourceType();

            Log.d(TAG, "selectHdmiDevice"
                + ", deviceId = " + deviceId
                + ", mSelectPort = " + mSelectPort
                + ", mSourceType = " + mSourceType);

            int devAddr = 0;
            if (mHdmiControlManager == null || mSelectPort == deviceId)
                return false;

            boolean cecOption = (Global.getInt(mContext.getContentResolver(), Global.HDMI_CONTROL_ENABLED, 1) == 1);
            if (!cecOption || mTvClient == null)
                return false;

            devAddr = getLogicalAddress(deviceId);
            Log.d(TAG, "mSelectPort = " + mSelectPort + ", devAddr = " + devAddr);

            if (mSelectPort < 0 && devAddr == 0)
                return false;

            final int addr = devAddr;
            mTvClient.deviceSelect(devAddr, new SelectCallback() {
                @Override
                public void onComplete(int result) {
                    if (addr == 0 || result != HdmiControlManager.RESULT_SUCCESS)
                        mSelectPort = 0;
                    else
                        mSelectPort = deviceId;
                    Log.d(TAG, "select device, onComplete result = " + result + ", mSelectPort = " + mSelectPort);
                }
            });
            return true;
        }
    }

    /**
     * when hdmi is disconnected or switched to another source, reset the cec selected status.
     * the function will invoked by some different works as follows.
     * 1. plug out the hdmi.
     * 2. tv has stopped because of something. such as source is switched to another.
     */
    public void disconnectHdmiCec(int deviceId) {
        synchronized (mLock) {
          //only disconnect hdmi device.
            if (deviceId < DroidLogicTvUtils.DEVICE_ID_HDMI1 || deviceId > DroidLogicTvUtils.DEVICE_ID_HDMI4)
                return;

            getInputSourceType();
            Log.d(TAG, "disconnectHdmiCec, deviceId = " + deviceId
                    + ", mSourceType = " + mSourceType
                    + ", mSelectPort = " + mSelectPort);
            selectHdmiDevice(0);
        }
    }

    public boolean activeHdmiCecSource(int deviceId) {
        synchronized (mLock) {
            getInputSourceType();

            Log.d(TAG, "activeHdmiCecSource"+ ", deviceId = " + deviceId
                + ", mSelectPort = " + mSelectPort+ ", mSourceType = " + mSourceType);

            int devAddr = 0;
            if (mHdmiControlManager == null || mSelectPort == deviceId)
                return false;

            boolean cecOption = (Global.getInt(mContext.getContentResolver(), Global.HDMI_CONTROL_ENABLED, 1) == 1);
            if (!cecOption || mTvClient == null)
                return false;



            int portId = getPortIdByDeviceId(deviceId);
            if (DEBUG)
                Log.d(TAG, "portId = " + portId);
            if (portId == 0)
                return false;
            mTvClient.portSelect(portId , new SelectCallback() {
                @Override
                public void onComplete(int result) {
                    if (result != HdmiControlManager.RESULT_SUCCESS)
                        mSelectPort = 0;
                    else
                        mSelectPort = portId ;
                    Log.d(TAG, "portSelect, onComplete result = " + result + ", mSelectPort = " + mSelectPort);
                }
            });

            return true;
        }
    }

    private int getPortIdByDeviceId(int deviceId){
        List<TvInputHardwareInfo> hardwareList = mTvInputManager.getHardwareList();
        if (hardwareList == null || hardwareList.size() == 0)
            return -1;
        Log.d(TAG, "getPortIdByDeviceId: " + deviceId);
        for (TvInputHardwareInfo hardwareInfo : hardwareList) {
            if (DEBUG)
                Log.d(TAG, "getPortIdByDeviceId: " + hardwareInfo);
            if (deviceId == hardwareInfo.getDeviceId())
                return hardwareInfo.getHdmiPortId();
        }
        return -1;
    }

    public int getLogicalAddress (int deviceId) {
        if (deviceId >= DroidLogicTvUtils.DEVICE_ID_HDMI1 && deviceId <= DroidLogicTvUtils.DEVICE_ID_HDMI4) {
            int id = deviceId - DroidLogicTvUtils.DEVICE_ID_HDMI1 + 1;
            for (HdmiDeviceInfo info : mTvClient.getDeviceList()) {
                Log.d(TAG, "getLogicalAddress: " + info);
                if (id == (info.getPhysicalAddress() >> 12)) {
                    return info.getLogicalAddress();
                }
            }
        }
        return 0;
    }

    public int getPhysicalAddress (int deviceId) {
        if (deviceId >= DroidLogicTvUtils.DEVICE_ID_HDMI1 && deviceId <= DroidLogicTvUtils.DEVICE_ID_HDMI4) {
            int id = deviceId - DroidLogicTvUtils.DEVICE_ID_HDMI1 + 1;
            for (HdmiDeviceInfo info : mTvClient.getDeviceList()) {
                if (id == (info.getPhysicalAddress() >> 12)) {
                    return info.getPhysicalAddress();
                }
            }
        }
        return 0;
    }

    public boolean hasHdmiCecDevice(int deviceId) {
        if (deviceId >= DroidLogicTvUtils.DEVICE_ID_HDMI1 && deviceId <= DroidLogicTvUtils.DEVICE_ID_HDMI4) {
            int id = deviceId - DroidLogicTvUtils.DEVICE_ID_HDMI1 + 1;
            for (HdmiDeviceInfo info : mTvClient.getDeviceList()) {
                Log.d(TAG, "hasHdmiCecDevice: " + info);
                if (id == (info.getPhysicalAddress() >> 12)) {
                    return true;
                }
            }
        }
        return false;
    }

    public int getInputSourceType() {
        mSourceType = Settings.System.getInt(mContext.getContentResolver(), DroidLogicTvUtils.TV_CURRENT_DEVICE_ID, 0);
        return mSourceType;
    }

    public boolean  isHdmiCecDeviceConneted(int  deviceId){
        Log.d(TAG, "isHdmiCecDeviceConneted,deviceId: " + deviceId);
        int portId = getPortIdByDeviceId(deviceId);
        Log.d(TAG, "portId: " + portId);
        for (HdmiDeviceInfo info : mTvClient.getDeviceList()) {
            if (DEBUG) Log.d(TAG, "info" + info.toString());
                if (info.getPortId() == portId)
                    return true;
        }
        return false;
    }
}
