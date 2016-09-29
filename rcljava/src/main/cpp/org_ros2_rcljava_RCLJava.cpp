#include <jni.h>
#include <string>
#include <cassert>
#include <cstdlib>

#include <rmw/rmw.h>
#include <rcl/error_handling.h>
#include <rcl/rcl.h>
#include <rcl/node.h>

#include <rosidl_generator_c/message_type_support.h>

#include "org_ros2_rcljava_RCLJava.h"
#include "utils.h"

/*
 *
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_RCLJava_nativeRCLJavaInit
  (JNIEnv *env, jclass, jobjectArray arg) {

  // TODO(esteve): parse args
  int argc = arg != NULL ? env->GetArrayLength(arg) : 0;
  char ** argv = nullptr;
  rcl_allocator_t allocator = rcl_get_default_allocator();

  rcl_ret_t ret = rcl_init(argc, argv, allocator);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to init: " +
        std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

/*
 *
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_RCLJava_nativeShutdown
  (JNIEnv *env, jclass) {

  rcl_ret_t ret = rcl_shutdown();
  if (ret != RCL_RET_OK) {
    std::string message("Failed to shutdown: " +
        std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

/*
 *
 */
JNIEXPORT jboolean
JNICALL Java_org_ros2_rcljava_RCLJava_nativeOk
  (JNIEnv *, jclass) {

  return rcl_ok();
}

/*
 *
 */
JNIEXPORT jstring
JNICALL Java_org_ros2_rcljava_RCLJava_nativeGetRMWIdentifier
  (JNIEnv *env, jclass) {

  const char * rmw_implementation_identifier = rmw_get_implementation_identifier();

  return env->NewStringUTF(rmw_implementation_identifier);
}


/*
 *
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_RCLJava_nativeCreateNodeHandle
  (JNIEnv *env, jclass, jstring jnode_name) {

  std::string node_name = jstring2String(env, jnode_name);

  rcl_node_t *node = makeInstance<rcl_node_t>();
  node->impl = nullptr;

  rcl_node_options_t default_options = rcl_node_get_default_options();

  rcl_ret_t ret = rcl_node_init(node, node_name.c_str(), &default_options);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to create node: " +
        std::string(rcl_get_error_string_safe()));
    throwException(env, message);

    return -1;
  }

  jlong node_handle = instance2Handle(node);
  return node_handle;
}



/*
 *
 */
JNIEXPORT jlong
JNICALL Java_org_ros2_rcljava_RCLJava_nativeGetZeroInitializedWaitSet
  (JNIEnv *, jclass) {

  // ~ rcl_get_zero_initialized_wait_set();
  rcl_wait_set_t * wait_set = makeInstance<rcl_wait_set_t>();
  wait_set->subscriptions = nullptr;
  wait_set->size_of_subscriptions = 0;
  wait_set->guard_conditions = nullptr;
  wait_set->size_of_guard_conditions = 0;
  wait_set->timers = nullptr;
  wait_set->size_of_timers = 0;
  wait_set->clients = nullptr;
  wait_set->size_of_clients = 0;
  wait_set->services = nullptr;
  wait_set->size_of_services = 0;
  wait_set->impl = nullptr;

  jlong wait_set_handle = instance2Handle(wait_set);
  return wait_set_handle;
}

/*
 *
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_RCLJava_nativeWaitSetInit(
    JNIEnv *env,
    jclass ,
     jlong wait_set_handle,
      jint number_of_subscriptions,
      jint number_of_guard_conditions,
      jint number_of_timers,
      jint number_of_clients,
      jint number_of_services
    ) {

  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);

  rcl_ret_t ret = rcl_wait_set_init(
    wait_set, number_of_subscriptions,
    number_of_guard_conditions,
    number_of_timers,
    number_of_clients,
    number_of_services,
    rcl_get_default_allocator());

  if (ret != RCL_RET_OK) {
    std::string message("Failed to initialize wait set: " +
        std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

/*
 *
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_RCLJava_nativeWaitSetClearSubscriptions
  (JNIEnv *env, jclass, jlong wait_set_handle) {

  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);

  rcl_ret_t ret = rcl_wait_set_clear_subscriptions(wait_set);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to clear subscriptions from wait set: " +
        std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

/*
 *
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_RCLJava_nativeWaitSetAddSubscription
  (JNIEnv *env, jclass, jlong wait_set_handle, jlong subscription_handle) {

  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);
  rcl_subscription_t * subscription =
      handle2Instance<rcl_subscription_t>(subscription_handle);

  rcl_ret_t ret = rcl_wait_set_add_subscription(wait_set, subscription);
  if (ret != RCL_RET_OK) {
    std::string message("Failed to add subscription to wait set: " +
        std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

/*
 *
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_RCLJava_nativeWait
  (JNIEnv *env, jclass, jlong wait_set_handle) {

  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);

  rcl_ret_t ret = rcl_wait(wait_set, RCL_S_TO_NS(1));
  if (ret != RCL_RET_OK && ret != RCL_RET_TIMEOUT) {
    std::string message("Failed to wait on wait set: " +
        std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }
}

/*
 *
 */
JNIEXPORT jobject
JNICALL Java_org_ros2_rcljava_RCLJava_nativeTake
  (JNIEnv *env, jclass, jlong subscription_handle, jclass jmessage_class) {

  rcl_subscription_t * subscription =
      handle2Instance<rcl_subscription_t>(subscription_handle);

  void * taken_msg = jclass2Message(env, jmessage_class);

  rcl_ret_t ret = rcl_take(subscription, taken_msg, nullptr);
  if (ret != RCL_RET_OK && ret != RCL_RET_SUBSCRIPTION_TAKE_FAILED) {
    std::string message("Failed to take from a subscription: " +
        std::string(rcl_get_error_string_safe()));
    throwException(env, message);
  }

  if (ret != RCL_RET_SUBSCRIPTION_TAKE_FAILED) {
    jobject jtaken_msg = jclass2JMessage(env, jmessage_class, taken_msg);

    return jtaken_msg;
  }

  return nullptr;
}

/*
 *
 */
JNIEXPORT void
JNICALL Java_org_ros2_rcljava_RCLJava_nativeWaitSetFini
  (JNIEnv *env, jclass, jlong wait_set_handle) {

  rcl_wait_set_t * wait_set = handle2Instance<rcl_wait_set_t>(wait_set_handle);

  rcl_ret_t ret = rcl_wait_set_fini(wait_set);
  if (ret != RCL_RET_OK) {
      std::string message("Failed to release wait set: " +
          std::string(rcl_get_error_string_safe()));
      throwException(env, message);
    }
}

JNIEXPORT jobject
JNICALL Java_org_ros2_rcljava_RCLJava_nativeGetNodeNames
  (JNIEnv *env, jclass) {

  return NULL;
}
