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

import javax.wsdl.extensions.schema.Schema;
import javax.wsdl.extensions.schema.SchemaReference;

/**
 * @author Jeremy Hughes <hughesj@uk.ibm.com>
 */
public class SchemaReferenceImpl implements SchemaReference
{

  public static final long serialVersionUID = 1;

  private String id = null;

  private String schemaLocation = null;

  private Schema referencedSchema = null;

  /**
   * @return Returns the id.
   */
  public String getId()
  {
    return this.id;
  }

  /**
   * @param id The id to set.
   */
  public void setId(String id)
  {
    this.id = id;
  }

  /**
   * @return Returns the schemaLocation.
   */
  public String getSchemaLocationURI()
  {
    return this.schemaLocation;
  }

  /**
   * @param schemaLocation The schemaLocation to set.
   */
  public void setSchemaLocationURI(String schemaLocation)
  {
    this.schemaLocation = schemaLocation;
  }

  /**
   * @return Returns the importedSchema.
   */
  public Schema getReferencedSchema()
  {
    return this.referencedSchema;
  }

  /**
   * @param referencedSchema The importedSchema to set.
   */
  public void setReferencedSchema(Schema referencedSchema)
  {
    this.referencedSchema = referencedSchema;
  }
}