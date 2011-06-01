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

package com.ibm.wsdl.extensions.mime;

import javax.xml.namespace.*;
import com.ibm.wsdl.*;

/**
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public class MIMEConstants
{
  // Namespace URIs.
  public static final String NS_URI_MIME =
    "http://schemas.xmlsoap.org/wsdl/mime/";

  // Element names.
  public static final String ELEM_CONTENT = "content";
  public static final String ELEM_MULTIPART_RELATED = "multipartRelated";
  public static final String ELEM_MIME_XML = "mimeXml";

  // Qualified element names.
  public static final QName Q_ELEM_MIME_CONTENT =
    new QName(NS_URI_MIME, ELEM_CONTENT);
  public static final QName Q_ELEM_MIME_MULTIPART_RELATED =
    new QName(NS_URI_MIME, ELEM_MULTIPART_RELATED);
  public static final QName Q_ELEM_MIME_PART =
    new QName(NS_URI_MIME, Constants.ELEM_PART);
  public static final QName Q_ELEM_MIME_MIME_XML =
    new QName(NS_URI_MIME, ELEM_MIME_XML);

  // Attribute names.
  public static final String ATTR_PART = "part";
}