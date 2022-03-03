package org.hypertrace.graphql.spanprocessing.dao;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import javax.inject.Inject;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.hypertrace.graphql.spanprocessing.schema.mutation.DeleteSpanProcessingRuleResponse;
import org.hypertrace.graphql.spanprocessing.schema.query.ApiNamingRuleResultSet;
import org.hypertrace.graphql.spanprocessing.schema.query.ExcludeSpanRuleResultSet;
import org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRule;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRule;
import org.hypertrace.span.processing.config.service.v1.CreateApiNamingRuleResponse;
import org.hypertrace.span.processing.config.service.v1.CreateExcludeSpanRuleResponse;
import org.hypertrace.span.processing.config.service.v1.DeleteApiNamingRuleResponse;
import org.hypertrace.span.processing.config.service.v1.DeleteExcludeSpanRuleResponse;
import org.hypertrace.span.processing.config.service.v1.GetAllApiNamingRulesResponse;
import org.hypertrace.span.processing.config.service.v1.GetAllExcludeSpanRulesResponse;
import org.hypertrace.span.processing.config.service.v1.UpdateApiNamingRuleResponse;
import org.hypertrace.span.processing.config.service.v1.UpdateExcludeSpanRuleResponse;

@Slf4j
public class ConfigServiceSpanProcessingResponseConverter {

  private final ConfigServiceSpanProcessingRuleConverter ruleConverter;

  @Inject
  ConfigServiceSpanProcessingResponseConverter(
      ConfigServiceSpanProcessingRuleConverter ruleConverter) {
    this.ruleConverter = ruleConverter;
  }

  Single<ExcludeSpanRuleResultSet> convert(GetAllExcludeSpanRulesResponse response) {
    return this.convertExcludeSpanRuleResultSet(response.getRuleDetailsList());
  }

  Single<ApiNamingRuleResultSet> convert(GetAllApiNamingRulesResponse response) {
    return this.convertApiNamingRuleResultSet(response.getRuleDetailsList());
  }

  private Maybe<ExcludeSpanRule> convertOrDrop(
      org.hypertrace.span.processing.config.service.v1.ExcludeSpanRuleDetails ruleDetails) {
    return this.ruleConverter
        .convert(ruleDetails)
        .doOnError(error -> log.error("Error converting ExcludeSpanRule", error))
        .onErrorComplete();
  }

  private Maybe<ApiNamingRule> convertOrDrop(
      org.hypertrace.span.processing.config.service.v1.ApiNamingRuleDetails ruleDetails) {
    return this.ruleConverter
        .convert(ruleDetails)
        .doOnError(error -> log.error("Error converting ExcludeSpanRule", error))
        .onErrorComplete();
  }

  private Single<ExcludeSpanRuleResultSet> convertExcludeSpanRuleResultSet(
      List<org.hypertrace.span.processing.config.service.v1.ExcludeSpanRuleDetails> ruleDetails) {
    return Observable.fromIterable(ruleDetails)
        .concatMapMaybe(this::convertOrDrop)
        .toList()
        .map(ConvertedExcludeSpanRuleResultSet::new);
  }

  private Single<ApiNamingRuleResultSet> convertApiNamingRuleResultSet(
      List<org.hypertrace.span.processing.config.service.v1.ApiNamingRuleDetails> ruleDetails) {
    return Observable.fromIterable(ruleDetails)
        .concatMapMaybe(this::convertOrDrop)
        .toList()
        .map(ConvertedApiNamingRuleResultSet::new);
  }

  Single<ExcludeSpanRule> convert(CreateExcludeSpanRuleResponse response) {
    return this.ruleConverter.convert(response.getRuleDetails());
  }

  Single<ExcludeSpanRule> convert(UpdateExcludeSpanRuleResponse response) {
    return this.ruleConverter.convert(response.getRuleDetails());
  }

  Single<DeleteSpanProcessingRuleResponse> convert(DeleteExcludeSpanRuleResponse response) {
    return Single.just(new DefaultDeleteSpanProcessingRuleResponse(true));
  }

  Single<ApiNamingRule> convert(CreateApiNamingRuleResponse response) {
    return this.ruleConverter.convert(response.getRuleDetails());
  }

  Single<ApiNamingRule> convert(UpdateApiNamingRuleResponse response) {
    return this.ruleConverter.convert(response.getRuleDetails());
  }

  Single<DeleteSpanProcessingRuleResponse> convert(DeleteApiNamingRuleResponse response) {
    return Single.just(new DefaultDeleteSpanProcessingRuleResponse(true));
  }

  @Value
  @Accessors(fluent = true)
  private static class DefaultDeleteSpanProcessingRuleResponse
      implements DeleteSpanProcessingRuleResponse {
    boolean success;
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedExcludeSpanRuleResultSet implements ExcludeSpanRuleResultSet {
    List<ExcludeSpanRule> results;
    long total;
    long count;

    private ConvertedExcludeSpanRuleResultSet(List<ExcludeSpanRule> results) {
      this.results = results;
      this.count = results.size();
      this.total = results.size();
    }
  }

  @Value
  @Accessors(fluent = true)
  private static class ConvertedApiNamingRuleResultSet implements ApiNamingRuleResultSet {
    List<ApiNamingRule> results;
    long total;
    long count;

    private ConvertedApiNamingRuleResultSet(List<ApiNamingRule> results) {
      this.results = results;
      this.count = results.size();
      this.total = results.size();
    }
  }
}
