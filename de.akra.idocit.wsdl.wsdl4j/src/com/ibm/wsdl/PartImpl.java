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
import javax.xml.namespace.*;

/**
 * This class represents a message part and contains the part's
 * name, elementName, typeName, and any extensibility attributes.
 *
 * @author Paul Fremantle
 * @author Nirmal Mukhi
 * @author Matthew J. Duftler
 */
public class PartImpl extends AbstractWSDLElement implements Part
{
  protected String name = null;
  protected QName elementName = null;
  protected QName typeName = null;
  protected List nativeAttributeNames =
    Arrays.asList(Constants.PART_ATTR_NAMES);

  public static final long serialVersionUID = 1;

  /**
   * Set the name of this part.
   *
   * @param name the desired name
   */
  public void setName(String name)
  {
    this.name = name;
  }

  /**
   * Get the name of this part.
   *
   * @return the part name
   */
  public String getName()
  {
    return name;
  }

  public void setElementName(QName elementName)
  {
    this.elementName = elementName;
  }

  public QName getElementName()
  {
    return elementName;
  }

  public void setTypeName(QName typeName)
  {
    this.typeName = typeName;
  }

  public QName getTypeName()
  {
    return typeName;
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

  public String toString()
  {
    StringBuffer strBuf = new StringBuffer();

    strBuf.append("Part: name=" + name);

    if (elementName != null)
    {
      strBuf.append("\nelementName=" + elementName);
    }

    if (typeName != null)
    {
      strBuf.append("\ntypeName=" + typeName);
    }

    String superString = super.toString();
    if(!superString.equals(""))
    {
      strBuf.append("\n");
      strBuf.append(superString);
    }

    return strBuf.toString();
  }
}
