@# Included from rosidl_generator_java/resource/idl.cpp.em
@{
from rosidl_generator_c import idl_structure_type_to_c_include_prefix

action_includes = [
    'rosidl_generator_c/action_type_support_struct.h',
]
}@
@[for include in action_includes]@
@[  if include in include_directives]@
// already included above
// @
@[  else]@
@{include_directives.add(include)}@
@[  end if]@
#include "@(include)"
@[end for]@

#include "@(idl_structure_type_to_c_include_prefix(action.namespaced_type)).h"

#ifdef __cplusplus
extern "C" {
#endif

@{
action_fqn = action.namespaced_type.namespaced_name()
underscore_separated_type_name = '_'.join(action_fqn)
# JNI name mangling: https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/design.html#resolving_native_method_names
underscore_separated_jni_type_name = action_fqn[0].replace('_', '_1') + '_' + '_'.join(action_fqn[1:])
}@
/*
 * Class:     @(underscore_separated_type_name)
 * Method:    getActionTypeSupport
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_@(underscore_separated_jni_type_name)_getActionTypeSupport(JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif

JNIEXPORT jlong JNICALL Java_@(underscore_separated_jni_type_name)_getActionTypeSupport(JNIEnv *, jclass)
{
  const rosidl_action_type_support_t * ts = ROSIDL_GET_ACTION_TYPE_SUPPORT(
    @(package_name), @(action.namespaced_type.name));
  return reinterpret_cast<jlong>(ts);
}
