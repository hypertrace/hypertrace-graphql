package org.hypertrace.core.graphql.common.request;

import org.hypertrace.core.graphql.attributes.AttributeModel;

public interface AttributeAssociation<T> {

  AttributeModel attribute();

  T value();

  static <T> AttributeAssociation<T> of(AttributeModel attribute, T value) {
    return new DefaultAttributeAssociation<>(attribute, value);
  }
}
