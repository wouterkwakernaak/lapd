package lapd.databases.neo4j;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.io.IValueTextReader;
import org.eclipse.imp.pdb.facts.io.StandardTextReader;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeFactory;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;

// Predefined java traversals
public class Queries {
	
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
	
	public static ISet switchNoDefault(Index<Node> index, IValueFactory vf, Type type, TypeStore ts) throws GraphDbMappingException {
		List<IValue> resultsList = new ArrayList<IValue>();	
		for (Node switchRefNode : index.get("node", "switch")) {
			Node switchHead = getHead(switchRefNode);
			Node switchBodyRefNode = getNextEle(switchHead);
			if (switchBodyRefNode.hasRelationship(RelTypes.HEAD, Direction.OUTGOING)) { // switch contains statements
				Node switchBodyHead = getHead(switchBodyRefNode);
				Node statement = switchBodyHead;
				boolean hasDefaultCase = false;
				while (statement.hasRelationship(RelTypes.NEXT_ELEMENT, Direction.OUTGOING)) {
					if(statement.getProperty("node").toString().equals("defaultCase")) {
						hasDefaultCase = true;
						break;
					}
					statement = getNextEle(statement);
				}
				if (!hasDefaultCase)
					resultsList.add(type.accept(new GraphDbValueRetrievalVisitor(switchRefNode, vf, ts)));
			}
			else
				resultsList.add(type.accept(new GraphDbValueRetrievalVisitor(switchRefNode, vf, ts)));
		}
		return vf.set(resultsList.toArray(new IValue[resultsList.size()]));
	}
	
	private static Node getHead(Node node) {
		return node.getSingleRelationship(RelTypes.HEAD, Direction.OUTGOING).getEndNode();
	}
	
	private static Node getNextEle(Node node) {
		return node.getSingleRelationship(RelTypes.NEXT_ELEMENT, Direction.OUTGOING).getEndNode();
	}

}
