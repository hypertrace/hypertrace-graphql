package org.hypertrace.graphql.spanprocessing.dao;

import java.util.NoSuchElementException;
import javax.inject.Inject;
import org.hypertrace.graphql.spanprocessing.request.mutation.ApiNamingCreateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ApiNamingDeleteRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ApiNamingUpdateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanCreateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanDeleteRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanUpdateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.IncludeSpanCreateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.IncludeSpanDeleteRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.IncludeSpanUpdateRuleRequest;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ApiNamingRuleCreate;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ApiNamingRuleUpdate;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ExcludeSpanRuleCreate;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ExcludeSpanRuleUpdate;
import org.hypertrace.graphql.spanprocessing.schema.mutation.IncludeSpanRuleCreate;
import org.hypertrace.graphql.spanprocessing.schema.mutation.IncludeSpanRuleUpdate;
import org.hypertrace.span.processing.config.service.v1.ApiNamingRuleConfig;
import org.hypertrace.span.processing.config.service.v1.ApiNamingRuleInfo;
import org.hypertrace.span.processing.config.service.v1.CreateApiNamingRuleRequest;
import org.hypertrace.span.processing.config.service.v1.CreateExcludeSpanRuleRequest;
import org.hypertrace.span.processing.config.service.v1.CreateIncludeSpanRuleRequest;
import org.hypertrace.span.processing.config.service.v1.DeleteApiNamingRuleRequest;
import org.hypertrace.span.processing.config.service.v1.DeleteExcludeSpanRuleRequest;
import org.hypertrace.span.processing.config.service.v1.DeleteIncludeSpanRuleRequest;
import org.hypertrace.span.processing.config.service.v1.ExcludeSpanRuleInfo;
import org.hypertrace.span.processing.config.service.v1.IncludeSpanRuleInfo;
import org.hypertrace.span.processing.config.service.v1.SegmentMatchingBasedConfig;
import org.hypertrace.span.processing.config.service.v1.UpdateApiNamingRule;
import org.hypertrace.span.processing.config.service.v1.UpdateApiNamingRuleRequest;
import org.hypertrace.span.processing.config.service.v1.UpdateExcludeSpanRule;
import org.hypertrace.span.processing.config.service.v1.UpdateExcludeSpanRuleRequest;
import org.hypertrace.span.processing.config.service.v1.UpdateIncludeSpanRule;
import org.hypertrace.span.processing.config.service.v1.UpdateIncludeSpanRuleRequest;

public class ConfigServiceSpanProcessingRequestConverter {

  private final ConfigServiceSpanFilterConverter filterConverter;

  @Inject
  ConfigServiceSpanProcessingRequestConverter(ConfigServiceSpanFilterConverter filterConverter) {
    this.filterConverter = filterConverter;
  }

  CreateExcludeSpanRuleRequest convert(ExcludeSpanCreateRuleRequest request) {
    return CreateExcludeSpanRuleRequest.newBuilder()
        .setRuleInfo(convertInput(request.createInput()))
        .build();
  }

  private ExcludeSpanRuleInfo convertInput(ExcludeSpanRuleCreate excludeSpanRuleCreate) {
    return ExcludeSpanRuleInfo.newBuilder()
        .setName(excludeSpanRuleCreate.name())
        .setFilter(this.filterConverter.convert(excludeSpanRuleCreate.spanFilter()))
        .setDisabled(excludeSpanRuleCreate.disabled())
        .build();
  }

  UpdateExcludeSpanRuleRequest convert(ExcludeSpanUpdateRuleRequest request) {
    return UpdateExcludeSpanRuleRequest.newBuilder()
        .setRule(convertInput(request.updateInput()))
        .build();
  }

  private UpdateExcludeSpanRule convertInput(ExcludeSpanRuleUpdate excludeSpanRuleUpdate) {
    return UpdateExcludeSpanRule.newBuilder()
        .setId(excludeSpanRuleUpdate.id())
        .setName(excludeSpanRuleUpdate.name())
        .setFilter(this.filterConverter.convert(excludeSpanRuleUpdate.spanFilter()))
        .setDisabled(excludeSpanRuleUpdate.disabled())
        .build();
  }

  DeleteExcludeSpanRuleRequest convert(ExcludeSpanDeleteRuleRequest request) {
    return DeleteExcludeSpanRuleRequest.newBuilder().setId(request.id()).build();
  }

  CreateIncludeSpanRuleRequest convert(IncludeSpanCreateRuleRequest request) {
    return CreateIncludeSpanRuleRequest.newBuilder()
        .setRuleInfo(convertInput(request.createInput()))
        .build();
  }

  private IncludeSpanRuleInfo convertInput(IncludeSpanRuleCreate includeSpanRuleCreate) {
    return IncludeSpanRuleInfo.newBuilder()
        .setName(includeSpanRuleCreate.name())
        .setFilter(this.filterConverter.convert(includeSpanRuleCreate.spanFilter()))
        .setDisabled(includeSpanRuleCreate.disabled())
        .build();
  }

  UpdateIncludeSpanRuleRequest convert(IncludeSpanUpdateRuleRequest request) {
    return UpdateIncludeSpanRuleRequest.newBuilder()
        .setRule(convertInput(request.updateInput()))
        .build();
  }

  private UpdateIncludeSpanRule convertInput(IncludeSpanRuleUpdate includeSpanRuleUpdate) {
    return UpdateIncludeSpanRule.newBuilder()
        .setId(includeSpanRuleUpdate.id())
        .setName(includeSpanRuleUpdate.name())
        .setFilter(this.filterConverter.convert(includeSpanRuleUpdate.spanFilter()))
        .setDisabled(includeSpanRuleUpdate.disabled())
        .build();
  }

  DeleteIncludeSpanRuleRequest convert(IncludeSpanDeleteRuleRequest request) {
    return DeleteIncludeSpanRuleRequest.newBuilder().setId(request.id()).build();
  }

  CreateApiNamingRuleRequest convert(ApiNamingCreateRuleRequest request) {
    return CreateApiNamingRuleRequest.newBuilder()
        .setRuleInfo(convertInput(request.createInput()))
        .build();
  }

  private org.hypertrace.span.processing.config.service.v1.ApiNamingRuleInfo convertInput(
      ApiNamingRuleCreate apiNamingRuleCreate) {
    return ApiNamingRuleInfo.newBuilder()
        .setName(apiNamingRuleCreate.name())
        .setFilter(this.filterConverter.convert(apiNamingRuleCreate.spanFilter()))
        .setDisabled(apiNamingRuleCreate.disabled())
        .setRuleConfig(convertRuleConfig(apiNamingRuleCreate.apiNamingRuleConfig()))
        .build();
  }

  private ApiNamingRuleConfig convertRuleConfig(
      org.hypertrace.graphql.spanprocessing.schema.rule.ApiNamingRuleConfig apiNamingRuleConfig) {
    switch (apiNamingRuleConfig.apiNamingRuleConfigType()) {
      case SEGMENT_MATCHING:
        return ApiNamingRuleConfig.newBuilder()
            .setSegmentMatchingBasedConfig(
                SegmentMatchingBasedConfig.newBuilder()
                    .addAllRegexes(apiNamingRuleConfig.segmentMatchingBasedRuleConfig().regexes())
                    .addAllValues(apiNamingRuleConfig.segmentMatchingBasedRuleConfig().values())
                    .build())
            .build();
      default:
        throw new NoSuchElementException(
            "Unsupported api naming rule config type: " + apiNamingRuleConfig);
    }
  }

  UpdateApiNamingRuleRequest convert(ApiNamingUpdateRuleRequest request) {
    return UpdateApiNamingRuleRequest.newBuilder()
        .setRule(convertInput(request.updateInput()))
        .build();
  }

  private UpdateApiNamingRule convertInput(ApiNamingRuleUpdate apiNamingRuleUpdate) {
    return UpdateApiNamingRule.newBuilder()
        .setId(apiNamingRuleUpdate.id())
        .setName(apiNamingRuleUpdate.name())
        .setFilter(this.filterConverter.convert(apiNamingRuleUpdate.spanFilter()))
        .setDisabled(apiNamingRuleUpdate.disabled())
        .setRuleConfig(convertRuleConfig(apiNamingRuleUpdate.apiNamingRuleConfig()))
        .build();
  }

  DeleteApiNamingRuleRequest convert(ApiNamingDeleteRuleRequest request) {
    return DeleteApiNamingRuleRequest.newBuilder().setId(request.id()).build();
  }
}
