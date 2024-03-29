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

import javax.wsdl.extensions.schema.SchemaImport;

/**
 * @author Jeremy Hughes <hughesj@uk.ibm.com>
 */
public class SchemaImportImpl extends SchemaReferenceImpl implements SchemaImport
{
  public static final long serialVersionUID = 1;

  private String namespace = null;

  /**
   * @return Returns the namespace.
   */
  public String getNamespaceURI()
  {
    return this.namespace;
  }

  /**
   * @param namespace The namespace to set.
   */
  public void setNamespaceURI(String namespace)
  {
    this.namespace = namespace;
  }
}