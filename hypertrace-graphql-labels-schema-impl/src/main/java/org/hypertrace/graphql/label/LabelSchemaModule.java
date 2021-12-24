package org.hypertrace.graphql.label;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.hypertrace.core.graphql.spi.schema.GraphQlSchemaFragment;
import org.hypertrace.graphql.label.dao.LabelApplicationRuleDaoModule;
import org.hypertrace.graphql.label.dao.LabelDaoModule;
import org.hypertrace.graphql.label.deserialization.LabelApplicationRuleDeserializationModule;
import org.hypertrace.graphql.label.deserialization.LabelDeserializationModule;
import org.hypertrace.graphql.label.joiner.LabelJoinerModule;
import org.hypertrace.graphql.label.request.LabelApplicationRuleRequestModule;
import org.hypertrace.graphql.label.request.LabelRequestModule;

public class LabelSchemaModule extends AbstractModule {
  @Override
  protected void configure() {
    Multibinder.newSetBinder(binder(), GraphQlSchemaFragment.class)
        .addBinding()
        .to(LabelSchemaFragment.class);

    install(new LabelDaoModule());
    install(new LabelDeserializationModule());
    install(new LabelRequestModule());
    install(new LabelJoinerModule());
    install(new LabelApplicationRuleDaoModule());
    install(new LabelApplicationRuleRequestModule());
    install(new LabelApplicationRuleDeserializationModule());
  }
}
