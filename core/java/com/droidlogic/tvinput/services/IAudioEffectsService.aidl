/*
 * Copyright (C) 2019 Amlogic Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.droidlogic.tvinput.services;

interface IAudioEffectsService{
    int getSoundModeStatus();
    int getSoundModule();
    int getTrebleStatus();
    int getBassStatus();
    int getBalanceStatus();
    int getSurroundStatus();
    int getDialogClarityStatus();
    int getBassBoostStatus();
    boolean getAgcEnableStatus();
    int getAgcMaxLevelStatus();
    int getAgcAttrackTimeStatus();
    int getAgcReleaseTimeStatus();
    int getAgcSourceIdStatus();
    int getVirtualSurroundStatus();
    void setSoundMode(int mode);
    void setSoundModeByObserver(int mode);
    void setDifferentBandEffects(int bandnum, int value, boolean needsave);
    void setTreble(int step);
    void setBass(int step);
    void setBalance(int step);
    void setSurround(int mode);
    void setDialogClarity(int mode);
    void setBassBoost(int mode);
    void setAgsEnable(int mode);
    void setAgsMaxLevel(int step);
    void setAgsAttrackTime(int step);
    void setAgsReleaseTime(int step);
    void setSourceIdForAvl(int step);
    void setVirtualSurround(int mode);
    void setParameters(int order, int value);
    int getParameters(int order);
    void cleanupAudioEffects();
    void initSoundEffectSettings();
    void resetSoundEffectSettings();
}
