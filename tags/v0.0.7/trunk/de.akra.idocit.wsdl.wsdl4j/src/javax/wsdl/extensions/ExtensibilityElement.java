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

package javax.wsdl.extensions;

import javax.xml.namespace.*;

/**
 * This interface should be implemented by classes intending to represent
 * extensions.
 *
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public interface ExtensibilityElement
{
  /**
   * Set the type of this extensibility element.
   *
   * @param elementType the type
   */
  public void setElementType(QName elementType);

  /**
   * Get the type of this extensibility element.
   *
   * @return the extensibility element's type
   */
  public QName getElementType();

  /**
   * Set whether or not the semantics of this extension
   * are required. Relates to the wsdl:required attribute.
   */
  public void setRequired(Boolean required);

  /**
   * Get whether or not the semantics of this extension
   * are required. Relates to the wsdl:required attribute.
   */
  public Boolean getRequired();
}