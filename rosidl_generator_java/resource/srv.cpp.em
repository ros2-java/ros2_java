@# Included from rosidl_generator_java/resource/idl.cpp.em
@{
from rosidl_generator_c import idl_structure_type_to_c_include_prefix

service_includes = [
    'rosidl_generator_c/service_type_support_struct.h',
]
}@
@[for include in service_includes]@
@[  if include in include_directives]@
// already included above
// @
@[  else]@
@{include_directives.add(include)}@
@[  end if]@
#include "@(include)"
@[end for]@

#include "@(idl_structure_type_to_c_include_prefix(service.namespaced_type)).h"

#ifdef __cplusplus
extern "C" {
#endif

@{
from rosidl_generator_java import get_jni_mangled_name

service_fqn = service.namespaced_type.namespaced_name()
underscore_separated_type_name = '_'.join(service_fqn)
underscore_separated_jni_type_name = get_jni_mangled_name(service_fqn)
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
