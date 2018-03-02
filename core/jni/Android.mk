LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LIB_TV_BINDER_PATH := $(wildcard $(BOARD_AML_VENDOR_PATH)/tv/frameworks/libtvbinder)

LOCAL_SRC_FILES:= \
    com_droidlogic_app_tv_TvControlManager.cpp

LOCAL_SHARED_LIBRARIES := \
    vendor.amlogic.hardware.tvserver@1.0 \
    libcutils \
    libutils \
    libbinder \
    libtvbinder \
    libnativehelper \
    libandroid_runtime \
    liblog \
    libhardware

LOCAL_C_INCLUDES += \
    frameworks/base/core/jni \
    frameworks/base/core/jni/android/graphics \
    frameworks/base/libs/hwui \
    $(LIB_TV_BINDER_PATH)/include \
    external/skia/include \
    external/skia/include/core \
    external/skia/include/config \
    libnativehelper/include/nativehelper

LOCAL_MODULE:= libtv_jni
LOCAL_PRELINK_MODULE := false

ifeq ($(shell test $(PLATFORM_SDK_VERSION) -ge 26 && echo OK),OK)
LOCAL_PROPRIETARY_MODULE := true
endif

include $(BUILD_SHARED_LIBRARY)
