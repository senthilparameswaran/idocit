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

import java.util.*;
import javax.wsdl.extensions.ExtensibilityElement;

/**
 * Classes that implement this interface can contain extensibility
 * elements.
 * 
 * @author John Kaputin
 */
public interface ElementExtensible {
    
    /**
     * Add an extensibility element.
     *
     * @param extElement the extensibility element to be added
     */
    public void addExtensibilityElement(ExtensibilityElement extElement);
    
    /**
     * Remove an extensibility element.
     *
     * @param extElement the extensibility element to be removed
     * @return the extensibility element which was removed
     */
    public ExtensibilityElement removeExtensibilityElement(ExtensibilityElement extElement);

    /**
     * Get all the extensibility elements defined here.
     */
    public List getExtensibilityElements();


}
