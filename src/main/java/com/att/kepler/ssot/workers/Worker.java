/**
 *
 *  AT&T Service Assurance Team copyright 2016
 *
 */
package com.att.kepler.ssot.workers;


/**
 * A Worker to processing assignment of tickets and users from data queue
 *
 * 
 */
public interface Worker extends Runnable {

    /**
     * Determine if worker is processing
     *
     * @return
     */
    public boolean isProcessing();

    /**
     * Determine is worker is terminated
     *
     * @return
     */
    public boolean isTerminated();

    /**
     * Stop worker execution
     *
     * @param now, true - stop and shutdown immediately, false - shutdown after
     * the current worker jobs are all processes
     */
    public void shutdown(boolean now);

    /**
     * Toggle whether this worker will process any new jobs.
     *
     * @param paused
     */
    public void togglePause(boolean paused);


    /**
     * Get worker meta id
     *
     * @return
     */
    public String getId();

    /**
     * Determine if worker is shutdown
     *
     * @return
     */
    public boolean isShutdown();

    /**
     * Determine if worker is idle
     *
     * @return
     */
    public boolean isIdle();
}
