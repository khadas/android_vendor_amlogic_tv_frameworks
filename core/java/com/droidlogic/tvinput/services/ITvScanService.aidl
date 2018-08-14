package com.droidlogic.tvinput.services;

import android.content.Intent;
import com.droidlogic.tvinput.services.IUpdateUiCallbackListener;

interface ITvScanService{
    void init(in Intent intent);

    void setAtsccSearchSys (int value);

    void startAutoScan();

    void startManualScan();

    void setSearchSys(boolean value1, boolean value2);

    void setFrequency(String value1, String value2);

    void release();

    void registerListener(IUpdateUiCallbackListener listener);

    void unregisterListener(IUpdateUiCallbackListener listener);
}
