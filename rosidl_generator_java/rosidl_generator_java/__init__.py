# Copyright 2016-2017 Esteve Fernandez <esteve@apache.org>
# Copyright 2019 Open Source Robotics Foundation, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from ast import literal_eval
from collections import defaultdict
import pathlib

from rosidl_cmake import generate_files
from rosidl_cmake import read_generator_arguments
from rosidl_parser.definition import AbstractGenericString
from rosidl_parser.definition import AbstractNestedType
from rosidl_parser.definition import BASIC_TYPES
from rosidl_parser.definition import BasicType
from rosidl_parser.definition import NamespacedType


# Taken from http://stackoverflow.com/a/6425628
def convert_lower_case_underscore_to_camel_case(word):
    return ''.join(x.capitalize() or '_' for x in word.split('_'))


def generate_java(generator_arguments_file, typesupport_impls):
    args = read_generator_arguments(generator_arguments_file)
    additional_context = {
        'output_dir': pathlib.Path(args['output_dir']),
        'template_basepath': pathlib.Path(args['template_dir']),
    }
    mapping = {
        'idl.java.em': '_%s.java',
    }
    generate_files(
        generator_arguments_file, mapping, additional_context=additional_context, keep_case=True)

    for impl in typesupport_impls:
        mapping = {
          'idl.cpp.em': '%s.ep.{0}.cpp'.format(impl),
        }
        generate_files(
            generator_arguments_file,
            mapping,
            additional_context=additional_context,
            keep_case=True)
    return 0


def escape_string(s):
    s = s.replace('\\', '\\\\')
    s = s.replace("'", "\\'")
    return s


def escape_wstring(s):
    return escape_string(s)


def value_to_java(type_, value):
    assert not isinstance(type_, NamespacedType), \
        "Could not convert non-basic type '{}' to Java".format(type_)
    assert value is not None, "Value for for type '{}' must not be None".format(type_)

    if not isinstance(type_, AbstractNestedType):
        return primitive_value_to_java(type_, value)

    java_values = []
    for single_value in literal_eval(value):
        java_value = primitive_value_to_java(type_.value_type, single_value)
        java_values.append(java_value)
    return '{%s}' % ', '.join(java_values)


def primitive_value_to_java(type_, value):
    assert isinstance(type_, (BasicType, AbstractGenericString)), \
        "Could not convert non-basic type '{}' to Java".format(type_)
    assert value is not None, "Value for for type '{}' must not be None".format(type_)

    if isinstance(type_, AbstractGenericString):
        return '"%s"' % escape_string(value)

    if type_.typename == 'boolean':
        return 'true' if value else 'false'

    if type_.typename == 'float':
        return '%sf' % value

    if type_.typename in BASIC_TYPES:
        return str(value)

    assert False, "unknown primitive type '%s'" % type_.typename


# Map IDL types to Java primitive types
# Maps to a tuple: (primitive, class type)
IDL_TYPE_TO_JAVA_PRIMITIVE = {
    'boolean': ('boolean', 'java.lang.Boolean'),
    'char': ('char', 'java.lang.Char'),
    'octet': ('byte', 'java.lang.Byte'),
    'float': ('float', 'java.lang.Float'),
    'double': ('double', 'java.lang.Double'),
    'long double': ('double', 'java.lang.Double'),
    'uint8': ('byte', 'java.lang.Byte'),
    'int8': ('byte', 'java.lang.Byte'),
    'uint16': ('short', 'java.lang.Short'),
    'int16': ('short', 'java.lang.Short'),
    'uint32': ('int', 'java.lang.Integer'),
    'int32': ('int', 'java.lang.Integer'),
    'uint64': ('long', 'java.lang.Long'),
    'int64': ('long', 'java.lang.Long'),
}


def get_java_type(type_, use_primitives=True):
    if isinstance(type_, AbstractNestedType):
        type_ = type_.value_type
    if isinstance(type_, NamespacedType):
        return '.'.join(type_.namespaced_name())
    if isinstance(type_, BasicType):
        return IDL_TYPE_TO_JAVA_PRIMITIVE[type_.typename][0 if use_primitives else 1]
    if isinstance(type_, AbstractGenericString):
        return 'java.lang.String'

    assert False, "unknown type '%s'" % type_


def get_normalized_type(type_):
    return get_java_type(type_, use_primitives=False).replace('.', '__')


def get_jni_type(type_):
    return get_java_type(type_, use_primitives=False).replace('.', '/')


# JNI performance tips taken from http://planet.jboss.org/post/jni_performance_the_saga_continues
constructor_signatures = defaultdict(lambda: '()V')
constructor_signatures['java/lang/Boolean'] = '(Z)V'
constructor_signatures['java/lang/Byte'] = '(B)V'
constructor_signatures['java/lang/Character'] = '(C)V'
constructor_signatures['java/lang/Double'] = '(D)V'
constructor_signatures['java/lang/Float'] = '(F)V'
constructor_signatures['java/lang/Integer'] = '(I)V'
constructor_signatures['java/lang/Long'] = '(J)V'
constructor_signatures['java/lang/Short'] = '(S)V'
constructor_signatures['java/util/List'] = None

value_methods = {}
value_methods['java/lang/Boolean'] = ('booleanValue', '()Z')
value_methods['java/lang/Byte'] = ('byteValue', '()B')
value_methods['java/lang/Character'] = ('charValue', '()C')
value_methods['java/lang/Double'] = ('doubleValue', '()D')
value_methods['java/lang/Float'] = ('floatValue', '()F')
value_methods['java/lang/Integer'] = ('intValue', '()I')
value_methods['java/lang/Long'] = ('longValue', '()J')
value_methods['java/lang/Short'] = ('shortValue', '()S')

jni_signatures = {}
jni_signatures['java/lang/Boolean'] = 'Z'
jni_signatures['java/lang/Byte'] = 'B'
jni_signatures['java/lang/Character'] = 'C'
jni_signatures['java/lang/Double'] = 'D'
jni_signatures['java/lang/Float'] = 'F'
jni_signatures['java/lang/Integer'] = 'I'
jni_signatures['java/lang/Long'] = 'J'
jni_signatures['java/lang/Short'] = 'S'


def get_jni_signature(type_):
    global jni_signatures
    return jni_signatures.get(get_jni_type(type_))
