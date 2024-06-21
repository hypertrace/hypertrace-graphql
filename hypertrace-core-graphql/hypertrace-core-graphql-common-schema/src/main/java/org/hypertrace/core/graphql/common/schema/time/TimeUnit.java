package org.hypertrace.core.graphql.common.schema.time;

import java.time.temporal.ChronoUnit;

public enum TimeUnit {
  MILLISECONDS(ChronoUnit.MILLIS),
  SECONDS(ChronoUnit.SECONDS),
  MINUTES(ChronoUnit.MINUTES),
  HOURS(ChronoUnit.HOURS),
  DAYS(ChronoUnit.DAYS);

  private final ChronoUnit chronoUnit;

  TimeUnit(ChronoUnit chronoUnit) {
    this.chronoUnit = chronoUnit;
  }

  public ChronoUnit getChronoUnit() {
    return chronoUnit;
  }
}
