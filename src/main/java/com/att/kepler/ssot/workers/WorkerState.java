/**
 *
 *  AT&T Service Assurance Team copyright 2016
 *
 */
package com.att.kepler.ssot.workers;

/**
 * Worker status
 *
 */
public enum WorkerState {

    /**
     * The JobExecutor has not started running.
     */
    NEW,
    /**
     * The JobExecutor is currently actively running.
     */
    RUNNING,
    /**
     * The JobExecutor has is non-actively running.
     */
    IDLE,
    /**
     * The JobExecutor has shutdown.
     */
    SHUTDOWN
}
