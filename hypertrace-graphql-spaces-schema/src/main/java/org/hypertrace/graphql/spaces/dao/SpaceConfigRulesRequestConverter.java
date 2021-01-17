package org.hypertrace.graphql.spaces.dao;

import org.hypertrace.graphql.spaces.request.SpaceConfigRuleCreationRequest;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRuleAttributeValueRule;
import org.hypertrace.graphql.spaces.schema.shared.SpaceConfigRuleDefinition;
import org.hypertrace.spaces.config.service.v1.AttributeValueRuleData;
import org.hypertrace.spaces.config.service.v1.CreateRuleRequest;
import org.hypertrace.spaces.config.service.v1.GetRulesRequest;

class SpaceConfigRulesRequestConverter {

  CreateRuleRequest convertCreationRequest(SpaceConfigRuleCreationRequest creationRequest) {
    return this.buildCreationRequestForRule(creationRequest.ruleDefinition());
  }

  GetRulesRequest convertGetRequest() {
    return GetRulesRequest.getDefaultInstance();
  }

  private CreateRuleRequest buildCreationRequestForRule(SpaceConfigRuleDefinition ruleDefinition) {
    switch (ruleDefinition.type()) {
      case ATTRIBUTE_VALUE:
        return CreateRuleRequest.newBuilder()
            .setAttributeValueRuleData(
                this.buildAttributeValueRuleData(ruleDefinition.attributeValueRule()))
            .build();
      default:
        throw new UnsupportedOperationException(
            "Cannot create unknown rule definition type: " + ruleDefinition.type().name());
    }
  }

  private AttributeValueRuleData buildAttributeValueRuleData(
      SpaceConfigRuleAttributeValueRule attributeValueRule) {
    return AttributeValueRuleData.newBuilder()
        .setAttributeKey(attributeValueRule.attributeKey())
        .setAttributeScope(attributeValueRule.attributeScope())
        .build();
  }
}
