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
package source;

import java.io.IOException;

public class JavadocRawComment
{
	/**
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @see {@link StringBuffer}
	 * @author diei
	 * @since now
	 */
	public String foo(String s) throws IOException
	{
		return s;
	}

	/**
	 * My method
	 * 
	 * @param s
	 *            my param
	 * @return a string
	 * @throws IOException
	 *             maybe thrown
	 * @throws IllegalArgumentException
	 *             maybe if illegal arg is inserted
	 * @see {@link StringBuffer}
	 * @author diei
	 * @since now
	 */
	public String foo2(String s) throws IOException
	{
		return s;
	}
}
