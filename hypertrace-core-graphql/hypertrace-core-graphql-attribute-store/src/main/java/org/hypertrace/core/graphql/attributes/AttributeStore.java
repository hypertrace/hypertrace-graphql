package org.hypertrace.core.graphql.attributes;

import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface AttributeStore {
  Single<List<AttributeModel>> getAll(GraphQlRequestContext context);

  Single<AttributeModel> get(GraphQlRequestContext context, AttributeModelScope scope, String key);

  Single<Map<String, AttributeModel>> get(
      GraphQlRequestContext context, AttributeModelScope scope, Collection<String> keys);

  Single<AttributeModel> getIdAttribute(GraphQlRequestContext context, AttributeModelScope scope);

  Single<AttributeModel> getForeignIdAttribute(
      GraphQlRequestContext context, AttributeModelScope scope, AttributeModelScope foreignScope);
}
