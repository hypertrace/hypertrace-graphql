package org.hypertrace.core.graphql.attributes;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import org.hypertrace.core.attribute.service.v1.AttributeMetadata;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface AttributeStore {
  Single<List<AttributeModel>> getAllExternal(GraphQlRequestContext context);

  Single<AttributeModel> get(GraphQlRequestContext context, String scope, String key);

  Single<AttributeModel> getIdAttribute(GraphQlRequestContext context, String scope);

  Single<AttributeModel> getAttributeById(GraphQlRequestContext context, String attributeId);

  Single<AttributeModel> getForeignIdAttribute(
      GraphQlRequestContext context, String scope, String foreignScope);

  Completable create(final GraphQlRequestContext context, final List<AttributeMetadata> attributes);

  Completable delete(final GraphQlRequestContext context, final AttributeIdentifier identifier);

  Single<AttributeModel> update(
      final GraphQlRequestContext context,
      final AttributeIdentifier identifier,
      final AttributeUpdate update);
}
