package de.akra.idocit.common.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for {@link StringUtils}
 * 
 * @author diei
 * 
 */
public class StringUtilsTest
{

	@Test
	public void testIsBlank()
	{
		/*
		 * Positive tests
		 */
		Assert.assertTrue(StringUtils.isBlank(null));
		Assert.assertTrue(StringUtils.isBlank(""));
		Assert.assertTrue(StringUtils.isBlank(new StringBuffer()));
		Assert.assertTrue(StringUtils.isBlank(" "));
		Assert.assertTrue(StringUtils.isBlank("     	"));
		Assert.assertTrue(StringUtils.isBlank(" 	\r\n\t "));

		/*
		 * Negative tests
		 */
		Assert.assertFalse(StringUtils.isBlank(" 	a 	"));
		Assert.assertFalse(StringUtils.isBlank("asd"));
		Assert.assertFalse(StringUtils.isBlank(" 12 "));
	}
}
