@# Generation triggered from rosidl_generator_java/resource/idl.java.em
// generated from rosidl_generator_java/resource/srv.java.em
// with input from @(package_name):@(interface_path)
// generated code does not contain a copyright notice

package @(package_name + '.' + interface_path.parts[0]);
@{
import os
from rosidl_cmake import expand_template

namespaces = service.namespaced_type.namespaces
type_name = service.namespaced_type.name
request_type_name = service.request_message.structure.namespaced_type.name
response_type_name = service.response_message.structure.namespaced_type.name

data = {
    'package_name': package_name,
    'interface_path': interface_path,
    'output_dir': output_dir,
    'template_basepath': template_basepath,
    'marker_interfaces': [],
}
data.update({'message': service.request_message})
output_file = os.path.join(output_dir, *namespaces[1:], request_type_name + '.java')
expand_template(
    'msg.java.em',
    data,
    output_file,
    template_basepath=template_basepath)

data.update({'message': service.response_message})
output_file = os.path.join(output_dir, *namespaces[1:], response_type_name + '.java')
expand_template(
    'msg.java.em',
    data,
    output_file,
    template_basepath=template_basepath)

service_imports = [
    'org.ros2.rcljava.common.JNIUtils',
    'org.ros2.rcljava.interfaces.ServiceDefinition',
    'org.slf4j.Logger',
    'org.slf4j.LoggerFactory',
]
}@

@[for service_import in service_imports]@
import @(service_import);
@[end for]@

public class @(type_name) implements ServiceDefinition {

  private static final Logger logger = LoggerFactory.getLogger(@(type_name).class);

  static {
    try {
      JNIUtils.loadTypesupport(@(type_name).class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  public static native long getServiceTypeSupport();

  public static final Class<@(type_name)_Request> RequestType = @(type_name)_Request.class;

  public static final Class<@(type_name)_Response> ResponseType = @(type_name)_Response.class;
}
