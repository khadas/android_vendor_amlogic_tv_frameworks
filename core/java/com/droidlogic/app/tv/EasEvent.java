package com.droidlogic.app.tv;

import android.os.Parcel;
import android.util.Log;

public class EasEvent {
    private static final String TAG = "EasEvent";
    public int    tableId;                       //table id
    public int    extension;                     //subtable id
    public int    version;                       //version_number
    public int    currentNext;                   //current_next_indicator
    public int    sequenceNum;                   //sequence version
    public int    protocolVersion;               //protocol version
    public int    easEventId;                    //eas event id
    public int[]  easOrigCode;                   //eas event orig code
    public int    easEventCodeLen;               //eas event code len
    public int[]  easEventCode;                  //eas event code
    public int    alertMessageTimeRemaining;     //alert msg remain time
    public int    eventStartTime;                //eas event start time
    public int    eventDuration;                 //event dur
    public int    alertPriority;                 //alert priority
    public int    detailsOOBSourceID;            //details oob source id
    public int    detailsMajorChannelNumber;     //details major channel num
    public int    detailsMinorChannelNumber;     //details minor channel num
    public int    audioOOBSourceID;              //audio oob source id
    public int    locationCount;                 //location count
    public Location[] location;                  //location info
    public int    exceptionCount;                //exception count
    public ExceptionList[]   exceptionList;      //exception info
    public int    multiTextCount;                //multi_text count
    public MultiStr[]   multiText;               //nature and alert multi str information structure.
    public int    descriptorTextCount;           //descriptor text count.
    public Descriptor[]   descriptor;            //descriptor structure.

    public class Location {
        public int  stateCode;
        public int  countySubdiv;
        public int  countyCode;
    }
    public class ExceptionList {
        public int  inBandRefer;
        public int  exceptionMajorChannelNumber; //the exception major channel num
        public int  exceptionMinorChannelNumber; //the exception minor channel num
        public int  exceptionOOBSourceID;        //the exception oob source id
    }
    public class MultiStr {
        public int[]   lang;                     //the language of mlti str
        public int   type;                       //the str type, alert or nature
        public int   compressionType;            //compression type
        public int   mode;                       //mode
        public int   numberBytes;                //number bytes
        public int[]   compressedStr;            //the compressed str
    }
    public class Descriptor {
        public int  tag;                         //descriptor_tag
        public int  length;                      //descriptor_length
        public int[]  data;                      //content
    }

    public void printEasEventInfo(){
        Log.i(TAG,"[EasEventInfo]"+
            "\n alertMessageTimeRemaining = "+alertMessageTimeRemaining+
            "\n alertPriority = "+alertPriority+
            "\n detailsMajorChannelNumber = "+detailsMajorChannelNumber+
            "\n detailsMinorChannelNumber = "+detailsMinorChannelNumber);
    }

    public void readEasEvent(Parcel p) {
        Log.i(TAG,"readEasEvent");
        int i, j, k;
        tableId = p.readInt();
        extension = p.readInt();
        version = p.readInt();
        currentNext = p.readInt();
        sequenceNum = p.readInt();
        protocolVersion = p.readInt();
        easEventId = p.readInt();
        easOrigCode = new int[3];
        for (j=0;j<3;j++) {
            easOrigCode[j] = p.readInt();
        }
        easEventCodeLen = p.readInt();
        if (easEventCodeLen != 0) {
            easEventCode = new int[easEventCodeLen];
            for (j=0;j<easEventCodeLen;j++)
                easEventCode[j] = p.readInt();
        }
        alertMessageTimeRemaining = p.readInt();
        eventStartTime = p.readInt();
        eventDuration = p.readInt();
        alertPriority = p.readInt();
        detailsOOBSourceID = p.readInt();
        detailsMajorChannelNumber = p.readInt();
        detailsMinorChannelNumber = p.readInt();
        audioOOBSourceID = p.readInt();
        locationCount = p.readInt();
        if (locationCount != 0) {
            location = new Location[locationCount];
            for (j=0;j<locationCount;j++) {
                location[j] = new Location();
                location[j].stateCode = p.readInt();
                location[j].countySubdiv = p.readInt();
                location[j].countyCode = p.readInt();
            }
        }
        exceptionCount = p.readInt();
        if (exceptionCount != 0) {
            exceptionList = new ExceptionList[exceptionCount];
            for (j=0;j<exceptionCount;j++) {
                exceptionList[j] = new ExceptionList();
                exceptionList[j].inBandRefer = p.readInt();
                exceptionList[j].exceptionMajorChannelNumber = p.readInt();
                exceptionList[j].exceptionMinorChannelNumber = p.readInt();
                exceptionList[j].exceptionOOBSourceID = p.readInt();
            }
        }
        multiTextCount = p.readInt();
        if (multiTextCount != 0) {
            multiText = new MultiStr[multiTextCount];
            for (j=0;j<multiTextCount;j++) {
                multiText[j] = new MultiStr();
                multiText[j].lang = new int[3];
                multiText[j].lang[0] = p.readInt();
                multiText[j].lang[1] = p.readInt();
                multiText[j].lang[2] = p.readInt();
                multiText[j].type = p.readInt();
                multiText[j].compressionType = p.readInt();
                multiText[j].mode = p.readInt();
                multiText[j].numberBytes = p.readInt();
                multiText[j].compressedStr = new int[multiText[j].numberBytes];
                for (k=0;k<multiText[j].numberBytes;k++) {
                    multiText[j].compressedStr[k] = p.readInt();
                }
            }
        }
        descriptorTextCount = p.readInt();
        if (descriptorTextCount != 0) {
            descriptor = new Descriptor[descriptorTextCount];
            for (j=0;j<descriptorTextCount;j++) {
                descriptor[j] = new Descriptor();
                descriptor[j].tag = p.readInt();
                descriptor[j].length = p.readInt();
                descriptor[j].data = new int[descriptor[j].length];
                for (k=0;k<descriptor[j].length;k++) {
                    descriptor[j].data[k] = p.readInt();
                }
            }
        }

    }
 }

