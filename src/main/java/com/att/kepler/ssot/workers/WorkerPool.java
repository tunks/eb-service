/**
 *
 *  AT&T Service Assurance Team copyright 2016
 *
 */
package com.att.kepler.ssot.workers;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

/**
 * Base WorkerPool interface
 *
 * @param <ID>
 * @param <T>
 */
public interface WorkerPool<ID extends Serializable, T> {

    /**
     * Add and execute worker
     *
     * @param object
     */
    public void execute(T object);

    /**
     * Toggle all workers
     *
     * @param paused
     */
    public void togglePause(boolean paused);
    /**
     * Toggle pause worker by id
     *
     * @param id
     * @param paused
     */
    public void togglePause(ID id, boolean paused);

    /**
     * Shutdown worker by
     *
     * @param id
     */
    public void shutdown(ID id);

    /**
     * Shutdown list of workers by collection ids
     *
     * @param ids
     */
    public void shutdown(Collection<ID> ids);

    /**
     * shutdown all workers in the worker pool
     */
    public void shutdown();

    /**
     * Get the size of workers in the worker pool
     *
     * @return
     */
    public int getWorkerCount();

    /**
     * Get active worker count
     *
     * @return
     */
    public int getActiveWorkerCount();

    /**
     * Get active worker count
     *
     * @return
     */
    public int getIdleWorkerCount();

    /**
     * All worker to pool
     *
     * @param object
     */
    public boolean addWorker(T object) throws WorkerException;

    /**
     * Get all workers
     *
     * @return, List of workers
     */
    public ConcurrentMap<? extends Serializable, T> getWorkers();
    
    /**
     * Remove worker from pool
     */
    
}
