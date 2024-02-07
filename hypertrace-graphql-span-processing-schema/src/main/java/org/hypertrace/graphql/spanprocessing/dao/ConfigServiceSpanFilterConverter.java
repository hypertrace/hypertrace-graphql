package org.hypertrace.graphql.spanprocessing.dao;

import com.google.common.collect.ImmutableBiMap;
import io.reactivex.rxjava3.core.Single;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.Value;
import lombok.experimental.Accessors;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingFilterField;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingLogicalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRelationalFilter;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRelationalOperator;
import org.hypertrace.graphql.spanprocessing.schema.rule.filter.SpanProcessingRuleFilter;
import org.hypertrace.span.processing.config.service.v1.Field;
import org.hypertrace.span.processing.config.service.v1.ListValue;
import org.hypertrace.span.processing.config.service.v1.LogicalOperator;
import org.hypertrace.span.processing.config.service.v1.LogicalSpanFilterExpression;
import org.hypertrace.span.processing.config.service.v1.RelationalOperator;
import org.hypertrace.span.processing.config.service.v1.RelationalSpanFilterExpression;
import org.hypertrace.span.processing.config.service.v1.SpanFilter;
import org.hypertrace.span.processing.config.service.v1.SpanFilterValue;

public class ConfigServiceSpanFilterConverter {

  private static final ImmutableBiMap<
          LogicalOperator, org.hypertrace.core.graphql.common.schema.operator.LogicalOperator>
      LOGICAL_OPERATOR_OPERATOR_BI_MAP =
          ImmutableBiMap.of(
              LogicalOperator.LOGICAL_OPERATOR_AND,
                  org.hypertrace.core.graphql.common.schema.operator.LogicalOperator.AND,
              LogicalOperator.LOGICAL_OPERATOR_OR,
                  org.hypertrace.core.graphql.common.schema.operator.LogicalOperator.OR);

  private static final ImmutableBiMap<
          org.hypertrace.core.graphql.common.schema.operator.LogicalOperator, LogicalOperator>
      OPERATOR_LOGICAL_OPERATOR_IMMUTABLE_BI_MAP = LOGICAL_OPERATOR_OPERATOR_BI_MAP.inverse();

  private static final ImmutableBiMap<RelationalOperator, SpanProcessingRelationalOperator>
      RELATIONAL_OPERATOR_OPERATOR_BI_MAP =
          ImmutableBiMap.<RelationalOperator, SpanProcessingRelationalOperator>builder()
              .put(
                  RelationalOperator.RELATIONAL_OPERATOR_CONTAINS,
                  SpanProcessingRelationalOperator.CONTAINS)
              .put(
                  RelationalOperator.RELATIONAL_OPERATOR_NOT_CONTAINS,
                  SpanProcessingRelationalOperator.NOT_CONTAINS)
              .put(
                  RelationalOperator.RELATIONAL_OPERATOR_EQUALS,
                  SpanProcessingRelationalOperator.EQUALS)
              .put(
                  RelationalOperator.RELATIONAL_OPERATOR_NOT_EQUALS,
                  SpanProcessingRelationalOperator.NOT_EQUALS)
              .put(
                  RelationalOperator.RELATIONAL_OPERATOR_STARTS_WITH,
                  SpanProcessingRelationalOperator.STARTS_WITH)
              .put(
                  RelationalOperator.RELATIONAL_OPERATOR_ENDS_WITH,
                  SpanProcessingRelationalOperator.ENDS_WITH)
              .put(
                  RelationalOperator.RELATIONAL_OPERATOR_REGEX_MATCH,
                  SpanProcessingRelationalOperator.REGEX_MATCH)
              .put(RelationalOperator.RELATIONAL_OPERATOR_IN, SpanProcessingRelationalOperator.IN)
              .build();

  private static final ImmutableBiMap<SpanProcessingRelationalOperator, RelationalOperator>
      OPERATOR_RELATIONAL_OPERATOR_IMMUTABLE_BI_MAP = RELATIONAL_OPERATOR_OPERATOR_BI_MAP.inverse();

  private static final ImmutableBiMap<Field, SpanProcessingFilterField>
      FIELD_FILTER_FIELD_IMMUTABLE_BI_MAP =
          ImmutableBiMap.<Field, SpanProcessingFilterField>builder()
              .put(Field.FIELD_URL, SpanProcessingFilterField.URL)
              .put(Field.FIELD_SERVICE_NAME, SpanProcessingFilterField.SERVICE_NAME)
              .put(Field.FIELD_ENVIRONMENT_NAME, SpanProcessingFilterField.ENVIRONMENT_NAME)
              .build();

  private static final ImmutableBiMap<SpanProcessingFilterField, Field>
      FILTER_FIELD_FIELD_IMMUTABLE_BI_MAP = FIELD_FILTER_FIELD_IMMUTABLE_BI_MAP.inverse();

  public Single<SpanProcessingRuleFilter> convert(SpanFilter filter) {
    return Single.just(Objects.requireNonNull(convertFilter(filter)));
  }

  public SpanFilter convert(SpanProcessingRuleFilter filter) {
    if (filter == null) {
      return null;
    }
    SpanFilter.Builder spanFilterBuilder = SpanFilter.newBuilder();
    if (filter.logicalSpanFilter() != null) {
      spanFilterBuilder =
          spanFilterBuilder.setLogicalSpanFilter(convertLogicalFilter(filter.logicalSpanFilter()));
    } else if (filter.relationalSpanFilter() != null) {
      spanFilterBuilder =
          spanFilterBuilder.setRelationalSpanFilter(
              convertRelationalFilter(filter.relationalSpanFilter()));
    }
    return spanFilterBuilder.build();
  }

  private SpanProcessingRuleFilter convertFilter(SpanFilter filter) {
    if (filter.equals(SpanFilter.getDefaultInstance())) {
      return null;
    }
    return new ConvertedSpanProcessingRuleFilter(
        this.convertLogicalFilter(filter.getLogicalSpanFilter()),
        this.convertRelationalFilter(filter.getRelationalSpanFilter()));
  }

  private SpanProcessingLogicalFilter convertLogicalFilter(
      LogicalSpanFilterExpression logicalSpanFilterExpression) {
    if (logicalSpanFilterExpression.equals(LogicalSpanFilterExpression.getDefaultInstance())) {
      return null;
    }
    return new ConvertedSpanProcessingLogicalFilter(
        LOGICAL_OPERATOR_OPERATOR_BI_MAP.get(logicalSpanFilterExpression.getOperator()),
        logicalSpanFilterExpression.getOperandsList().stream()
            .map(this::convertFilter)
            .collect(Collectors.toUnmodifiableList()));
  }

  private LogicalSpanFilterExpression convertLogicalFilter(
      SpanProcessingLogicalFilter spanProcessingLogicalFilter) {
    return LogicalSpanFilterExpression.newBuilder()
        .setOperator(
            Objects.requireNonNull(
                OPERATOR_LOGICAL_OPERATOR_IMMUTABLE_BI_MAP.get(
                    spanProcessingLogicalFilter.logicalOperator())))
        .addAllOperands(
            spanProcessingLogicalFilter.spanFilters().stream()
                .map(this::convert)
                .collect(Collectors.toUnmodifiableList()))
        .build();
  }

  private SpanProcessingRelationalFilter convertRelationalFilter(
      RelationalSpanFilterExpression relationalSpanFilterExpression) {
    if (relationalSpanFilterExpression.equals(
        RelationalSpanFilterExpression.getDefaultInstance())) {
      return null;
    }
    return new ConvertedSpanProcessingRelationalFilter(
        RELATIONAL_OPERATOR_OPERATOR_BI_MAP.get(relationalSpanFilterExpression.getOperator()),
        relationalSpanFilterExpression.hasSpanAttributeKey()
            ? relationalSpanFilterExpression.getSpanAttributeKey()
            : null,
        relationalSpanFilterExpression.hasField()
            ? FIELD_FILTER_FIELD_IMMUTABLE_BI_MAP.get(relationalSpanFilterExpression.getField())
            : null,
        convertSpanFilterValue(relationalSpanFilterExpression.getRightOperand()));
  }

  private Object convertSpanFilterValue(SpanFilterValue spanFilterValue) {
    switch (spanFilterValue.getValueCase()) {
      case STRING_VALUE:
        return spanFilterValue.getStringValue();
      case LIST_VALUE:
        return convertListSpanFilterValue(spanFilterValue.getListValue());
      default:
        throw new NoSuchElementException("Unsupported right operand type");
    }
  }

  private Object convertListSpanFilterValue(ListValue value) {
    return value.getValuesList().stream()
        .map(this::convertSpanFilterValue)
        .collect(Collectors.toUnmodifiableList());
  }

  private RelationalSpanFilterExpression convertRelationalFilter(
      SpanProcessingRelationalFilter spanProcessingRelationalFilter) {
    RelationalSpanFilterExpression.Builder relationalSpanFilterExpressionBuilder =
        RelationalSpanFilterExpression.newBuilder()
            .setOperator(
                Objects.requireNonNull(
                    OPERATOR_RELATIONAL_OPERATOR_IMMUTABLE_BI_MAP.get(
                        spanProcessingRelationalFilter.relationalOperator())))
            .setRightOperand(convertToSpanFilterValue(spanProcessingRelationalFilter.value()));

    if (spanProcessingRelationalFilter.key() != null) {
      relationalSpanFilterExpressionBuilder =
          relationalSpanFilterExpressionBuilder.setSpanAttributeKey(
              spanProcessingRelationalFilter.key());
    } else {
      relationalSpanFilterExpressionBuilder =
          relationalSpanFilterExpressionBuilder.setField(
              Objects.requireNonNull(
                  FILTER_FIELD_FIELD_IMMUTABLE_BI_MAP.get(spanProcessingRelationalFilter.field())));
    }
    return relationalSpanFilterExpressionBuilder.build();
  }

  private SpanFilterValue convertToSpanFilterValue(Object value) {
    SpanFilterValue.Builder spanFilterValueBuilder = SpanFilterValue.newBuilder();
    if (String.class.equals(value.getClass())) {
      spanFilterValueBuilder = spanFilterValueBuilder.setStringValue(value.toString());
    } else if (ArrayList.class.equals(value.getClass())) {
      spanFilterValueBuilder.setListValue(convertToListSpanFilterValue(value));
    }
    return spanFilterValueBuilder.build();
  }

  private ListValue convertToListSpanFilterValue(Object value) {
    List<Object> objectList = (List<Object>) value;
    return ListValue.newBuilder()
        .addAllValues(
            objectList.stream()
                .map(this::convertToSpanFilterValue)
                .collect(Collectors.toUnmodifiableList()))
        .build();
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedSpanProcessingRelationalFilter
      implements SpanProcessingRelationalFilter {
    SpanProcessingRelationalOperator relationalOperator;
    String key;
    SpanProcessingFilterField field;
    Object value;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedSpanProcessingLogicalFilter implements SpanProcessingLogicalFilter {
    org.hypertrace.core.graphql.common.schema.operator.LogicalOperator logicalOperator;
    List<SpanProcessingRuleFilter> spanFilters;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedSpanProcessingRuleFilter implements SpanProcessingRuleFilter {
    SpanProcessingLogicalFilter logicalSpanFilter;
    SpanProcessingRelationalFilter relationalSpanFilter;
  }
}
