package org.hypertrace.graphql.entity.request;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.Optional;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;

public interface EntityLabelRequestBuilder {

  Single<Optional<EntityLabelRequest>> buildLabelRequestIfPresentInResultSet(
      GraphQlRequestContext context, String scope, DataFetchingFieldSelectionSet selectionSet);

  Single<Optional<EntityLabelRequest>> buildLabelRequestIfPresentInAnyEntity(
      GraphQlRequestContext context, String scope, Collection<SelectedField> entityField);
}
