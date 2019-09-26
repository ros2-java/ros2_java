// generated from rosidl_generator_java/resource/idl.cpp.em
// with input from @(package_name):@(interface_path)
// generated code does not contain a copyright notice
@
@#######################################################################
@# EmPy template for generating <idl>.cpp files
@#
@# Context:
@#  - package_name (string)
@#  - interface_path (Path relative to the directory named after the package)
@#  - content (IdlContent, list of elements, e.g. Messages or Services)
@#######################################################################
@{
include_directives = set()

jni_includes = [
    'jni.h',
]
include_directives.update(jni_includes)
std_includes = [
    'cassert',
    'cstdint',
    'string',
]
include_directives.update(std_includes)
rosidl_includes = [
    'rosidl_generator_c/message_type_support_struct.h',
]
include_directives.update(rosidl_includes)
rcljava_includes = [
    'rcljava_common/exceptions.h',
    'rcljava_common/signatures.h',
]
include_directives.update(rcljava_includes)
}@
@[for include in jni_includes]@
#include <@(include)>
@[end for]@

@[for include in std_includes]@
#include <@(include)>
@[end for]@

@[for include in rosidl_includes]@
#include "@(include)"
@[end for]@

@[for include in rcljava_includes]@
#include "@(include)"
@[end for]@

// Ensure that a jlong is big enough to store raw pointers
static_assert(sizeof(jlong) >= sizeof(std::intptr_t), "jlong must be able to store pointers");

using rcljava_common::exceptions::rcljava_throw_exception;

@{
jni_package_name = package_name.replace('_', '_1')
}@
@
@#######################################################################
@# Handle messages
@#######################################################################
@{
from rosidl_parser.definition import Message
}@
@[for message in content.get_elements_of_type(Message)]@
@{
TEMPLATE(
    'msg.cpp.em',
    package_name=package_name,
    jni_package_name=jni_package_name,
    message=message,
    include_directives=include_directives)
}@
@[end for]@
@
@#######################################################################
@# Handle services
@#######################################################################
@{
from rosidl_parser.definition import Service
}@
@[for service in content.get_elements_of_type(Service)]@
@{
TEMPLATE(
    'srv.cpp.em',
    package_name=package_name,
    jni_package_name=jni_package_name,
    service=service,
    include_directives=include_directives)
}@
@[end for]@
@
@#######################################################################
@# Handle actions
@#######################################################################
@{
from rosidl_parser.definition import Action
}@
@[for action in content.get_elements_of_type(Action)]@
@{
TEMPLATE(
    'action.cpp.em',
    package_name=package_name,
    jni_package_name=jni_package_name,
    action=action,
    include_directives=include_directives)
}@
@[end for]@
