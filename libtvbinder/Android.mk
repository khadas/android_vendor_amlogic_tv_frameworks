LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LOCAL_SRC_FILES:= \
        TvClient.cpp \
        ITv.cpp \
        ITvClient.cpp \
        ITvService.cpp

LOCAL_SHARED_LIBRARIES := \
        libcutils \
        liblog \
        libutils \
        libbinder \
        libui

LOCAL_MODULE:= libtvbinder

ifeq ($(shell test $(PLATFORM_SDK_VERSION) -ge 26 && echo OK),OK)
LOCAL_PROPRIETARY_MODULE := true
endif

#LOCAL_PRELINK_MODULE := false
include $(BUILD_SHARED_LIBRARY)
