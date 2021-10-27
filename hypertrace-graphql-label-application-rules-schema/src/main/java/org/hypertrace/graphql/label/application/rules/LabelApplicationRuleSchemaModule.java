package org.hypertrace.graphql.label.application.rules;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;
import org.hypertrace.graphql.label.application.rules.dao.LabelApplicationRuleDaoModule;
import org.hypertrace.graphql.label.application.rules.deserialization.LabelApplicationRuleDeserializationModule;
import org.hypertrace.graphql.label.application.rules.request.LabelApplicationRuleRequestModule;

public class LabelApplicationRuleSchemaModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), GraphQlSchemaFragment.class)
        .addBinding()
        .to(LabelApplicationRuleSchemaFragment.class);

    install(new LabelApplicationRuleDaoModule());
    install(new LabelApplicationRuleRequestModule());
    install(new LabelApplicationRuleDeserializationModule());
  }
}
