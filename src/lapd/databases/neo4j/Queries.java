package lapd.databases.neo4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.IValueFactory;
import org.eclipse.imp.pdb.facts.type.Type;
import org.eclipse.imp.pdb.facts.type.TypeStore;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;

// Predefined java traversals
public class Queries {
	
	public static ISet recursiveMethods(Iterable<Node> allNodes, IValueFactory vf, Type type, TypeStore ts) throws GraphDbMappingException {
		Set<IValue> results = new HashSet<IValue>();	
		for (Node node : allNodes) {
			if (node.hasRelationship(RelTypes.TO, Direction.OUTGOING)) {
				for (Relationship rel : node.getRelationships(RelTypes.TO, Direction.OUTGOING)) {
					if (rel.getEndNode().equals(node))
						results.add(type.accept(new GraphDbValueRetrievalVisitor(node, vf, ts)));
				}
			}
		}
		return vf.set(results.toArray(new IValue[results.size()]));
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
				while (statement.hasRelationship(RelTypes.TO, Direction.OUTGOING)) {
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
		return node.getSingleRelationship(RelTypes.TO, Direction.OUTGOING).getEndNode();
	}

}
