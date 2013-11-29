package lapd.databases.neo4j;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.imp.pdb.facts.IBool;
import org.eclipse.imp.pdb.facts.IConstructor;
import org.eclipse.imp.pdb.facts.IDateTime;
import org.eclipse.imp.pdb.facts.IExternalValue;
import org.eclipse.imp.pdb.facts.IInteger;
import org.eclipse.imp.pdb.facts.IList;
import org.eclipse.imp.pdb.facts.IMap;
import org.eclipse.imp.pdb.facts.INode;
import org.eclipse.imp.pdb.facts.IRational;
import org.eclipse.imp.pdb.facts.IReal;
import org.eclipse.imp.pdb.facts.ISet;
import org.eclipse.imp.pdb.facts.ISourceLocation;
import org.eclipse.imp.pdb.facts.IString;
import org.eclipse.imp.pdb.facts.ITuple;
import org.eclipse.imp.pdb.facts.IValue;
import org.eclipse.imp.pdb.facts.visitors.IValueVisitor;
import org.neo4j.unsafe.batchinsert.BatchInserter;
import org.neo4j.unsafe.batchinsert.BatchInserterIndex;

public class GraphDbValueInsertionVisitor implements IValueVisitor<Long, GraphDbMappingException> {

	private final BatchInserter inserter;
	private Map<String, Object> predefinedProperties;
	private final BatchInserterIndex nodeIndex;
	private boolean rootNode;
	private final String id;
	
	public GraphDbValueInsertionVisitor(BatchInserter inserter, Map<String, Object> predefinedProperties, BatchInserterIndex nodeIndex, boolean rootNode, String id) {
		this.inserter = inserter;
		this.predefinedProperties = predefinedProperties;
		this.nodeIndex = nodeIndex;
		this.rootNode = rootNode;
		this.id = id;
	}
	
	@Override
	public Long visitString(IString stringValue) throws GraphDbMappingException {
		Map<String, Object> properties = createPropertyMap();
		properties.put(PropertyNames.TYPE, TypeNames.STRING);
		properties.put(PropertyNames.STRING, stringValue.getValue());
		long node = createNode(properties);
		return node;
	}	
	
	@Override
	public Long visitInteger(IInteger integerValue) throws GraphDbMappingException {
		long node = createPrimitiveStringNode(integerValue, PropertyNames.INTEGER, TypeNames.INTEGER);
		return node;
	}

	@Override
	public Long visitReal(IReal realValue) throws GraphDbMappingException {
		long node = createPrimitiveStringNode(realValue, PropertyNames.REAL, TypeNames.REAL);
		return node;
	}	
	
	@Override
	public Long visitBoolean(IBool booleanValue) throws GraphDbMappingException {
		Map<String, Object> properties = createPropertyMap();
		properties.put(PropertyNames.TYPE, TypeNames.BOOLEAN);
		properties.put(PropertyNames.BOOLEAN, booleanValue.getValue());
		long node = createNode(properties);
		return node;
	}

	@Override
	public Long visitRational(IRational rationalValue) throws GraphDbMappingException {
		Map<String, Object> properties = createPropertyMap();
		properties.put(PropertyNames.TYPE, TypeNames.RATIONAL);
		properties.put(PropertyNames.NUMERATOR, rationalValue.numerator().toString());
		properties.put(PropertyNames.DENOMINATOR, rationalValue.denominator().toString());
		long node = createNode(properties);
		return node;
	}
	
	@Override
	public Long visitSourceLocation(ISourceLocation sourceLocationValue) throws GraphDbMappingException {
		long node = createPrimitiveStringNode(sourceLocationValue, PropertyNames.SOURCE_LOCATION, TypeNames.SOURCE_LOCATION);
		return node;
	}
	
	@Override
	public Long visitDateTime(IDateTime dateTimeValue) throws GraphDbMappingException {
		Map<String, Object> properties = createPropertyMap();
		properties.put(PropertyNames.TYPE, TypeNames.DATE_TIME);
		properties.put(PropertyNames.DATE_TIME, dateTimeValue.getInstant());
		long node = createNode(properties);
		return node;
	}

	@Override
	public Long visitList(IList listValue) throws GraphDbMappingException {
		return insertList(listValue);
	}	
	
	@Override
	public Long visitListRelation(IList listValue) throws GraphDbMappingException {		
		long node = insertList(listValue);
		return node;
	}
	
	@Override
	public Long visitSet(ISet setValue) throws GraphDbMappingException {	
		long node = insertSet(setValue);
		return node;
	}	

	@Override
	public Long visitRelation(ISet setValue) throws GraphDbMappingException {		
		long node = insertSet(setValue);
		return node;
	}
	
	@Override
	public Long visitNode(INode nodeValue) throws GraphDbMappingException {
		long node = createAnnotatableNode(nodeValue, TypeNames.NODE, createPropertyMap());
		return node;
	}	

	@Override
	public Long visitConstructor(IConstructor constructorValue) throws GraphDbMappingException {
		Map<String, Object> properties = createPropertyMap();
		properties.put(PropertyNames.ADT, constructorValue.getType().getAbstractDataType().getName());
		long node = createAnnotatableNode(constructorValue, TypeNames.CONSTRUCTOR, properties);
		return node;
	}

	@Override
	public Long visitTuple(ITuple tupleValue) throws GraphDbMappingException {
		Map<String, Object> properties = createPropertyMap();
		properties.put(PropertyNames.TYPE, TypeNames.TUPLE);
		long firstElementNode = createIterableNodeCollection(tupleValue.iterator(), properties);
		return firstElementNode;
	}		

	@Override
	public Long visitMap(IMap mapValue) throws GraphDbMappingException {
		Iterator<Entry<IValue, IValue>> iterator = mapValue.entryIterator();	
		Map<String, Object> properties = createPropertyMap();
		properties.put(PropertyNames.TYPE, TypeNames.MAP);
		long referenceNode = createNode(properties);
		long previousElementNode;
		if (iterator.hasNext()) {			
			Entry<IValue, IValue> entry = iterator.next();			
			previousElementNode = entry.getKey().accept(this);
			inserter.createRelationship(referenceNode, previousElementNode,  RelTypes.HEAD, null);
			addValueToMap(previousElementNode, entry.getValue());
			while (iterator.hasNext()) {
				entry = iterator.next();
				long currentElementNode = entry.getKey().accept(this);
				addValueToMap(currentElementNode, entry.getValue());
				inserter.createRelationship(previousElementNode, currentElementNode, RelTypes.NEXT_ELEMENT, null);
				previousElementNode = currentElementNode;
			}
		}
		return referenceNode;
	}
	
	@Override
	public Long visitExternal(IExternalValue externalValue)	throws GraphDbMappingException {
		throw new GraphDbMappingException("External values not supported.");
	}
	
	private Long insertList(IList listValue) throws GraphDbMappingException {
		Map<String, Object> properties = createPropertyMap();
		properties.put(PropertyNames.TYPE, TypeNames.LIST);
		long firstElementNode = createIterableNodeCollection(listValue.iterator(), properties);
		return firstElementNode;
	}
	
	private Long insertSet(ISet setValue) throws GraphDbMappingException {
		Map<String, Object> properties = createPropertyMap();
		properties.put(PropertyNames.TYPE, TypeNames.SET);		
		long firstElementNode = createIterableNodeCollection(setValue.iterator(), properties);
		return firstElementNode;
	}

	private void addValueToMap(long keyNode, IValue value) throws GraphDbMappingException {
		long valueNode = value.accept(this);
		inserter.createRelationship(keyNode, valueNode, RelTypes.MAP_KEY_VALUE, null);
	}	
	
	// stores values such as integers and reals as strings in order to allow larger then 64 bit integer storage
	private Long createPrimitiveStringNode(IValue value, String propertyName, String typeName) {
		Map<String, Object> properties = createPropertyMap();
		properties.put(PropertyNames.TYPE, typeName);
		properties.put(propertyName, value.toString());
		long node = createNode(properties);
		return node;
	}
	
	private Long createIterableNodeCollection(Iterator<IValue> iterator, Map<String, Object> properties) throws GraphDbMappingException {
		long referenceNode = createNode(properties);
		long previousElementNode;
		if (iterator.hasNext()) {
			IValue elementValue = iterator.next();
			previousElementNode = elementValue.accept(this);
			inserter.createRelationship(referenceNode, previousElementNode, RelTypes.HEAD, null);
			while (iterator.hasNext()) {
				IValue currentElementValue = iterator.next();
				long currentElementNode = currentElementValue.accept(this);
				inserter.createRelationship(previousElementNode, currentElementNode, RelTypes.NEXT_ELEMENT, null);
				previousElementNode = currentElementNode;
			}
		}
		return referenceNode;
	}

	private long createNode(Map<String, Object> properties) {
		if (rootNode) {
			properties.put("id", id);
			rootNode = false;
		}
		long referenceNode = inserter.createNode(properties);
		return referenceNode;
	}
	
	private Long createAnnotatableNode(INode nodeValue, String typeName, Map<String, Object> properties) 
			throws GraphDbMappingException {
		properties.put(PropertyNames.NODE, nodeValue.getName());
		properties.put(PropertyNames.TYPE, typeName);
		long node = createIterableNodeCollection(nodeValue.getChildren().iterator(), properties);
		for (Entry<String, IValue> annotation : nodeValue.asAnnotatable().getAnnotations().entrySet()) {
			Map<String, Object> annotationProperties = new HashMap<String, Object>();
			annotationProperties.put(PropertyNames.ANNOTATION, annotation.getKey());
			long annotationNode = annotation.getValue().accept(new GraphDbValueInsertionVisitor(inserter, annotationProperties, nodeIndex, false, id));
			inserter.createRelationship(node, annotationNode, RelTypes.ANNOTATION, null);
		}
		nodeIndex.add(node, properties);
		return node;
	}
	
	private Map<String, Object> createPropertyMap() {
		Map<String, Object> properties = new HashMap<String, Object>();
		for (Entry<String, Object> property : predefinedProperties.entrySet())
			properties.put(property.getKey(), property.getValue());
		return properties;
	}

}
