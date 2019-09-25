@# Included from rosidl_generator_java/resource/idl.cpp.em
@{
from rosidl_generator_c import idl_structure_type_to_c_include_prefix
}@
#include "rosidl_generator_c/service_type_support_struct.h"
#include "@(idl_structure_type_to_c_include_prefix(service.namespaced_type)).h"

#ifdef __cplusplus
extern "C" {
#endif

@{
service_fqn = service.namespaced_type.namespaced_name()
underscore_separated_type_name = '_'.join(service_fqn)
# JNI name mangling: https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/design.html#resolving_native_method_names
underscore_separated_jni_type_name = service_fqn[0].replace('_', '_1') + '_' + '_'.join(service_fqn[1:])
}@
/*
 * Class:     @(underscore_separated_type_name)
 * Method:    getServiceTypeSupport
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_@(underscore_separated_jni_type_name)_getServiceTypeSupport(JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif

JNIEXPORT jlong JNICALL Java_@(underscore_separated_jni_type_name)_getServiceTypeSupport(JNIEnv *, jclass)
{
  const rosidl_service_type_support_t * ts = ROSIDL_GET_SRV_TYPE_SUPPORT(
    @(','.join(service_fqn)));
  return reinterpret_cast<jlong>(ts);
}
