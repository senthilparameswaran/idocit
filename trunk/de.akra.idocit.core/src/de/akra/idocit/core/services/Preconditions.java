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
package de.akra.idocit.core.services;

/**
 * Contains methods for checking preconditions.
 * 
 * @author Dirk Meier-Eickhoff
 * @since 0.0.1
 * @version 0.0.1
 */
public class Preconditions {

    /**
     * Returns a {@link IllegalArgumentException} if <code>pvObject</code> is
     * <code>null</code> with <code>pvMessage</code> as message. 
     * 
     * @param pvObject
     *  The object to test
     * @param pvMessage
     *  The message to set in the (maybe) thrown {@link IllegalArgumentException}
     * @throws java.lang.IllegalArgumentException
     *  If <code>pvObject == null</code>
     */
    public static void checkNotNull(Object pvObject, String pvMessage) throws IllegalArgumentException {
        if (pvObject == null) {
            throw new IllegalArgumentException(pvMessage);
        }
    }
    
    /**
     * Returns a {@link IllegalArgumentException} if <code>pvBoolExpr</code> is
     * <code>false</code> with <code>pvMessage</code> as message. 
     * 
     * @param pvBoolExpr
     *  The expression to test
     * @param pvMessage
     *  The message to set in the (maybe) thrown {@link IllegalArgumentException}
     * @throws java.lang.IllegalArgumentException
     *  If <code>!pvBoolExpr</code>
     */
    public static void checkTrue(boolean pvBoolExpr, String pvMessage) throws IllegalArgumentException{
        if(!pvBoolExpr){
            throw new IllegalArgumentException(pvMessage);
        }
    }
    
    /**
     * Returns a {@link IllegalArgumentException} if <code>pvBoolExpr</code> is
     * <code>true</code> with <code>pvMessage</code> as message. 
     * 
     * @param pvBoolExpr
     *  The expression to test
     * @param pvMessage
     *  The message to set in the (maybe) thrown {@link IllegalArgumentException}
     * @throws java.lang.IllegalArgumentException
     *  If <code>pvBoolExpr</code>
     */
    public static void checkFalse(boolean pvBoolExpr, String pvMessage) throws IllegalArgumentException{
        if(pvBoolExpr){
            throw new IllegalArgumentException(pvMessage);
        }
    }
}
