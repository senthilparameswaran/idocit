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
import javax.xml.namespace.*;

/**
 * This interface represents a service, which groups related
 * ports to provide some functionality.
 *
 * @author Paul Fremantle
 * @author Nirmal Mukhi
 * @author Matthew J. Duftler
 */
public interface Service extends WSDLElement
{
  /**
   * Set the name of this service.
   *
   * @param name the desired name
   */
  public void setQName(QName name);

  /**
   * Get the name of this service.
   *
   * @return the service name
   */
  public QName getQName();

  /**
   * Add a port to this service.
   *
   * @param port the port to be added
   */
  public void addPort(Port port);

  /**
   * Get the specified port.
   *
   * @param name the name of the desired port.
   * @return the corresponding port, or null if there wasn't
   * any matching port
   */
  public Port getPort(String name);
  
  /**
   * Remove the specified port.
   *
   * @param name the name of the port to be removed.
   * @return the port which was removed.
   */
  public Port removePort(String name);

  /**
   * Get all the ports defined here.
   */
  public Map getPorts();
}