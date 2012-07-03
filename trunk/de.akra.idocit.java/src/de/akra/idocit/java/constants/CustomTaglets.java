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
package de.akra.idocit.java.constants;

public enum CustomTaglets
{
	PARAM_INFO("paraminfo", "Parameter-Info"),
	RETURN_INFO("returninfo", "Return-Info"),
	THROWS_INFO("throwsinfo", "Throw-Info"),
	SUB_RETURN("subreturn", "Subreturn"),
	SUB_RETURN_INFO("subreturninfo", "Subreturn-Info"),
	SUB_PARAM("subparam", "Subparameter"),
	SUB_PARAM_INFO("subparaminfo", "Subparameter-Info"),
	SUB_THROWS_INFO("subthrowsinfo", "Subthrow-Info"),
	THEMATIC_GRID("thematicgrid", "Thematic Grid");

	private String name;
	private String header;

	CustomTaglets(final String name, final String header)
	{
		this.name = name;
		this.header = header;
	}

	/**
	 * 
	 * @return [OBJECT] name of the taglet without '@'. Example: <code>thematicgrid</code>
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * 
	 * @return [OBJECT] the name of the taglet with leading '@'. Example: <code>@thematicgrid</code>
	 */
	public String getTagName()
	{
		return "@" + name;
	}

	/**
	 * The header is the headline name within the exported Javadoc (e.g. HTML). 
	 * 
	 * @return [OBJECT] the header.
	 */
	public String getHeader()
	{
		return header;
	}
}
