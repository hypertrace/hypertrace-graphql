package org.hypertrace.graphql.label.joiner;

import io.reactivex.rxjava3.core.Single;
import org.hypertrace.graphql.label.schema.LabelResultSet;

public interface EntityAndRuleJoiner {

  /**
   * Produces label result set after joining labels with associated entities and label application
   * rules
   *
   * @return label result set
   */
  Single<LabelResultSet> joinLabelsWithEntitiesAndRules();
}
