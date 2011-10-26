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

import java.util.*;
import javax.wsdl.extensions.*;

/**
 * @author Matthew J. Duftler (duftler@us.ibm.com)
 */
public interface MIMEMultipartRelated extends ExtensibilityElement,
                                              java.io.Serializable
{
  /**
   * Add a MIME part to this MIME multipart related.
   *
   * @param mimePart the MIME part to be added
   */
  public void addMIMEPart(MIMEPart mimePart);
  
  /**
   * Remove a MIME part to this MIME multipart related.
   *
   * @param mimePart the MIME part to be remove.
   * @return the MIME part which was removed.
   */
  public MIMEPart removeMIMEPart(MIMEPart mimePart);

  /**
   * Get all the MIME parts defined here.
   */
  public List getMIMEParts();
}