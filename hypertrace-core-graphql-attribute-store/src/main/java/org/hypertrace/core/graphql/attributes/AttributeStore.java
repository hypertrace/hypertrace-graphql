package org.hypertrace.core.graphql.attributes;

import io.reactivex.rxjava3.core.Single;
import java.util.List;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface AttributeStore {
  Single<List<AttributeModel>> getAllExternal(GraphQlRequestContext context);

  Single<AttributeModel> get(GraphQlRequestContext context, String scope, String key);

  Single<AttributeModel> getIdAttribute(GraphQlRequestContext context, String scope);

  Single<AttributeModel> getForeignIdAttribute(
      GraphQlRequestContext context, String scope, String foreignScope);
}
