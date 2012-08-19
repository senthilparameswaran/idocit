/*******************************************************************************
 * Copyright 2012 AKRA GmbH
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
	
	@Test
	public void testMergeSpaces(){
		Assert.assertEquals(" hello world ", StringUtils.mergeSpaces("  hello  world  "));
		Assert.assertEquals(" hello world ", StringUtils.mergeSpaces("   hello   world   "));
		Assert.assertEquals("hello world", StringUtils.mergeSpaces("hello world"));
		Assert.assertEquals("helloworld", StringUtils.mergeSpaces("helloworld"));
	}
}
