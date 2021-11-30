package org.hypertrace.graphql.explorer.schema.argument;

import graphql.annotations.annotationTypes.GraphQLDeprecate;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import java.util.List;
import javax.annotation.Nullable;
import org.hypertrace.core.graphql.common.schema.attributes.arguments.AttributeExpression;

@GraphQLName(GroupByArgument.TYPE_NAME)
public interface GroupByArgument {
  String TYPE_NAME = "GroupByArgument";
  String ARGUMENT_NAME = "groupBy";

  String GROUP_BY_KEYS_KEY = "keys";
  String GROUP_BY_EXPRESSIONS_KEY = "expressions";
  String GROUP_BY_INCLUDE_REST_KEY = "includeRest";
  String GROUP_BY_LIMIT_KEY = "groupLimit";

  @GraphQLField
  @GraphQLDeprecate
  @Deprecated
  @Nullable
  @GraphQLName(GROUP_BY_KEYS_KEY)
  List<String> keys();

  @GraphQLField
  @Nullable
  @GraphQLName(GROUP_BY_EXPRESSIONS_KEY)
  List<AttributeExpression> expressions();

  @GraphQLField
  @GraphQLName(GROUP_BY_INCLUDE_REST_KEY)
  boolean includeRest();

  @GraphQLField
  @GraphQLName(GROUP_BY_LIMIT_KEY)
  int groupLimit();
}
