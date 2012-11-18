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

package javax.wsdl.extensions.soap12;

import javax.wsdl.extensions.*;
import javax.xml.namespace.*;

/**
 * Based on javax.wsdl.extensions.SOAPHeaderFault.
 */
public interface SOAP12HeaderFault extends ExtensibilityElement,
                                         java.io.Serializable
{
  /**
   * Set the message for this SOAP header fault.
   *
   * @param message the desired message
   */
  public void setMessage(QName message);

  /**
   * Get the message for this SOAP header fault.
   */
  public QName getMessage();

  /**
   * Set the part for this SOAP header fault.
   *
   * @param part the desired part
   */
  public void setPart(String part);

  /**
   * Get the part for this SOAP header fault.
   */
  public String getPart();

  /**
   * Set the use for this SOAP header fault.
   *
   * @param use the desired use
   */
  public void setUse(String use);

  /**
   * Get the use for this SOAP header fault.
   */
  public String getUse();

  /**
   * Set the encodingStyle for this SOAP header fault.
   *
   * @param encodingStyle the desired encodingStyle
   */
  public void setEncodingStyle(String encodingStyle);

  /**
   * Get the encodingStyle for this SOAP header fault.
   */
  public String getEncodingStyle();

  /**
   * Set the namespace URI for this SOAP header fault.
   *
   * @param namespaceURI the desired namespace URI
   */
  public void setNamespaceURI(String namespaceURI);

  /**
   * Get the namespace URI for this SOAP header fault.
   */
  public String getNamespaceURI();
}