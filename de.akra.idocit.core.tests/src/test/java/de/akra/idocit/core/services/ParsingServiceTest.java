/*******************************************************************************
 * Copyright 2011, 2012 AKRA GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package de.akra.idocit.core.services;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.junit.Before;
import org.junit.Test;

import de.akra.idocit.common.structure.Delimiters;
import de.akra.idocit.common.structure.InterfaceArtifact;
import de.akra.idocit.core.exceptions.UnitializedIDocItException;
import de.akra.idocit.core.extensions.Parser;
import de.akra.idocit.core.extensions.ValidationReport;
import de.akra.idocit.core.services.impl.ParsingService;
import de.akra.idocit.core.services.impl.ParsingServiceInitializer;
import de.akra.idocit.core.services.impl.ServiceManager;

/**
 * Tests for {@link ParsingService}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class ParsingServiceTest {
	private static Logger logger = Logger.getLogger(ParsingServiceTest.class
			.getName());

	@Before
	public void setUp() {
		ServiceManager.getInstance().setParsingService(new ParsingService());
		ServiceManager.getInstance().getParsingService()
				.init(new TestParserInitializer());
	}

	private static class WsdlTestParser implements Parser {
		@Override
		public InterfaceArtifact parse(IFile iFile) throws Exception {
			return null;
		}

		@Override
		public void write(InterfaceArtifact interfaceStructure, IFile iFile)
				throws Exception {
		}

		@Override
		public boolean isSupported(String type) {
			return "wsdl".equals(type);
		}

		@Override
		public String getSupportedType() {
			return "wsdl";
		}

		@Override
		public Delimiters getDelimiters() {
			return null;
		}

		@Override
		public ValidationReport validateArtifact(InterfaceArtifact artifact)
		{
			return null;
		}

	}

	private class TestParserInitializer implements ParsingServiceInitializer {

		@Override
		public Map<String, Parser> readRegisteredParsers() {
			Map<String, Parser> testParsers = new HashMap<String, Parser>();

			testParsers.put("wsdl", new WsdlTestParser());

			return testParsers;
		}

	}

	/**
	 * Tests {@link ParsingService#getParser(String)}.
	 */
	@Test
	public void testGetParser() throws UnitializedIDocItException {
		/*
		 * Positive tests
		 * ******************************************************* Test case #1:
		 * Get the parser for WSDL files, with the type "wsdl".
		 * *******************************************************
		 */
		{
			Parser parser = ServiceManager.getInstance().getParsingService()
					.getParser("wsdl");
			logger.log(Level.FINE, "The received parser for \"wsdl\": "
					+ parser);
			assertEquals(true, parser != null);
		}

		/*
		 * Negative tests
		 * ******************************************************* Test case #1:
		 * Getting the parser for the type "xyz" should fail.
		 * *******************************************************
		 */
		{
			ServiceManager.getInstance().getParsingService().init(null);
			boolean exceptionOccured = false;
			try {
				Parser parser = ServiceManager.getInstance()
						.getParsingService().getParser("xyz");
				logger.log(Level.FINE, "The received parser for \"xyz\": "
						+ parser);
			} catch (UnitializedIDocItException unInitEx) {
				exceptionOccured = true;
			}
			assertEquals(true, exceptionOccured);
		}
		{
			ServiceManager.getInstance().getParsingService()
					.init(new TestParserInitializer());

			Parser parser = ServiceManager.getInstance().getParsingService()
					.getParser("xyz");
			logger.log(Level.FINE, "The received parser for \"xyz\": " + parser);
			assertEquals(true, parser == null);
		}
	}
}
