package org.hypertrace.graphql.entity.join;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import io.reactivex.rxjava3.core.Scheduler;
import org.hypertrace.core.graphql.common.request.FilterRequestBuilder;
import org.hypertrace.core.graphql.common.request.ResultSetRequestBuilder;
import org.hypertrace.core.graphql.deserialization.ArgumentDeserializer;
import org.hypertrace.core.graphql.rx.BoundedIoScheduler;
import org.hypertrace.core.graphql.utils.schema.GraphQlSelectionFinder;
import org.hypertrace.graphql.entity.dao.EntityDao;

public class EntityJoinerModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(EntityJoinerBuilder.class).to(DefaultEntityJoinerBuilder.class);

    requireBinding(EntityDao.class);
    requireBinding(GraphQlSelectionFinder.class);
    requireBinding(ArgumentDeserializer.class);
    requireBinding(ResultSetRequestBuilder.class);
    requireBinding(FilterRequestBuilder.class);
    requireBinding(Key.get(Scheduler.class, BoundedIoScheduler.class));
  }
}
