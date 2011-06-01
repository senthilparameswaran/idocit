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

package javax.wsdl;

import javax.xml.namespace.*;

/**
 * This interface represents a message part and contains the part's
 * name, elementName, typeName, and any extensibility attributes.
 *
 * @author Paul Fremantle
 * @author Nirmal Mukhi
 * @author Matthew J. Duftler
 */
public interface Part extends WSDLElement
{
  /**
   * Set the name of this part.
   *
   * @param name the desired name
   */
  public void setName(String name);

  /**
   * Get the name of this part.
   *
   * @return the part name
   */
  public String getName();

  public void setElementName(QName elementName);

  public QName getElementName();

  public void setTypeName(QName typeName);

  public QName getTypeName();
}
