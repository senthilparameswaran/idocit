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

/**
 * This interface represents a fault binding. That is, it contains
 * the information that would be specified in a fault element
 * contained within an operation element contained within a
 * binding element.
 *
 * @author Matthew J. Duftler
 */
public interface BindingFault extends WSDLElement
{
  /**
   * Set the name of this fault binding.
   *
   * @param name the desired name
   */
  public void setName(String name);

  /**
   * Get the name of this fault binding.
   *
   * @return the fault binding name
   */
  public String getName();

}