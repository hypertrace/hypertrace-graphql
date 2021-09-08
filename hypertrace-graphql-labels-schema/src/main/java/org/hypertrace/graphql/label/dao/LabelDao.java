package org.hypertrace.graphql.label.dao;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.core.graphql.common.request.ContextualRequest;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.label.request.LabelCreateRequest;
import org.hypertrace.graphql.label.request.LabelUpdateRequest;
import org.hypertrace.graphql.label.schema.Label;
import org.hypertrace.graphql.label.schema.LabelResultSet;
import org.hypertrace.label.config.service.v1.GetLabelsRequest;
import org.hypertrace.label.config.service.v1.GetLabelsResponse;

public interface LabelDao {
  Single<LabelResultSet> getLabels(ContextualRequest request);

  Single<Label> createLabel(LabelCreateRequest request);

  Single<Label> updateLabel(LabelUpdateRequest request);

  Single<GetLabelsResponse> getLabelsResponse(
      GraphQlRequestContext context, GetLabelsRequest request);
}
