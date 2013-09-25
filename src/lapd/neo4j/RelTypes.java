package lapd.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum RelTypes implements RelationshipType
{
    NEXT_LIST_ELEMENT,
    LIST_HEAD,
    NEXT_TUPLE_ELEMENT,
    TUPLE_HEAD,
    NEXT_SET_ELEMENT,
    SET_HEAD,
    NEXT_MAP_ELEMENT,
    MAP_HEAD,
    IS_MAP_VALUE,
    NEXT_CHILD_NODE,
    NODE_HEAD,
    ANNOTATION_NODE,
    NEXT_CHILD_CONSTRUCTOR,
    CONSTRUCTOR_HEAD,
    ANNOTATION_CONSTRUCTOR
}
