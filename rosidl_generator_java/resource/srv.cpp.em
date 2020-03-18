@# Included from rosidl_generator_java/resource/idl.cpp.em
@{
import os
from rosidl_cmake import expand_template
from rosidl_generator_c import idl_structure_type_to_c_include_prefix

namespaces = service.namespaced_type.namespaces
type_name = service.namespaced_type.name
request_type_name = service.request_message.structure.namespaced_type.name
response_type_name = service.response_message.structure.namespaced_type.name

data = {
    'package_name': package_name,
    'output_dir': output_dir,
    'template_basepath': template_basepath,
}

# Generate request message
data.update({'message': service.request_message})
output_file = os.path.join(
    output_dir, *namespaces[1:], '{0}.ep.{1}.cpp'.format(request_type_name, typesupport_impl))
expand_template(
    'msg.cpp.em',
    data,
    output_file)

# Generate response message
data.update({'message': service.response_message})
output_file = os.path.join(
    output_dir, *namespaces[1:], '{0}.ep.{1}.cpp'.format(response_type_name, typesupport_impl))
expand_template(
    'msg.cpp.em',
    data,
    output_file)
}@

#include <jni.h>

#include <cstdint>

#include "rosidl_generator_c/service_type_support_struct.h"

#include "@(idl_structure_type_to_c_include_prefix(service.namespaced_type)).h"

// Ensure that a jlong is big enough to store raw pointers
static_assert(sizeof(jlong) >= sizeof(std::intptr_t), "jlong must be able to store pointers");

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
