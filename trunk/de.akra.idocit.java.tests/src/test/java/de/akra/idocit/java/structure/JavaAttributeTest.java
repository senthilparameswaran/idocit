package de.akra.idocit.java.structure;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

/**
 * Contains test-cases for {@link JavaAttribute}.
 * 
 * @author Jan Christian Krause
 * 
 */
public class JavaAttributeTest {

	/**
	 * Two JavaAttributes should only be compared by their name.
	 */
	@Test
	public void testCompareTo() {
		JavaAttribute attr1 = new JavaAttribute("A");
		JavaAttribute attr2 = new JavaAttribute("Aa");
		JavaAttribute attr3 = new JavaAttribute("B");
		JavaAttribute attr4 = new JavaAttribute("C");
		JavaAttribute attr5 = new JavaAttribute("Z");

		List<JavaAttribute> attributes = new ArrayList<JavaAttribute>();
		attributes.add(attr5);
		attributes.add(attr4);
		attributes.add(attr2);
		attributes.add(attr3);
		attributes.add(attr1);

		Collections.sort(attributes);

		List<JavaAttribute> referenceAttributes = new ArrayList<JavaAttribute>();
		referenceAttributes.add(attr1);
		referenceAttributes.add(attr2);
		referenceAttributes.add(attr3);
		referenceAttributes.add(attr4);
		referenceAttributes.add(attr5);

		assertEquals(referenceAttributes, attributes);
	}

	/**
	 * If an object is compared with null, the object is always greater.
	 */
	@Test
	public void testCompareToNull() {
		JavaAttribute attr1 = new JavaAttribute("A");
		assertEquals(Long.valueOf(attr1.compareTo(null)), Long.valueOf(-1));
		
		attr1 = new JavaAttribute();
		assertEquals(Long.valueOf(attr1.compareTo(null)), Long.valueOf(-1));
	}
}
