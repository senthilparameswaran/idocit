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
 * This class represents an import, and may contain a reference
 * to the imported definition.
 *
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public class ImportImpl extends AbstractWSDLElement implements Import
{
  protected String namespaceURI = null;
  protected String locationURI = null;
  /*
    This would need to be made into a generic reference to handle other
    types of referenced documents.
  */
  protected Definition definition = null;  
  protected List nativeAttributeNames =
    Arrays.asList(Constants.IMPORT_ATTR_NAMES);

  public static final long serialVersionUID = 1;

  public void setNamespaceURI(String namespaceURI)
  {
    this.namespaceURI = namespaceURI;
  }

  public String getNamespaceURI()
  {
    return namespaceURI;
  }

  public void setLocationURI(String locationURI)
  {
    this.locationURI = locationURI;
  }

  public String getLocationURI()
  {
    return locationURI;
  }

  /**
   * This property can be used to hang a referenced Definition,
   * and the top-level Definition (i.e. the one with the &lt;import&gt;)
   * will use this Definition when resolving referenced WSDL parts.
   * This would need to be made into a generic reference to handle
   * other types of referenced documents.
   */
  public void setDefinition(Definition definition)
  {
    this.definition = definition;
  }

  /**
   * This property can be used to hang a referenced Definition,
   * and the top-level Definition (i.e. the one with the &lt;import&gt;)
   * will use this Definition when resolving referenced WSDL parts.
   * This would need to be made into a generic reference to handle
   * other types of referenced documents.
   */
  public Definition getDefinition()
  {
    return definition;
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

    strBuf.append("Import:");

    if (namespaceURI != null)
    {
      strBuf.append("\nnamespaceURI=" + namespaceURI);
    }

    if (locationURI != null)
    {
      strBuf.append("\nlocationURI=" + locationURI);
    }

    if (definition != null)
    {
      //only printing out the defintion URI and TNS to avoid infinite loop
      //if there are circular imports
      strBuf.append("\ndefinition=" + definition.getDocumentBaseURI());
      strBuf.append("\ndefinition namespaceURI=" + definition.getTargetNamespace());
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
