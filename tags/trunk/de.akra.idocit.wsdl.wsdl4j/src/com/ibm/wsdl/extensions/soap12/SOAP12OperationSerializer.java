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

package com.ibm.wsdl.extensions.soap12;

import java.io.*;
import org.w3c.dom.*;
import javax.wsdl.*;
import javax.wsdl.extensions.*;
import javax.wsdl.extensions.soap12.*;
import javax.xml.namespace.*;
import com.ibm.wsdl.*;
import com.ibm.wsdl.util.xml.*;

/**
 * Based on com.ibm.wsdl.extensions.soap.SOAPOperationSerializer
 */
public class SOAP12OperationSerializer implements ExtensionSerializer,
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
    SOAP12Operation soapOperation = (SOAP12Operation)extension;

    if (soapOperation != null)
    {
      String tagName =
        DOMUtils.getQualifiedValue(SOAP12Constants.NS_URI_SOAP12,
                                   "operation",
                                   def);

      pw.print("      <" + tagName);

      Boolean soapActionRequired = soapOperation.getSoapActionRequired();
      String soapActionRequiredString =
        soapActionRequired == null ? null : soapActionRequired.toString();      
      
      DOMUtils.printAttribute(SOAP12Constants.ATTR_SOAP_ACTION,
                              soapOperation.getSoapActionURI(),
                              pw);
      DOMUtils.printAttribute(SOAP12Constants.ATTR_SOAP_ACTION_REQUIRED,
                              soapActionRequiredString,
                              pw);
      DOMUtils.printAttribute(SOAP12Constants.ATTR_STYLE,
                              soapOperation.getStyle(),
                              pw);

      Boolean required = soapOperation.getRequired();

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
    SOAP12Operation soapOperation =
      (SOAP12Operation)extReg.createExtension(parentType, elementType);
    String soapActionURI = DOMUtils.getAttribute(el,
                                             SOAP12Constants.ATTR_SOAP_ACTION);
    String soapActionRequiredString = DOMUtils.getAttribute(el,
                                             SOAP12Constants.ATTR_SOAP_ACTION_REQUIRED);
    String style = DOMUtils.getAttribute(el, SOAP12Constants.ATTR_STYLE);
    String requiredStr = DOMUtils.getAttributeNS(el,
                                                 Constants.NS_URI_WSDL,
                                                 Constants.ATTR_REQUIRED);
    if (soapActionURI != null)
    {
      soapOperation.setSoapActionURI(soapActionURI);
    }
    
    if(soapActionRequiredString != null)
    {
      Boolean soapActionRequired = new Boolean(soapActionRequiredString);
      soapOperation.setSoapActionRequired(soapActionRequired);
    }
    
    if (style != null)
    {
      soapOperation.setStyle(style);
    }

    if (requiredStr != null)
    {
      soapOperation.setRequired(new Boolean(requiredStr));
    }

    return soapOperation;
  }
}