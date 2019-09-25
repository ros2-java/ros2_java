@# Included from rosidl_generator_java/resource/idl.java.em
@{
from rosidl_generator_java import convert_lower_case_underscore_to_camel_case
from rosidl_generator_java import get_java_type
from rosidl_generator_java import primitive_value_to_java
from rosidl_generator_java import value_to_java
from rosidl_parser.definition import AbstractGenericString
from rosidl_parser.definition import AbstractNestedType
from rosidl_parser.definition import Array
from rosidl_parser.definition import BasicType
from rosidl_parser.definition import BoundedSequence
from rosidl_parser.definition import NamespacedType

type_name = message.structure.namespaced_type.name
}@
package @('.'.join(message.structure.namespaced_type.namespaced_name()[:-1]));

import org.ros2.rcljava.common.JNIUtils;
import org.ros2.rcljava.interfaces.MessageDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@[for member in message.structure.members]@
@[  if isinstance(member.type, NamespacedType)]@
import @('.'.join(member.type.namespaced_name()));
@[  end if]@
@[end for]@

public final class @(type_name) implements MessageDefinition {

  private static final Logger logger = LoggerFactory.getLogger(@(type_name).class);

  static {
    try {
      JNIUtils.loadTypesupport(@(type_name).class);
    } catch (UnsatisfiedLinkError ule) {
      logger.error("Native code library failed to load.\n" + ule);
      System.exit(1);
    }
  }

  public static native long getDestructor();
  public static native long getFromJavaConverter();
  public static native long getToJavaConverter();
  public static native long getTypeSupport();

  public long getDestructorInstance() {
    return @(type_name).getDestructor();
  }

  public long getFromJavaConverterInstance() {
    return @(type_name).getFromJavaConverter();
  }

  public long getToJavaConverterInstance() {
    return @(type_name).getToJavaConverter();
  }

  public long getTypeSupportInstance() {
    return @(type_name).getTypeSupport();
  }

@[for constant in message.constants]@
    public static final @(get_java_type(constant.type)) @(constant.name) = @(primitive_value_to_java(constant.type, constant.value));
@[end for]@

@[for member in message.structure.members]@

@[  if isinstance(member.type, AbstractNestedType)]@
@[    if member.has_annotation('default')]@
  private java.util.List<@(get_java_type(member.type, use_primitives=False))> @(member.name) = java.util.Arrays.asList(new @(get_java_type(member.type, use_primitives=False))[] @(value_to_java(member.type, member.get_annotation_value('default')['value'])));
@[    else]@
@[      if isinstance(member.type, Array)]@
  private java.util.List<@(get_java_type(member.type, use_primitives=False))> @(member.name);
@[      else]@
  private java.util.List<@(get_java_type(member.type, use_primitives=False))> @(member.name) = new java.util.ArrayList<@(get_java_type(member.type, use_primitives=False))>();
@[      end if]@
@[    end if]@

  public final @(type_name) set@(convert_lower_case_underscore_to_camel_case(member.name))(final java.util.List<@(get_java_type(member.type, use_primitives=False))> @(member.name)) {
@[    if isinstance(member.type, BoundedSequence)]@
    if(@(member.name).size() > @(member.type.maximum_size)) {
        throw new IllegalArgumentException("List too big, maximum size allowed: @(member.type.maximum_size)");
    }
@[    elif isinstance(member.type, Array)]@
    if(@(member.name).size() != @(member.type.size)) {
        throw new IllegalArgumentException("Invalid size for fixed array, must be exactly: @(member.type.size)");
    }
@[    end if]@
    this.@(member.name) = @(member.name);
    return this;
  }

@[    if isinstance(member.type.value_type, (BasicType, AbstractGenericString))]@
  public final @(type_name) set@(convert_lower_case_underscore_to_camel_case(member.name))(final @(get_java_type(member.type, use_primitives=True))[] @(member.name)) {
    java.util.List<@(get_java_type(member.type, use_primitives=False))> @(member.name)_tmp = new java.util.ArrayList<@(get_java_type(member.type, use_primitives=False))>();
    for(@(get_java_type(member.type, use_primitives=True)) @(member.name)_value : @(member.name)) {
      @(member.name)_tmp.add(@(member.name)_value);
    }
    return set@(convert_lower_case_underscore_to_camel_case(member.name))(@(member.name)_tmp);
  }
@[    end if]@

  public final java.util.List<@(get_java_type(member.type, use_primitives=False))> get@(convert_lower_case_underscore_to_camel_case(member.name))() {
    return this.@(member.name);
  }
@[  else]@
@[    if member.has_annotation('default')]@
  private @(get_java_type(member.type)) @(member.name) = @(value_to_java(member.type, member.get_annotation_value('default')['value']));
@[    else]@
@[      if isinstance(member.type, AbstractGenericString)]@
  private @(get_java_type(member.type)) @(member.name) = "";
@[      elif isinstance(member.type, BasicType)]@
  private @(get_java_type(member.type)) @(member.name);
@[      else]@
  private @(get_java_type(member.type)) @(member.name) = new @(get_java_type(member.type))();
@[      end if]@
@[    end if]@

  public @(type_name) set@(convert_lower_case_underscore_to_camel_case(member.name))(final @(get_java_type(member.type)) @(member.name)) {
@[    if isinstance(member.type, AbstractGenericString) and member.type.has_maximum_size()]@
    if(@(member.name).length() > @(member.type.maximum_size)) {
        throw new IllegalArgumentException("String too long, maximum size allowed: @(member.type.maximum_size)");
    }
@[    end if]@

    this.@(member.name) = @(member.name);
    return this;
  }

  public @(get_java_type(member.type)) get@(convert_lower_case_underscore_to_camel_case(member.name))() {
    return this.@(member.name);
  }
@[  end if]@
@[end for]@

  public int hashCode() {
    return new HashCodeBuilder(17, 37)
@[for member in message.structure.members]@
      .append(this.@(member.name))
@[end for]@
      .toHashCode();
  }

 public boolean equals(Object obj) {
   if (obj == null) { return false; }
   if (obj == this) { return true; }
   if (obj.getClass() != getClass()) {
     return false;
   }
   @(type_name) rhs = (@(type_name)) obj;
   return new EqualsBuilder()
@[for member in message.structure.members]@
                .append(this.@(member.name), rhs.@(member.name))
@[end for]@
                .isEquals();
  }
}
