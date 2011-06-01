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

package com.ibm.wsdl.extensions.http;

import javax.xml.namespace.*;
import com.ibm.wsdl.*;

/**
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public class HTTPConstants
{
  // Namespace URIs.
  public static final String NS_URI_HTTP =
    "http://schemas.xmlsoap.org/wsdl/http/";

  // Element names.
  public static final String ELEM_ADDRESS = "address";
  public static final String ELEM_URL_ENCODED = "urlEncoded";
  public static final String ELEM_URL_REPLACEMENT = "urlReplacement";

  // Qualified element names.
  public static final QName Q_ELEM_HTTP_BINDING =
    new QName(NS_URI_HTTP, Constants.ELEM_BINDING);
  public static final QName Q_ELEM_HTTP_OPERATION =
    new QName(NS_URI_HTTP, Constants.ELEM_OPERATION);
  public static final QName Q_ELEM_HTTP_ADDRESS =
    new QName(NS_URI_HTTP, ELEM_ADDRESS);
  public static final QName Q_ELEM_HTTP_URL_ENCODED =
    new QName(NS_URI_HTTP, ELEM_URL_ENCODED);
  public static final QName Q_ELEM_HTTP_URL_REPLACEMENT =
    new QName(NS_URI_HTTP, ELEM_URL_REPLACEMENT);

  // Attribute names.
  public static final String ATTR_VERB = "verb";
}