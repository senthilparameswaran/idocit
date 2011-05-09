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
