package de.akra.idocit.wsdl.services;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ WSDLParserTest.class, WSDLParsingServiceTest.class })
public class AllIDocItWsdlTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllIDocItWsdlTests.class.getName());
		// $JUnit-BEGIN$

		// $JUnit-END$
		return suite;
	}

}
