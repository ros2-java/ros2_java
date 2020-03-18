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
@#  - output_dir (Path)
@#  - template_basepath (Path)
@#  - typesupport_impl (string, the typesupport identifier of the generated code)
@#######################################################################
@{
import os

from rosidl_cmake import expand_template
from rosidl_parser.definition import Action
from rosidl_parser.definition import Message
from rosidl_parser.definition import Service

}@
@
@#######################################################################
@# Handle messages
@#######################################################################
@{
data = {
    'package_name': package_name,
    'output_dir': output_dir,
    'template_basepath': template_basepath,
}

for message in content.get_elements_of_type(Message):
    data.update({'message': message})
    type_name = message.structure.namespaced_type.name
    namespaces = message.structure.namespaced_type.namespaces
    output_file = os.path.join(
        output_dir, *namespaces[1:], '{0}.ep.{1}.cpp'.format(type_name, typesupport_impl))
    expand_template(
        'msg.cpp.em',
        data,
        output_file,
        template_basepath=template_basepath)
}@
@
@#######################################################################
@# Handle services
@#######################################################################
@{
data = {
    'package_name': package_name,
    'interface_path': interface_path,
    'output_dir': output_dir,
    'template_basepath': template_basepath,
    'typesupport_impl': typesupport_impl,
}

for service in content.get_elements_of_type(Service):
    data.update({'service': service})
    type_name = service.namespaced_type.name
    namespaces = service.namespaced_type.namespaces
    output_file = os.path.join(
        output_dir, *namespaces[1:], '{0}.ep.{1}.cpp'.format(type_name, typesupport_impl))
    expand_template(
        'srv.cpp.em',
        data,
        output_file,
        template_basepath=template_basepath)

}@
@
@#######################################################################
@# Handle actions
@#######################################################################
@{
data = {
    'package_name': package_name,
    'interface_path': interface_path,
    'output_dir': output_dir,
    'template_basepath': template_basepath,
    'typesupport_impl': typesupport_impl,
}

for action in content.get_elements_of_type(Action):
    data.update({'action': action})
    type_name = action.namespaced_type.name
    namespaces = action.namespaced_type.namespaces
    output_file = os.path.join(
        output_dir, *namespaces[1:], '{0}.ep.{1}.cpp'.format(type_name, typesupport_impl))
    expand_template(
        'action.cpp.em',
        data,
        output_file,
        template_basepath=template_basepath)
}@
