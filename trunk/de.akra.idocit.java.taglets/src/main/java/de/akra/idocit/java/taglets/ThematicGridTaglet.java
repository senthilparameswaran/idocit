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
package de.akra.idocit.java.taglets;

import java.util.Map;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;

/**
 * iDocIt! specific taglet "@thematicgrid".
 * 
 * @author Jan Christian Krause
 *
 */
public class ThematicGridTaglet implements Taglet
{

	@Override
	public String getName()
	{
		return "thematicgrid";
	}

	@Override
	public boolean inConstructor()
	{
		return false;
	}

	@Override
	public boolean inField()
	{
		return false;
	}

	@Override
	public boolean inMethod()
	{
		return true;
	}

	@Override
	public boolean inOverview()
	{
		return false;
	}

	@Override
	public boolean inPackage()
	{
		return false;
	}

	@Override
	public boolean inType()
	{
		return false;
	}

	@Override
	public boolean isInlineTag()
	{
		return false;
	}

	@Override
	public String toString(Tag tag)
	{
		return "<DT/><B>Thematic Grid:</B> <DD>" + tag.text() + "</DD>";
	}

	@Override
	public String toString(Tag[] tags)
	{
		StringBuffer buffer = new StringBuffer();

		if ((tags != null) && (tags.length > 0))
		{
			for (int i = 0; i < tags.length; i++)
			{
				Tag tag = tags[i];
				buffer.append(toString(tag));

				if (i < (tags.length - 1))
				{
					buffer.append("<br/>");
				}

			}
		}

		return buffer.toString();
	}

	/**
	 * Register this Taglet.
	 * 
	 * @param tagletMap
	 *            the map to register this tag to.
	 */
	public static void register(Map tagletMap)
	{
		ThematicGridTaglet tag = new ThematicGridTaglet();
		Taglet t = (Taglet) tagletMap.get(tag.getName());
		if (t != null)
		{
			tagletMap.remove(tag.getName());
		}

		tagletMap.put(tag.getName(), tag);
	}

}
