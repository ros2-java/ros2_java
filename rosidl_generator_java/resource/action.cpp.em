@# Included from rosidl_generator_java/resource/idl.cpp.em
// generated from rosidl_generator_java/resource/action.cpp.em
// with input from @(package_name):@(interface_path)
// generated code does not contain a copyright notice
@{
import os

from rosidl_cmake import expand_template
from rosidl_generator_c import idl_structure_type_to_c_include_prefix

namespaces = action.namespaced_type.namespaces
type_name = action.namespaced_type.name
goal_type_name = action.goal.structure.namespaced_type.name
result_type_name = action.result.structure.namespaced_type.name
feedback_type_name = action.feedback.structure.namespaced_type.name
feedback_message_type_name = action.feedback_message.structure.namespaced_type.name
send_goal_type_name = action.send_goal_service.namespaced_type.name
get_result_type_name = action.get_result_service.namespaced_type.name

data = {
    'package_name': package_name,
    'interface_path': interface_path,
    'output_dir': output_dir,
    'template_basepath': template_basepath,
}

# Generate Goal message type
data.update({'message': action.goal})
output_file = os.path.join(
    output_dir, *namespaces[1:], '{0}.ep.{1}.cpp'.format(goal_type_name, typesupport_impl))
expand_template(
    'msg.cpp.em',
    data,
    output_file)

# Generate Result message type
data.update({'message': action.result})
output_file = os.path.join(
    output_dir, *namespaces[1:], '{0}.ep.{1}.cpp'.format(result_type_name, typesupport_impl))
expand_template(
    'msg.cpp.em',
    data,
    output_file)

# Generate Feedback message type
data.update({'message': action.feedback})
output_file = os.path.join(
    output_dir, *namespaces[1:], '{0}.ep.{1}.cpp'.format(feedback_type_name, typesupport_impl))
expand_template(
    'msg.cpp.em',
    data,
    output_file)

# Generate FeedbackMessage message type
data.update({'message': action.feedback_message})
output_file = os.path.join(
    output_dir,
    *namespaces[1:],
    '{0}.ep.{1}.cpp'.format(feedback_message_type_name, typesupport_impl))
expand_template(
    'msg.cpp.em',
    data,
    output_file)

data = {
    'package_name': package_name,
    'interface_path': interface_path,
    'output_dir': output_dir,
    'template_basepath': template_basepath,
    'typesupport_impl': typesupport_impl,
}

# Generate SendGoal service type
data.update({'service': action.send_goal_service})
output_file = os.path.join(
    output_dir, *namespaces[1:], '{0}.ep.{1}.cpp'.format(send_goal_type_name, typesupport_impl))
expand_template(
    'srv.cpp.em',
    data,
    output_file)

# Generate GetResult service type
data.update({'service': action.get_result_service})
output_file = os.path.join(
    output_dir, *namespaces[1:], '{0}.ep.{1}.cpp'.format(get_result_type_name, typesupport_impl))
expand_template(
    'srv.cpp.em',
    data,
    output_file)
}@

#include <jni.h>

#include <cstdint>

#include "rosidl_runtime_c/action_type_support_struct.h"

#include "@(idl_structure_type_to_c_include_prefix(action.namespaced_type)).h"

// Ensure that a jlong is big enough to store raw pointers
static_assert(sizeof(jlong) >= sizeof(std::intptr_t), "jlong must be able to store pointers");

#ifdef __cplusplus
extern "C" {
#endif

@{
from rosidl_generator_java import get_jni_mangled_name

action_fqn = action.namespaced_type.namespaced_name()
underscore_separated_type_name = '_'.join(action_fqn)
underscore_separated_jni_type_name = get_jni_mangled_name(action_fqn)
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
