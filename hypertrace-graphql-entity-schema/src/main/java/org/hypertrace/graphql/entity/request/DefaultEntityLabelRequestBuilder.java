package org.hypertrace.graphql.entity.request;

import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.core.graphql.common.request.AttributeRequest;
import org.hypertrace.core.graphql.common.request.AttributeRequestBuilder;
import org.hypertrace.core.graphql.common.schema.results.ResultSet;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.core.graphql.utils.schema.SelectionQuery;
import org.hypertrace.graphql.entity.schema.Entity;

public class DefaultEntityLabelRequestBuilder implements EntityLabelRequestBuilder {

  private static final String LABELS_ATTRIBUTE_KEY = "labels";

  private static final SelectionQuery RESULT_SET_LABEL_FIELD_QUERY =
      SelectionQuery.builder()
          .selectionPath(List.of(ResultSet.RESULT_SET_RESULTS_NAME, Entity.LABELS_KEY))
          .build();

  private static final SelectionQuery LABEL_FIELD_QUERY =
      SelectionQuery.namedChild(Entity.LABELS_KEY);

  private final AttributeRequestBuilder attributeRequestBuilder;
  private final GraphQlSelectionFinder selectionFinder;

  @Inject
  public DefaultEntityLabelRequestBuilder(
      AttributeRequestBuilder attributeRequestBuilder, GraphQlSelectionFinder selectionFinder) {
    this.attributeRequestBuilder = attributeRequestBuilder;
    this.selectionFinder = selectionFinder;
  }

  @Override
  public Single<Optional<EntityLabelRequest>> buildLabelRequestIfPresentInResultSet(
      GraphQlRequestContext context, String scope, DataFetchingFieldSelectionSet selectionSet) {
    if (isLabelFieldRequested(selectionSet, RESULT_SET_LABEL_FIELD_QUERY)) {
      return buildRequest(context, scope);
    }
    return Single.just(Optional.empty());
  }

  @Override
  public Single<Optional<EntityLabelRequest>> buildLabelRequestIfPresentInAnyEntity(
      GraphQlRequestContext context, String scope, Collection<SelectedField> entityFields) {
    if (entityFields.stream()
        .anyMatch(field -> isLabelFieldRequested(field.getSelectionSet(), LABEL_FIELD_QUERY))) {
      return buildRequest(context, scope);
    }
    return Single.just(Optional.empty());
  }

  private boolean isLabelFieldRequested(
      DataFetchingFieldSelectionSet selectionSet, SelectionQuery labelFieldQuery) {
    return this.selectionFinder.findSelections(selectionSet, labelFieldQuery).findAny().isPresent();
  }

  private Single<Optional<EntityLabelRequest>> buildRequest(
      GraphQlRequestContext context, String scope) {
    return this.attributeRequestBuilder
        .buildForKey(context, scope, LABELS_ATTRIBUTE_KEY)
        .map(DefaultLabelRequest::new)
        .map(Optional::of);
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultLabelRequest implements EntityLabelRequest {
    AttributeRequest labelIdArrayAttributeRequest;
  }
}
