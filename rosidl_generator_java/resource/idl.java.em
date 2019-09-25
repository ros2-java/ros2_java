@#######################################################################
@# EmPy template for generating <idl>.java files
@#
@# Context:
@#  - package_name (string)
@#  - interface_path (Path relative to the directory named after the package)
@#  - content (IdlContent, list of elements, e.g. Messages or Services)
@# Additional context:
@#  - output_dir (Path)
@#  - template_basepath (Path)
@#######################################################################
@
@#######################################################################
@# Handle messages
@#######################################################################
@{
import os

from rosidl_cmake import expand_template
from rosidl_parser.definition import Message
from rosidl_parser.definition import Service

data = {
    'package_name': package_name,
    'interface_path': interface_path,
    'output_dir': output_dir,
    'template_basepath': template_basepath,
}

for message in content.get_elements_of_type(Message):
    data.update({'message': message})
    type_name = message.structure.namespaced_type.name
    namespaces = message.structure.namespaced_type.namespaces
    output_file = os.path.join(output_dir, *namespaces[1:], type_name + '.java')
    expand_template(
        'msg.java.em',
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
}

for service in content.get_elements_of_type(Service):
    data.update({'service': service})
    type_name = service.namespaced_type.name
    namespaces = service.namespaced_type.namespaces
    output_file = os.path.join(output_dir, *namespaces[1:], type_name + '.java')
    expand_template(
        'srv.java.em',
        data,
        output_file,
        template_basepath=template_basepath)
}@
@
@#######################################################################
@# Handle actions
@#######################################################################
@{
# TODO
}@
