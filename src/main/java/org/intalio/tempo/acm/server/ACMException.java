
package org.intalio.tempo.acm.server;



public abstract class ACMException extends  Exception {

    public ACMException() {
        super();
    }

    public ACMException(String message) {
        super(message);
    }

    public ACMException(String message, Throwable cause) {
        super(message, cause);
    }

    public ACMException(Throwable cause) {
        super(cause);
    }

}
