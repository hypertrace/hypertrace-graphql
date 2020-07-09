package org.hypertrace.graphql.entity.schema;

import graphql.annotations.annotationTypes.GraphQLField;
import graphql.annotations.annotationTypes.GraphQLName;
import graphql.annotations.annotationTypes.GraphQLNonNull;
import org.hypertrace.core.graphql.common.schema.attributes.AttributeQueryable;
import org.hypertrace.core.graphql.common.schema.id.Identifiable;
import org.hypertrace.core.graphql.common.schema.type.Typed;
import org.hypertrace.graphql.entity.schema.argument.NeighborEntityTypeArgument;
import org.hypertrace.graphql.metric.schema.MetricQueryable;

@GraphQLName(Entity.TYPE_NAME)
public interface Entity
    extends AttributeQueryable, MetricQueryable, Identifiable, Typed<EntityType> {
  String TYPE_NAME = "Entity";
  String ENTITY_INCOMING_EDGES_KEY = "incomingEdges";
  String ENTITY_OUTGOING_EDGES_KEY = "outgoingEdges";

  @Override
  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(TYPE_FIELD_NAME)
  EntityType type();

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ENTITY_INCOMING_EDGES_KEY)
  EdgeResultSet incomingEdges(
      @GraphQLNonNull @GraphQLName(NeighborEntityTypeArgument.ARGUMENT_NAME)
          EntityType neighborType);

  @GraphQLField
  @GraphQLNonNull
  @GraphQLName(ENTITY_OUTGOING_EDGES_KEY)
  EdgeResultSet outgoingEdges(
      @GraphQLNonNull @GraphQLName(NeighborEntityTypeArgument.ARGUMENT_NAME)
          EntityType neighborType);
}
