package org.hypertrace.core.graphql.attributes;

import java.util.Objects;
import javax.annotation.Nonnull;

public interface IdMapping {

  String containingScope();

  String idAttribute();

  default String foreignScope() {
    return null;
  }

  static IdMapping forId(@Nonnull String scope, @Nonnull String idAttribute) {
    Objects.requireNonNull(scope);
    Objects.requireNonNull(idAttribute);
    return new DefaultIdMapping(scope, idAttribute, null);
  }

  static IdMapping forForeignId(
      @Nonnull String scope, @Nonnull String foreignScope, @Nonnull String idAttribute) {
    Objects.requireNonNull(scope);
    Objects.requireNonNull(foreignScope);
    Objects.requireNonNull(idAttribute);
    return new DefaultIdMapping(scope, idAttribute, foreignScope);
  }
}
