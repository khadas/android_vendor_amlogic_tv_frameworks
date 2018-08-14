package com.droidlogic.tvinput.services;

import com.droidlogic.tvinput.services.TvMessage;

interface IUpdateUiCallbackListener{
    void onRespond(in TvMessage msg);
}
