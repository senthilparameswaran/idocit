package de.akra.idocit.services;

import static org.junit.Assert.assertEquals;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import de.akra.idocit.extensions.Parser;

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
