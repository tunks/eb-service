/**
 *
 *  AT&T Service Assurance Team copyright 2016
 *
 */
package com.att.kepler.ssot.workers;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static java.util.stream.Collectors.counting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.toConcurrentMap;

/**
 * DefaultWorkerPool -- Manages the workers in the worker pool
 *
 */
public class DefaultWorkerPool implements WorkerPool<String, Worker> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultWorkerPool.class);
    /*
     */
    private ExecutorService executorService;
    /**
     * Worker factory to create objects at runtime
     */
    /**
     * Workers store in concurrent hash map collection
     */
    private ConcurrentMap<String, Worker> workers = new ConcurrentHashMap();
    /**
     * Worker meta data store
     */
    public DefaultWorkerPool() {
    	executorService = Executors.newFixedThreadPool(10);
    }
    public DefaultWorkerPool(int numberOfPool) {
    	executorService = Executors.newFixedThreadPool(numberOfPool);
    }
    /**
     * Insert to the workers concurrent collection and executor the new worker
     * thread
     *
     * Worker, a runnable thread object
     *
     * @param worker
     */
    @Override
    public void execute(Worker worker) {
        try {
            //add worker to worker concurrent map
            if(addWorker(worker)){
             //execute worker thread
              executorService.execute(worker);
            }
        } catch (WorkerException ex) {
            logger.error(ex.getMessage());
        }
    }

    /**
     * Get the size of all workers
     *
     * @return integer
     */
    @Override
    public int getWorkerCount() {
        return workers.size();
    }

    /**
     * Get the count of active workers
     *
     * @return integer
     */
    @Override
    public int getActiveWorkerCount() {
        return workers.values().stream().filter(x -> {
            return x.isProcessing();
        }).collect(counting()).intValue();
    }

    /**
     * Get idle worker count
     *
     * @return integer
     */
    @Override
    public int getIdleWorkerCount() {
        return getWorkerCount() - getActiveWorkerCount();
    }

    /**
     * Toggle pause worker by id
     *
     * @param id, worker id
     * @param paused
     */
    @Override
    public void togglePause(String id, boolean paused) {
        if (workers.containsKey(id)) {
            workers.get(id).togglePause(paused);
        }
    }

    /**
     * Shutdown worker thread object by id
     */
    @Override
    public void shutdown(String id) {
        if (workers.containsKey(id)) {
            workers.get(id).shutdown(true);
        }
    }

    @Override
    public void shutdown(Collection<String> ids) {
        ids.stream().forEach(id -> shutdown(id));
    }

    /**
     * Shutdown all workers and shutdown all workers
     */
    @Override
    public void shutdown() {
        executorService.shutdown();
    }

    /**
     * Get all workers
     *
     * @return
     */
    @Override
    public ConcurrentMap<String, Worker> getWorkers() {
        return workers;
    }

    /**
     * Add new worker to the workers concurrent collection,
     *
     * @param worker
     * @throws WorkerException, exception will be throw if worker id is missing
     * or existing worker in collections
     */
    @Override
    public boolean addWorker(Worker worker) throws WorkerException {
    	String workerId = worker.getId();
        if (workerId != null) {
            if (!workers.containsKey(workerId)) {
                workers.putIfAbsent(workerId, worker);
                return true;
            }
            return false;
        } else {
            throw new WorkerException("Failed to add worker, workerId is null : ");
        }
    }
    
	@Override
	public void togglePause(boolean paused) {
		this.workers.values().stream().forEach(worker->{
			worker.togglePause(paused);
		});
	    String msg = String.format("%s pool workers: %s",((paused)? "Deactivated": "Activated"),getActiveWorkerCount());
        logger.info(msg);
	}

}
