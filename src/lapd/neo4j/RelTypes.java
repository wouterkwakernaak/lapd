package lapd.neo4j;

import org.neo4j.graphdb.RelationshipType;

public enum RelTypes implements RelationshipType
{
    NEXT_LIST_ELEMENT,
    LIST_HEAD,
    NEXT_TUPLE_ELEMENT,
    NEXT_SET_ELEMENT,
    NEXT_RELATION_ELEMENT,
    NEXT_LIST_RELATION_ELEMENT,
    NEXT_MAP_ELEMENT,
    IS_MAP_VALUE,
    CHILD_NODE,
    ANNOTATION_NODE,
    CHILD_CONSTRUCTOR,
    ANNOTATION_CONSTRUCTOR
}
