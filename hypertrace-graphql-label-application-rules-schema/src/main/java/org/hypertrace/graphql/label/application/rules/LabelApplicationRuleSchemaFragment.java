package org.hypertrace.graphql.label.application.rules;

import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;
import org.hypertrace.graphql.label.application.rules.schema.mutation.LabelApplicationRuleMutationSchema;
import org.hypertrace.graphql.label.application.rules.schema.query.LabelApplicationRuleQuerySchema;

public class LabelApplicationRuleSchemaFragment implements GraphQlSchemaFragment {

  @Override
  public String fragmentName() {
    return "Label Application Rules Schema";
  }

  @Override
  public Class<LabelApplicationRuleQuerySchema> annotatedQueryClass() {
    return LabelApplicationRuleQuerySchema.class;
  }

  @Override
  public Class<?> annotatedMutationClass() {
    return LabelApplicationRuleMutationSchema.class;
  }
}
