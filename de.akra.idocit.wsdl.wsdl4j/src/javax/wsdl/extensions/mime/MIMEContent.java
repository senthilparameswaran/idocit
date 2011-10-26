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

package javax.wsdl.extensions.mime;

import javax.wsdl.extensions.*;

/**
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public interface MIMEContent extends ExtensibilityElement, java.io.Serializable
{
  /**
   * Set the part for this MIME content.
   *
   * @param part the desired part
   */
  public void setPart(String part);

  /**
   * Get the part for this MIME content.
   */
  public String getPart();

  /**
   * Set the type for this MIME content.
   *
   * @param type the desired type
   */
  public void setType(String type);

  /**
   * Get the type for this MIME content.
   */
  public String getType();
}