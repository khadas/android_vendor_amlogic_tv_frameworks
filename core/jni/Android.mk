LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

LIB_TV_BINDER_PATH := $(LOCAL_PATH)/../../libtvbinder

LOCAL_SRC_FILES:= \
	com_droidlogic_app_tv_TvControlManager.cpp
LOCAL_SHARED_LIBRARIES := \
	libcutils \
	libutils \
	libbinder \
	libtvbinder \
	libnativehelper \
	libandroid_runtime \
	liblog \
	libskia \
	libhardware

LOCAL_C_INCLUDES += \
    frameworks/base/core/jni \
    frameworks/base/core/jni/android/graphics \
    frameworks/base/libs/hwui \
    $(LIB_TV_BINDER_PATH)/include \
    external/skia/include \

LOCAL_MODULE:= libtv_jni
LOCAL_PRELINK_MODULE := false
include $(BUILD_SHARED_LIBRARY)
