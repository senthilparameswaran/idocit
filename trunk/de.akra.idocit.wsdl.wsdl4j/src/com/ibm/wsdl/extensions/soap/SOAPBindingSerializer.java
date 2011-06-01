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

package com.ibm.wsdl.extensions.soap;

import java.io.*;
import org.w3c.dom.*;
import javax.wsdl.*;
import javax.wsdl.extensions.*;
import javax.wsdl.extensions.soap.*;
import javax.xml.namespace.*;
import com.ibm.wsdl.*;
import com.ibm.wsdl.util.xml.*;

/**
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public class SOAPBindingSerializer implements ExtensionSerializer,
                                              ExtensionDeserializer,
                                              Serializable
{
  public static final long serialVersionUID = 1;

  public void marshall(Class parentType,
                       QName elementType,
                       ExtensibilityElement extension,
                       PrintWriter pw,
                       Definition def,
                       ExtensionRegistry extReg)
                         throws WSDLException
  {
    SOAPBinding soapBinding = (SOAPBinding)extension;

    if (soapBinding != null)
    {
      String tagName =
        DOMUtils.getQualifiedValue(SOAPConstants.NS_URI_SOAP,
                                   "binding",
                                   def);

      pw.print("    <" + tagName);

      DOMUtils.printAttribute(SOAPConstants.ATTR_STYLE,
                              soapBinding.getStyle(),
                              pw);
      DOMUtils.printAttribute(SOAPConstants.ATTR_TRANSPORT,
                              soapBinding.getTransportURI(),
                              pw);

      Boolean required = soapBinding.getRequired();

      if (required != null)
      {
        DOMUtils.printQualifiedAttribute(Constants.Q_ATTR_REQUIRED,
                                         required.toString(),
                                         def,
                                         pw);
      }

      pw.println("/>");
    }
  }

  public ExtensibilityElement unmarshall(Class parentType,
                                         QName elementType,
                                         Element el,
                                         Definition def,
                                         ExtensionRegistry extReg)
                                           throws WSDLException
  {
    SOAPBinding soapBinding = (SOAPBinding)extReg.createExtension(parentType,
                                                                  elementType);
    String transportURI = DOMUtils.getAttribute(el,
                                                SOAPConstants.ATTR_TRANSPORT);
    String style = DOMUtils.getAttribute(el, SOAPConstants.ATTR_STYLE);
    String requiredStr = DOMUtils.getAttributeNS(el,
                                                 Constants.NS_URI_WSDL,
                                                 Constants.ATTR_REQUIRED);

    if (transportURI != null)
    {
      soapBinding.setTransportURI(transportURI);
    }

    if (style != null)
    {
      soapBinding.setStyle(style);
    }

    if (requiredStr != null)
    {
      soapBinding.setRequired(new Boolean(requiredStr));
    }

    return soapBinding;
  }
}