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

/**
 * Based on javax.wsdl.extensions.SOAPOperation.
 */
public interface SOAP12Operation extends ExtensibilityElement,
                                       java.io.Serializable
{
  /**
   * Set the SOAP action attribute.
   *
   * @param soapActionURI the desired value of the SOAP
   * action header for this operation.
   */
  public void setSoapActionURI(String soapActionURI);

  /**
   * Get the value of the SOAP action attribute.
   *
   * @return the SOAP action attribute's value
   */
  public String getSoapActionURI();
  
  /**
   * Specify whether the SOAP Action is required for this operation.
   *
   * @param soapActionRequired true if the SOAP Action is required, otherwise false.
   */
  public void setSoapActionRequired(Boolean soapActionRequired);

  /**
   * Indicates whether the SOAP Action is required for this operation.
   *
   * @return true if the SOAP action is required, otherwise false.
   */
  public Boolean getSoapActionRequired();

  /**
   * Set the style for this SOAP operation.
   *
   * @param style the desired style
   */
  public void setStyle(String style);

  /**
   * Get the style for this SOAP operation.
   */
  public String getStyle();
}