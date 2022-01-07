package org.hypertrace.core.graphql.common.schema.attributes.arguments;

import com.fasterxml.jackson.annotation.JsonProperty;
import graphql.annotations.annotationTypes.GraphQLConstructor;
import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@GraphQLName(AttributeExpression.TYPE_NAME)
public class AttributeExpression {
  public static final String ARGUMENT_NAME = "expression";
  static final String TYPE_NAME = "AttributeExpression";

  private static final String ATTRIBUTE_KEY = "key";
  private static final String SUBPATH = "subpath";

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ATTRIBUTE_KEY)
  @JsonProperty(ATTRIBUTE_KEY)
  String key;

  @GraphQLField
  @GraphQLName(SUBPATH)
  @JsonProperty(SUBPATH)
  Optional<String> subpath;

  @GraphQLConstructor
  public AttributeExpression(
      @GraphQLName(ATTRIBUTE_KEY) String key, @GraphQLName(SUBPATH) @Nullable String subpath) {
    this.key = key;
    this.subpath = Optional.ofNullable(subpath);
  }

  private AttributeExpression() {
    this.key = null;
    this.subpath = Optional.empty();
  }

  public String asAlias() {
    return subpath()
        .map(subpath -> String.format("%s.%s", this.key(), subpath))
        .orElseGet(this::key);
  }

  public static AttributeExpression forAttributeKey(@Nonnull String key) {
    return new AttributeExpression(key, null);
  }
}
