package org.hypertrace.core.graphql.rx;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.inject.Inject;
import javax.inject.Provider;
import org.hypertrace.core.graphql.spi.config.GraphQlServiceConfig;

/**
 * A scheduler using up to a configuration-based number of threads.
 *
 * <p>This differs from the default Scheduler.io implementation which uses an unbounded number of
 * threads.
 */
class BoundedIoSchedulerProvider implements Provider<Scheduler> {

  private final Scheduler scheduler;
  private final GraphQlServiceConfig serviceConfig;

  @Inject
  BoundedIoSchedulerProvider(GraphQlServiceConfig serviceConfig) {
    this.serviceConfig = serviceConfig;
    this.scheduler = Schedulers.from(this.buildExecutor());
  }

  @Override
  public Scheduler get() {
    return this.scheduler;
  }

  private Executor buildExecutor() {
    return Executors.newFixedThreadPool(
        this.serviceConfig.getMaxIoThreads(), this.buildThreadFactory());
  }

  private ThreadFactory buildThreadFactory() {
    return new ThreadFactoryBuilder()
        .setDaemon(true)
        .setNameFormat("rx-bounded-io-scheduler-%d")
        .build();
  }
}
