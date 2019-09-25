// generated from rosidl_generator_java/resource/idl.java.em
// with input from @(package_name):@(interface_path)
// generated code does not contain a copyright notice

@#######################################################################
@# EmPy template for generating <idl>.java files
@#
@# Context:
@#  - package_name (string)
@#  - interface_path (Path relative to the directory named after the package)
@#  - content (IdlContent, list of elements, e.g. Messages or Services)
@#######################################################################
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
    'msg.java.em',
    message=message)
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
    'srv.java.em',
    service=service)
}@
@[end for]@
@
@#######################################################################
@# Handle actions
@#######################################################################
@{
# TODO
}@
