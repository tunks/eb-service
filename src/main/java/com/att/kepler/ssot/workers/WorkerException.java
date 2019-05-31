/**
 *
 *  AT&T Service Assurance Team copyright 2016
 *
 */
package com.att.kepler.ssot.workers;

/**
 * WorkerException
 *
 */
public class WorkerException extends RuntimeException {

    public WorkerException() {
    }

    public WorkerException(String message) {
        super(message);
    }

    public WorkerException(String message, Throwable cause) {
        super(message, cause);
    }

}
