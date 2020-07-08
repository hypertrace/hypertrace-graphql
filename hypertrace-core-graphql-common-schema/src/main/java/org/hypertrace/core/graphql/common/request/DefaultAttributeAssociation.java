package org.hypertrace.core.graphql.common.request;

import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.attributes.AttributeModel;

@Value
@Accessors(fluent = true)
class DefaultAttributeAssociation<T> implements AttributeAssociation<T> {
  AttributeModel attribute;
  T value;
}
