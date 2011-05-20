/*******************************************************************************
 *   Copyright 2011 AKRA GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *******************************************************************************/
package de.akra.idocit.core.services;

import static org.junit.Assert.assertEquals;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import de.akra.idocit.core.extensions.Parser;
import de.akra.idocit.core.services.ParsingService;

/**
 * Tests for {@link ParsingService}.
 * 
 * @author Dirk Meier-Eickhoff
 * 
 */
public class ParsingServiceTest
{
	private static Logger logger = Logger.getLogger(ParsingServiceTest.class.getName());

	/**
	 * Tests {@link ParsingService#getParser(String)}.
	 */
	@Test
	public void testGetParser()
	{
		/*
		 * Positive tests
		 * ******************************************************************************
		 * Test case #1: Get the parser for WSDL files, with the type "wsdl".
		 * ******************************************************************************
		 */
		{
			Parser parser = ParsingService.getParser("wsdl");
			logger.log(Level.FINE, "The received parser for \"wsdl\": " + parser);
			assertEquals(true, parser != null);
		}

		/*
		 * Negative tests
		 * ***************************************************************************
		 * Test case #1: Getting the parser for the type "xyz" should fail.
		 * ****************************************************************************
		 */
		{
			Parser parser = ParsingService.getParser("xyz");
			logger.log(Level.FINE, "The received parser for \"xyz\": " + parser);
			assertEquals(true, parser == null);
		}
	}

}
