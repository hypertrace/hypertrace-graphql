package org.hypertrace.graphql.label.joiner;

import static java.util.function.Function.identity;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.AllArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.hypertrace.core.graphql.context.GraphQlRequestContext;
import org.hypertrace.graphql.label.dao.LabelDao;
import org.hypertrace.graphql.label.schema.LabelResultSet;
import org.hypertrace.label.config.service.v1.GetLabelsRequest;
import org.hypertrace.label.config.service.v1.GetLabelsResponse;
import org.hypertrace.label.config.service.v1.Label;

@Slf4j
class DefaultLabelJoinerBuilder implements LabelJoinerBuilder {

  private final LabelDao labelDao;

  @Inject
  DefaultLabelJoinerBuilder(LabelDao labelDao) {
    this.labelDao = labelDao;
  }

  @Override
  public Single<LabelJoiner> build(GraphQlRequestContext context) {
    return Single.just(new DefaultLabelJoiner(context));
  }

  @AllArgsConstructor
  private class DefaultLabelJoiner implements LabelJoiner {
    private final GraphQlRequestContext context;

    @Override
    public <T> Single<Map<String, LabelResultSet>> joinLabels(
        Collection<T> joinSources, IdGetter<T> idGetter, LabelsGetter<T> labelsGetter) {
      return labelDao
          .getLabelsResponse(context, GetLabelsRequest.getDefaultInstance())
          .map(this::getLabelsMap)
          .flatMap(labelsMap -> getResultSetMap(labelsMap, joinSources, idGetter, labelsGetter));
    }

    private Map<String, Label> getLabelsMap(GetLabelsResponse labelsResponse) {
      return labelsResponse.getLabelsList().stream()
          .collect(Collectors.toUnmodifiableMap(Label::getId, identity()));
    }

    private <T> Single<Map<String, LabelResultSet>> getResultSetMap(
        Map<String, Label> labelsMap,
        Collection<T> joinSources,
        IdGetter<T> idGetter,
        LabelsGetter<T> labelsGetter) {
      return Observable.fromIterable(joinSources)
          .toMap(
              source -> idGetter.getId(source),
              source -> buildLabelResultSet(labelsGetter.getLabels(source), labelsMap));
    }

    private LabelResultSet buildLabelResultSet(
        List<String> labelIds, Map<String, Label> labelsMap) {
      if (labelIds.isEmpty() || labelsMap.isEmpty()) {
        return ConvertedLabelResultSet.EMPTY_LABEL_RESULT_SET;
      }
      List<org.hypertrace.graphql.label.schema.Label> convertedLabels =
          labelsMap.entrySet().stream()
              .filter(entry -> labelIds.contains(entry.getKey()))
              .map(entry -> new ConvertedLabel(entry.getKey(), entry.getValue().getKey()))
              .collect(Collectors.toUnmodifiableList());
      return new ConvertedLabelResultSet(
          convertedLabels, convertedLabels.size(), convertedLabels.size());
    }
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedLabel implements org.hypertrace.graphql.label.schema.Label {
    String id;
    String key;
  }

  @lombok.Value
  @Accessors(fluent = true)
  private static class ConvertedLabelResultSet implements LabelResultSet {
    static final ConvertedLabelResultSet EMPTY_LABEL_RESULT_SET =
        new ConvertedLabelResultSet(List.of(), 0, 0);
    List<org.hypertrace.graphql.label.schema.Label> results;
    long count;
    long total;
  }
}
