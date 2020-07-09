package org.hypertrace.core.graphql.attributes;

import java.util.Objects;
import javax.annotation.Nonnull;

public interface IdMapping {

  AttributeModelScope containingScope();

  String idAttribute();

  default AttributeModelScope foreignScope() {
    return null;
  }

  static IdMapping forId(@Nonnull AttributeModelScope scope, @Nonnull String idAttribute) {
    Objects.requireNonNull(scope);
    Objects.requireNonNull(idAttribute);
    return new DefaultIdMapping(scope, idAttribute, null);
  }

  static IdMapping forForeignId(
      @Nonnull AttributeModelScope scope,
      @Nonnull AttributeModelScope foreignScope,
      @Nonnull String idAttribute) {
    Objects.requireNonNull(scope);
    Objects.requireNonNull(foreignScope);
    Objects.requireNonNull(idAttribute);
    return new DefaultIdMapping(scope, idAttribute, foreignScope);
  }
}
