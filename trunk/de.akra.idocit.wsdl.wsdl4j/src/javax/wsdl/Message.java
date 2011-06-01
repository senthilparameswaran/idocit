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

import java.util.*;
import javax.xml.namespace.QName;

/**
 * This interface describes a message used for communication with an operation.
 *
 * @author Paul Fremantle
 * @author Nirmal Mukhi
 * @author Matthew J. Duftler
 */
public interface Message extends WSDLElement
{
  /**
   * Set the name of this message.
   *
   * @param name the desired name
   */
  public void setQName(QName name);

  /**
   * Get the name of this message.
   *
   * @return the message name
   */
  public QName getQName();

  /**
   * Add a part to this message.
   *
   * @param part the part to be added
   */
  public void addPart(Part part);

  /**
   * Get the specified part.
   *
   * @param name the name of the desired part.
   * @return the corresponding part, or null if there wasn't
   * any matching part
   */
  public Part getPart(String name);

  /**
   * Remove the specified part.
   *
   * @param name the name of the part to be removed.
   * @return the part which was removed
   */
  public Part removePart(String name);

  /**
   * Get all the parts defined here.
   */
  public Map getParts();

  /**
   * Get an ordered list of parts as specified by the partOrder
   * argument.
   *
   * @param partOrder a list of strings, with each string referring
   * to a part by its name. If this argument is null, the parts are
   * returned in the order in which they were added to the message.
   * @return the list of parts
   */
  public List getOrderedParts(List partOrder);

  public void setUndefined(boolean isUndefined);

  public boolean isUndefined();
}