package org.hypertrace.graphql.spanprocessing.dao;

import java.util.NoSuchElementException;
import javax.inject.Inject;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanCreateRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanDeleteRuleRequest;
import org.hypertrace.graphql.spanprocessing.request.mutation.ExcludeSpanUpdateRuleRequest;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ExcludeSpanRuleCreate;
import org.hypertrace.graphql.spanprocessing.schema.mutation.ExcludeSpanRuleUpdate;
import org.hypertrace.graphql.spanprocessing.schema.rule.ExcludeSpanRuleRuleType;
import org.hypertrace.span.processing.config.service.v1.CreateExcludeSpanRuleRequest;
import org.hypertrace.span.processing.config.service.v1.DeleteExcludeSpanRuleRequest;
import org.hypertrace.span.processing.config.service.v1.ExcludeSpanRuleInfo;
import org.hypertrace.span.processing.config.service.v1.RuleType;
import org.hypertrace.span.processing.config.service.v1.UpdateExcludeSpanRule;
import org.hypertrace.span.processing.config.service.v1.UpdateExcludeSpanRuleRequest;

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
        .setType(convertExcludeSpanRuleRuleType(excludeSpanRuleCreate.ruleType()))
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

  RuleType convertExcludeSpanRuleRuleType(ExcludeSpanRuleRuleType ruleType) {
    // TODO: remove this check after making this field non-nullable
    if (ruleType == null) {
      return RuleType.RULE_TYPE_USER;
    }
    switch (ruleType) {
      case SYSTEM:
        return RuleType.RULE_TYPE_SYSTEM;
      case USER:
        return RuleType.RULE_TYPE_USER;
      default:
        throw new NoSuchElementException("Unsupported exclude span rule rule type: " + ruleType);
    }
  }
}
