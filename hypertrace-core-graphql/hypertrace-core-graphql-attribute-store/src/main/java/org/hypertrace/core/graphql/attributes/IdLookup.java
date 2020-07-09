package org.hypertrace.core.graphql.attributes;

import com.google.common.collect.ImmutableTable;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
class IdLookup {

  private final ImmutableTable<AttributeModelScope, AttributeModelScope, String> idTable;

  @Inject
  IdLookup(Set<IdMapping> idMappings) {
    this.idTable =
        idMappings.stream()
            .collect(
                ImmutableTable.toImmutableTable(
                    IdMapping::containingScope, IdMapping::foreignScope, IdMapping::idAttribute));
  }

  Optional<String> idKey(AttributeModelScope scope) {
    return this.foreignIdKey(scope, scope);
  }

  Optional<String> foreignIdKey(AttributeModelScope scope, AttributeModelScope foreignScope) {
    return Optional.ofNullable(this.idTable.get(scope, foreignScope));
  }
}
