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

import java.io.*;
import javax.wsdl.*;
import javax.xml.namespace.*;

/**
 * This interface should be implemented by classes which serialize
 * extension-specific instances of ExtensibilityElement into the
 * PrintWriter.
 *
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public interface ExtensionSerializer
{
  /**
   * This method serializes extension-specific instances of
   * ExtensibilityElement into the PrintWriter.
   *
   * @param parentType a class object indicating where in the WSDL
   * definition this extension was encountered. For
   * example, javax.wsdl.Binding.class would be used to indicate
   * this extensibility element was found in the list of
   * extensibility elements belonging to a javax.wsdl.Binding.
   * @param elementType the qname of the extensibility element
   * @param extension the extensibility element to serialize
   * @param def the definition this extensibility element was
   * encountered in
   * @param extReg the ExtensionRegistry to use (if needed again)
   */
  public void marshall(Class parentType,
                       QName elementType,
                       ExtensibilityElement extension,
                       PrintWriter pw,
                       Definition def,
                       ExtensionRegistry extReg)
                         throws WSDLException;
}