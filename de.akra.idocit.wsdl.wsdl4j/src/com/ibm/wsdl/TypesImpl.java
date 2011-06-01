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
 * This class represents the &lt;types&gt; section of a WSDL document.
 *
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public class TypesImpl extends AbstractWSDLElement implements Types
{
  protected List nativeAttributeNames =
    Arrays.asList(Constants.TYPES_ATTR_NAMES);

  public static final long serialVersionUID = 1;

  public String toString()
  {
    StringBuffer strBuf = new StringBuffer();

    strBuf.append("Types:");

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
