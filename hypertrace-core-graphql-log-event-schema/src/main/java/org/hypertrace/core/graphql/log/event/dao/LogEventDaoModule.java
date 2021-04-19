package org.hypertrace.core.graphql.log.event.dao;

import com.google.inject.AbstractModule;

public class LogEventDaoModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(LogEventDao.class).to(MockLogEventDao.class);
  }
}
