package lapd.neo4j.test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import lapd.neo4j.GraphDbMappingException;

import org.eclipse.imp.pdb.facts.IValue;
import org.junit.Test;

public class TestNode extends BaseGraphDbTest {

	@Test
	public void testNullaryNode() throws GraphDbMappingException {
		IValue nodeValue = valueFactory.node("");
		graphDbValueIO.write(id, nodeValue);
		assertEquals(nodeValue, graphDbValueIO.read(id, nodeValue.getType()));
	}
	
	@Test
	public void testNode() throws GraphDbMappingException {
		IValue nodeValue = valueFactory.node("someNode", createChildren());
		graphDbValueIO.write(id, nodeValue);
		assertEquals(nodeValue, graphDbValueIO.read(id, nodeValue.getType()));
	}
	
	@Test
	public void testNodeWithAnnotations() throws GraphDbMappingException {
		IValue nodeValue = valueFactory.node("someNode", createAnnotations(), createChildren());
		graphDbValueIO.write(id, nodeValue);
		assertEquals(nodeValue, graphDbValueIO.read(id, nodeValue.getType()));
	}

	private Map<String, IValue> createAnnotations() {
		Map<String, IValue> annotations = new HashMap<String, IValue>();
		annotations.put("rating", valueFactory.integer(5));
		annotations.put("annotationX", valueFactory.string("itsover9000"));		
		return annotations;
	}

	private IValue[] createChildren() {
		IValue[] children = new IValue[2];
		children[0] = valueFactory.node("nodeX", valueFactory.bool(true));
		children[1] = valueFactory.rational(5, 3);
		return children;
	}
}
