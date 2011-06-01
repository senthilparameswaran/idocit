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

package com.ibm.wsdl.extensions.http;

import javax.wsdl.extensions.http.*;
import javax.xml.namespace.*;

/**
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public class HTTPBindingImpl implements HTTPBinding
{
  protected QName elementType = HTTPConstants.Q_ELEM_HTTP_BINDING;
  // Uses the wrapper type so we can tell if it was set or not.
  protected Boolean required = null;
  protected String verb = null;

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
   * Set the verb for this HTTP binding.
   *
   * @param verb the desired verb
   */
  public void setVerb(String verb)
  {
    this.verb = verb;
  }

  /**
   * Get the verb for this HTTP binding.
   */
  public String getVerb()
  {
    return verb;
  }

  public String toString()
  {
    StringBuffer strBuf = new StringBuffer();

    strBuf.append("HTTPBinding (" + elementType + "):");
    strBuf.append("\nrequired=" + required);

    if (verb != null)
    {
      strBuf.append("\nverb=" + verb);
    }

    return strBuf.toString();
  }
}