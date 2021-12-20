package org.hypertrace.graphql.label.joiner;

import com.google.inject.AbstractModule;
import org.hypertrace.graphql.label.dao.LabelDao;

public class LabelJoinerModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(LabelJoinerBuilder.class).to(DefaultLabelJoinerBuilder.class);

    requireBinding(LabelDao.class);
  }
}
