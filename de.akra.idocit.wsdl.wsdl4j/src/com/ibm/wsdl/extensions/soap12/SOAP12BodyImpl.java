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

package com.ibm.wsdl.extensions.soap12;

import java.util.*;
import javax.wsdl.extensions.soap12.*;
import javax.xml.namespace.*;

/**
 * Based on com.ibm.wsdl.extensions.soap.SOAPBodyImpl
 */
public class SOAP12BodyImpl implements SOAP12Body
{
  protected QName elementType = SOAP12Constants.Q_ELEM_SOAP_BODY;
  protected Boolean required = null;
  protected List parts = null;
  protected String use = null;
  protected String encodingStyle = null;
  protected String namespaceURI = null;

  public static final long serialVersionUID = 1;

  /**
   * Set the type of this extensibility element.
   *
   * @param elementType the type
   */
  public void setElementType(QName elementType)
  {
    this.elementType = elementType;
  }

  /**
   * Get the type of this extensibility element.
   *
   * @return the extensibility element's type
   */
  public QName getElementType()
  {
    return elementType;
  }

  /**
   * Set whether or not the semantics of this extension
   * are required. Relates to the wsdl:required attribute.
   */
  public void setRequired(Boolean required)
  {
    this.required = required;
  }

  /**
   * Get whether or not the semantics of this extension
   * are required. Relates to the wsdl:required attribute.
   */
  public Boolean getRequired()
  {
    return required;
  }

  /**
   * Set the parts for this SOAP body.
   *
   * @param parts the desired parts
   */
  public void setParts(List parts)
  {
    this.parts = parts;
  }

  /**
   * Get the parts for this SOAP body.
   */
  public List getParts()
  {
    return parts;
  }

  /**
   * Set the use for this SOAP body.
   *
   * @param use the desired use
   */
  public void setUse(String use)
  {
    this.use = use;
  }

  /**
   * Get the use for this SOAP body.
   */
  public String getUse()
  {
    return use;
  }

  /**
   * Set the encodingStyle for this SOAP body.
   *
   * @param encodingStyle the desired encodingStyle
   */
  public void setEncodingStyle(String encodingStyle)
  {
    this.encodingStyle = encodingStyle;
  }

  /**
   * Get the encodingStyle for this SOAP body.
   */
  public String getEncodingStyle()
  {
    return encodingStyle;
  }

  /**
   * Set the namespace URI for this SOAP body.
   *
   * @param namespaceURI the desired namespace URI
   */
  public void setNamespaceURI(String namespaceURI)
  {
    this.namespaceURI = namespaceURI;
  }

  /**
   * Get the namespace URI for this SOAP body.
   */
  public String getNamespaceURI()
  {
    return namespaceURI;
  }

  public String toString()
  {
    StringBuffer strBuf = new StringBuffer();

    strBuf.append("SOAPBody (" + elementType + "):");
    strBuf.append("\nrequired=" + required);

    if (parts != null)
    {
      strBuf.append("\nparts=" + parts);
    }

    if (use != null)
    {
      strBuf.append("\nuse=" + use);
    }

    if (encodingStyle != null)
    {
      strBuf.append("\nencodingStyle=" + encodingStyle);
    }

    if (namespaceURI != null)
    {
      strBuf.append("\nnamespaceURI=" + namespaceURI);
    }

    return strBuf.toString();
  }
}