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

package com.ibm.wsdl.extensions.schema;

import java.util.Arrays;
import java.util.List;
import javax.xml.namespace.QName;
import com.ibm.wsdl.Constants;

/**
 * Constants used for handling XML Schemas
 * 
 * @author John Kaputin <kaputin@uk.ibm.com>
 */
public class SchemaConstants {

    //Schema attribute names
    public static final String ATTR_ID = "id";
    public static final String ATTR_SCHEMA_LOCATION = "schemaLocation";
    
    //Schema element names
    public static final String ELEM_SCHEMA = "schema";
    public static final String ELEM_INCLUDE = "include";
    public static final String ELEM_REDEFINE = "redefine";

    //Schema uri
    public static final String NS_URI_XSD_1999 =
        "http://www.w3.org/1999/XMLSchema";
    public static final String NS_URI_XSD_2000 =
        "http://www.w3.org/2000/10/XMLSchema";
    public static final String NS_URI_XSD_2001 =
        "http://www.w3.org/2001/XMLSchema";
    
    //Schema qnames
    public static final QName Q_ELEM_XSD_1999 =
        new QName(NS_URI_XSD_1999, ELEM_SCHEMA);
    public static final QName Q_ELEM_XSD_2000 =
        new QName(NS_URI_XSD_2000, ELEM_SCHEMA);
    public static final QName Q_ELEM_XSD_2001 =
        new QName(NS_URI_XSD_2001, ELEM_SCHEMA);
    public static final List XSD_QNAME_LIST = Arrays.asList(new QName[]
        {Q_ELEM_XSD_1999, Q_ELEM_XSD_2000, Q_ELEM_XSD_2001});
    
    //Schema import qnames
    public static final QName Q_ELEM_IMPORT_XSD_1999 = new QName(
        NS_URI_XSD_1999, Constants.ELEM_IMPORT);
    public static final QName Q_ELEM_IMPORT_XSD_2000 = new QName(
        NS_URI_XSD_2000, Constants.ELEM_IMPORT);
    public static final QName Q_ELEM_IMPORT_XSD_2001 = new QName(
        NS_URI_XSD_2001, Constants.ELEM_IMPORT);
    public static final List XSD_IMPORT_QNAME_LIST = Arrays.asList(new QName[] 
        { Q_ELEM_IMPORT_XSD_1999, Q_ELEM_IMPORT_XSD_2000, Q_ELEM_IMPORT_XSD_2001 });


    //Schema include qnames
    public static final QName Q_ELEM_INCLUDE_XSD_1999 = new QName(
        NS_URI_XSD_1999, ELEM_INCLUDE);
    public static final QName Q_ELEM_INCLUDE_XSD_2000 = new QName(
        NS_URI_XSD_2000, ELEM_INCLUDE);
    public static final QName Q_ELEM_INCLUDE_XSD_2001 = new QName(
        NS_URI_XSD_2001, ELEM_INCLUDE);
    public static final List XSD_INCLUDE_QNAME_LIST = Arrays.asList(new QName[]
        { Q_ELEM_INCLUDE_XSD_1999, Q_ELEM_INCLUDE_XSD_2000, Q_ELEM_INCLUDE_XSD_2001 });

    //Schema redefine qnames
    public static final QName Q_ELEM_REDEFINE_XSD_1999 = new QName(
        NS_URI_XSD_1999, ELEM_REDEFINE);
    public static final QName Q_ELEM_REDEFINE_XSD_2000 = new QName(
        NS_URI_XSD_2000, ELEM_REDEFINE);
    public static final QName Q_ELEM_REDEFINE_XSD_2001 = new QName(
	    NS_URI_XSD_2001, ELEM_REDEFINE);
    public static final List XSD_REDEFINE_QNAME_LIST = Arrays.asList(new QName[]
	    { Q_ELEM_REDEFINE_XSD_1999, Q_ELEM_REDEFINE_XSD_2000, Q_ELEM_REDEFINE_XSD_2001 });


}
