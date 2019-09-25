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
# TODO(jacobperron): Maybe we can remove this
include_directives = set()
}@
#include <jni.h>

#include <cassert>
#include <cstdint>
#include <string>

// Ensure that a jlong is big enough to store raw pointers
static_assert(sizeof(jlong) >= sizeof(std::intptr_t), "jlong must be able to store pointers");

#include "rosidl_generator_c/message_type_support_struct.h"

@# TODO(jacobperron): Make these includes conditional
#include "rosidl_generator_c/string.h"
#include "rosidl_generator_c/string_functions.h"

#include "rosidl_generator_c/primitives_sequence.h"
#include "rosidl_generator_c/primitives_sequence_functions.h"

#include "rcljava_common/exceptions.h"
#include "rcljava_common/signatures.h"

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
    # interface_path=interface_path,
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
    # interface_path=interface_path,
    service=service,
    include_directives=include_directives)
}@
@[end for]@
@
@#######################################################################
@# Handle actions
@#######################################################################
@{
# TODO
}@
