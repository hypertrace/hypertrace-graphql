package org.hypertrace.graphql.label.joiner;

import static java.util.function.Function.identity;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hypertrace.core.graphql.common.request.ContextualRequestBuilder;
import org.hypertrace.core.graphql.common.utils.CollectorUtils;
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
          .flatMap(labelResultSet -> buildLabelMap(labelResultSet.results()))
          .flatMap(labelMap -> getLabelResultSetMap(joinSources, labelIdGetter, labelMap));
    }

    private Single<Map<String, Label>> buildLabelMap(List<Label> results) {
      return Single.just(
          results.stream().collect(Collectors.toUnmodifiableMap(Label::id, identity())));
    }

    private <T> Single<Map<T, LabelResultSet>> getLabelResultSetMap(
        Collection<T> joinSources, LabelIdGetter<T> labelIdGetter, Map<String, Label> labelMap) {
      return Observable.fromIterable(joinSources)
          .flatMapSingle(source -> buildLabelResultSetMapEntry(source, labelIdGetter, labelMap))
          .collect(CollectorUtils.immutableMapEntryCollector());
    }

    private <T> Single<Map.Entry<T, LabelResultSet>> buildLabelResultSetMapEntry(
        T source, LabelIdGetter<T> labelIdGetter, Map<String, Label> labelMap) {
      return labelIdGetter
          .getLabelIds(source)
          .flatMap(labelIds -> filterLabels(labelIds, labelMap))
          .flatMap(labels -> responseConverter.convert(labels))
          .map(labelResultSet -> Map.entry(source, labelResultSet));
    }

    private Single<List<Label>> filterLabels(List<String> labelIds, Map<String, Label> labelMap) {
      return Single.just(
          labelIds.stream()
              .map(labelId -> getLabelFromMap(labelId, labelMap))
              .filter(Objects::nonNull)
              .collect(Collectors.toUnmodifiableList()));
    }

    private Label getLabelFromMap(String labelId, Map<String, Label> labelMap) {
      Label label = labelMap.get(labelId);
      if (label == null) {
        log.warn("Label config doesn't exist for label id {}", labelId);
      }
      return label;
    }
  }
}
