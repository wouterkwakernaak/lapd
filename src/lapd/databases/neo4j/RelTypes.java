package lapd.databases.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum RelTypes implements RelationshipType
{
    ANNOTATION,
    HEAD,
    NEXT_ELEMENT,
    MAP_KEY_VALUE
}
