package lapd.databases.neo4j;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.io.IValueTextReader;
import org.eclipse.imp.pdb.facts.io.StandardTextReader;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

// Predefined java traversals
public class Queries {
	
	// Assumes the startnode is a referencenode for a M3 model
	public static ISet recursiveMethods(Node startNode, IValueFactory valueFactory) throws GraphDbMappingException {
		List<IValue> resultsList = new ArrayList<IValue>();	
		Node node = null;
		for (Relationship a : startNode.getRelationships(RelTypes.ANNOTATION, Direction.OUTGOING)) {
			Node annotation = a.getEndNode();
			if (annotation.getProperty(PropertyNames.ANNOTATION).toString().equals("methodInvocation")) {
				node = annotation;
				break;
			}
		}
		node = node.getSingleRelationship(RelTypes.HEAD, Direction.OUTGOING).getEndNode();
		while (node.hasRelationship(RelTypes.NEXT_ELEMENT, Direction.OUTGOING)) {
			Node from = node.getSingleRelationship(RelTypes.HEAD, Direction.OUTGOING).getEndNode();
			Node to = from.getSingleRelationship(RelTypes.NEXT_ELEMENT, Direction.OUTGOING).getEndNode();
			String fromLoc = from.getProperty(PropertyNames.SOURCE_LOCATION).toString();
			String toLoc = to.getProperty(PropertyNames.SOURCE_LOCATION).toString();
			if (fromLoc.contains(toLoc.subSequence(14, toLoc.length()))) {
				IValueTextReader reader = new StandardTextReader();
				try {
					resultsList.add(reader.read(valueFactory, TypeFactory.getInstance().sourceLocationType(), 
							new StringReader(toLoc)));
				} catch (Exception e) {
					throw new GraphDbMappingException(e.getMessage());
				}
			}
			node = node.getSingleRelationship(RelTypes.NEXT_ELEMENT, Direction.OUTGOING).getEndNode();
		}
		return valueFactory.set(resultsList.toArray(new IValue[resultsList.size()]));
	}

}
