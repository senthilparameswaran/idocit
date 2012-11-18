/*******************************************************************************
 * Copyright {date} AKRA GmbH
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
package de.akra.idocit.java.utils;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class JavadocUtilsTest
{

	@Test
	public void testEscapeXmlBrackets()
	{
		assertEquals("<br/>", JavadocUtils.escapeXmlBrackets("<br/>"));
		assertEquals(
				"Ascending &lt;&gt;<br/> Test y &lt;= x<br/> Test y &gt;= x",
				JavadocUtils
						.escapeXmlBrackets("Ascending <><br/> Test y <= x<br/> Test y >= x"));
	}
	
	@Test
	public void testEscapeHtml4() throws ParserConfigurationException, SAXException, IOException
	{
		assertEquals("<br/>", JavadocUtils.escapeHtml4("<br/>"));
		assertEquals(
				"Ascending &lt;&gt;<br/> Test y &lt;= x<br/> Test y &gt;= x",
				JavadocUtils
						.escapeHtml4("Ascending <>\n Test y <= x\n Test y >= x"));
	}

}
