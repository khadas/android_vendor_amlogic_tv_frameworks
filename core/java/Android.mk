LOCAL_PATH:= $(call my-dir)

# the library
# ============================================================
include $(CLEAR_VARS)

LOCAL_SRC_FILES := \
  $(call all-subdir-java-files)

LOCAL_MODULE := droidlogic-tv
LOCAL_MODULE_CLASS := JAVA_LIBRARIES
LOCAL_DX_FLAGS := --core-library

LOCAL_JAVA_LIBRARIES := droidlogic

include $(BUILD_JAVA_LIBRARY)
