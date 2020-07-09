package org.hypertrace.graphql.explorer.schema.argument;

import graphql.annotations.annotationTypes.GraphQLDeprecate;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import java.util.List;
import javax.annotation.Nullable;

@GraphQLName(GroupByArgument.TYPE_NAME)
public interface GroupByArgument {
  String TYPE_NAME = "GroupByArgument";
  String ARGUMENT_NAME = "groupBy";

  @Deprecated String GROUP_BY_KEY_KEY = "key";
  String GROUP_BY_KEYS_KEY = "keys";
  String GROUP_BY_INCLUDE_REST_KEY = "includeRest";

  @GraphQLField
  @GraphQLDeprecate
  @Deprecated
  @GraphQLName(GROUP_BY_KEY_KEY)
  @Nullable
  String key();

  @GraphQLField // TODO require once key is removed
  @GraphQLName(GROUP_BY_KEYS_KEY)
  @Nullable
  List<String> keys();

  @GraphQLField
  @GraphQLName(GROUP_BY_INCLUDE_REST_KEY)
  boolean includeRest();
}
