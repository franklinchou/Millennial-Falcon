package models

import org.janusgraph.core.Multiplicity.{ONE2MANY, SIMPLE}

package object edge {

  val User2FeatureEdge =  Edge(s"${vertex.UserType} -> ${vertex.FeatureType}", SIMPLE)

  val Group2UserEdge = Edge(s"${vertex.GroupType} -> ${vertex.UserType}", ONE2MANY)

}
