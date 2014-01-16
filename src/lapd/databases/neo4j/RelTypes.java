package lapd.databases.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum RelTypes implements RelationshipType
{
    ANNO,	// relation to indicate an annotation
    HEAD,	// pointer to the start of a list
    TO,		// general purpose relation
    VALUE,	// key to value relation on maps
    PART,	// relation to indicate disconnected graph parts
    ELE,	// element of a set/map
}
