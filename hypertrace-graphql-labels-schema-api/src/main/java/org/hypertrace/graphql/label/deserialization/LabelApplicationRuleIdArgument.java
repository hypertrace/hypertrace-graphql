package org.hypertrace.graphql.label.deserialization;

import org.hypertrace.core.graphql.deserialization.PrimitiveArgument;

public interface LabelApplicationRuleIdArgument extends PrimitiveArgument<String> {
  String ARGUMENT_NAME = "id";
}
