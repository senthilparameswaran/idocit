/*******************************************************************************
 * Copyright 2011 AKRA GmbH
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

package com.ibm.wsdl;

import java.util.*;

import javax.wsdl.*;

/**
 * This class represents an output binding. That is, it contains
 * the information that would be specified in an output element
 * contained within an operation element contained within a
 * binding element.
 *
 * @author Matthew J. Duftler
 */
public class BindingOutputImpl extends AbstractWSDLElement implements BindingOutput
{
  protected String name = null;
  protected List nativeAttributeNames =
    Arrays.asList(Constants.BINDING_OUTPUT_ATTR_NAMES);

  public static final long serialVersionUID = 1;

  /**
   * Set the name of this output binding.
   *
   * @param name the desired name
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Get the name of this output binding.
   *
   * @return the output binding name
   */
  public String getName()
  {
    return name;
  }

  public String toString()
  {
    StringBuffer strBuf = new StringBuffer();

    strBuf.append("BindingOutput: name=" + name);

    String superString = super.toString();
    if(!superString.equals(""))
    {
      strBuf.append("\n");
      strBuf.append(superString);
    }

    return strBuf.toString();
  }
  
  /**
   * Get the list of local attribute names defined for this element in
   * the WSDL specification.
   *
   * @return a List of Strings, one for each local attribute name
   */
  public List getNativeAttributeNames()
  {
    return nativeAttributeNames;
  }
}
