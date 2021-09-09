package org.hypertrace.graphql.label.joiner;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hypertrace.core.graphql.common.request.ContextualRequestBuilder;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.label.dao.LabelDao;
import org.hypertrace.graphql.label.dao.LabelResponseConverter;
import org.hypertrace.graphql.label.schema.Label;
import org.hypertrace.graphql.label.schema.LabelResultSet;

@Slf4j
class DefaultLabelJoinerBuilder implements LabelJoinerBuilder {

  private final ContextualRequestBuilder requestBuilder;
  private final LabelDao labelDao;
  private final LabelResponseConverter responseConverter;

  @Inject
  DefaultLabelJoinerBuilder(
      ContextualRequestBuilder requestBuilder,
      LabelDao labelDao,
      LabelResponseConverter responseConverter) {
    this.requestBuilder = requestBuilder;
    this.labelDao = labelDao;
    this.responseConverter = responseConverter;
  }

  @Override
  public Single<LabelJoiner> build(GraphQlRequestContext context) {
    return Single.just(new DefaultLabelJoiner(context));
  }

  @AllArgsConstructor
  private class DefaultLabelJoiner implements LabelJoiner {
    private final GraphQlRequestContext context;

    @Override
    public <T> Single<Map<T, LabelResultSet>> joinLabels(
        Collection<T> joinSources, LabelIdGetter<T> labelIdGetter) {
      return labelDao
          .getLabels(requestBuilder.build(context))
          .flatMap(labels -> getResultSetMap(labels, joinSources, labelIdGetter));
    }

    private <T> Single<Map<T, LabelResultSet>> getResultSetMap(
        List<Label> labels, Collection<T> joinSources, LabelIdGetter<T> labelIdGetter) {
      return Observable.fromIterable(joinSources)
          .toMap(
              source -> source,
              source -> responseConverter.convert(labelIdGetter.getLabelIds(source), labels));
    }
  }
}
