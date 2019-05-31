/**
 *
 *  AT&T Service Assurance Team copyright 2016
 *
 */
package com.att.kepler.ssot.workers;


import java.io.IOException;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.att.kepler.ssot.dao.CrudOperations;
import com.att.kepler.ssot.dao.SaveOperations;

/**
 * DataStreamWorkerImpl is an implementation of the Worker interface to process incoming alarms assign to the cluster instance
 *
 * @author ebrimatunkara
 */
public class WorkerImpl implements Worker{

	private static final Logger logger = LoggerFactory.getLogger(WorkerImpl.class);
	private final AtomicReference<Thread> threadRef = new AtomicReference(null);
	@SuppressWarnings("unchecked")
	private AtomicReference<WorkerState> workerState = new AtomicReference(WorkerState.NEW);
	private final AtomicBoolean paused = new AtomicBoolean(false);
	private String workerId;
	private Queue<List<Document>> dataQueue;
    private SaveOperations<Document> dataOperations;

	public WorkerImpl(String workerId, Queue<List<Document>> dataQueue, SaveOperations<Document> dataOperations) {
		super();
		this.workerId = workerId;
	    this.dataQueue = dataQueue;
	    this.dataOperations = dataOperations;
	}

	/**
	 * Execute the worker thread when worker state is RUNNING or IDLE state It
	 * terminates when Worker state is SHUTDOWN
	 */
	@Override
	public void run() {
		try {
			// start work
			startWorker();
			while (isRunning()) {
				try {
					// Halt thread here and wait if worker state is paused
					checkIdleness();
					// when worker state is RUNNING , do worker operation
					List<Document> docs = dataQueue.poll();
					if (docs != null) {
						dataOperations.saveAll(docs);
					} 
   
				} catch (Exception ex) {
					ex.printStackTrace();
					Thread.currentThread().interrupt();
				}
			}
		} catch (WorkerException ex) {
			logger.error(ex.getMessage());
		}
	}


	/**
	 * Determine if worker state is SHUTDOWN
	 *
	 * @return boolean
	 */
	@Override
	public boolean isTerminated() {
		return workerState.get().equals(WorkerState.SHUTDOWN);
	}

	/**
	 * Shutdown worker thread
	 *
	 * @param now
	 */
	@Override
	public void shutdown(boolean now) {
		final Thread workerThread = this.threadRef.get();
		if (workerThread != null) {
			workerThread.interrupt();
		}
		// Release any threads waiting and shutdown
		togglePause(false, WorkerState.SHUTDOWN);
	}

	/**
	 * Toggle pause worker state paused => ture, then set worker state to IDLE,
	 *
	 * @param paused
	 */
	@Override
	public void togglePause(boolean paused) {
		togglePause(paused, paused ? WorkerState.IDLE : WorkerState.RUNNING);
	}

	/**
	 * Determine current worker state is RUNNING
	 *
	 * @return boolean
	 */
	@Override
	public boolean isProcessing() {
		return workerState.get().equals(WorkerState.RUNNING);
	}

	/**
	 * Get worker info from the worker meta data
	 *
	 * @return String
	 */
	@Override
	public String getId() {
		return workerId;
	}

	/**
	 * Determine if worker is SHUTDOWN
	 *
	 * @return boolean
	 */
	@Override
	public boolean isShutdown() {
		return workerState.get().equals(WorkerState.SHUTDOWN);
	}

	/**
	 * Determine if worker state is RUNNING OR IDLE
	 *
	 * @return boolean, return true if worker state is RUNNING or IDLE, else
	 *         return false
	 */
	private boolean isRunning() {
		return workerState.get().equals(WorkerState.IDLE) || workerState.get().equals(WorkerState.RUNNING);
	}

	/**
	 * Determine if worker status is idle
	 *
	 * @return boolean
	 */
	@Override
	public boolean isIdle() {
		return this.paused.get();// workerInfo.get().getState().equals(IDLE);
	}

	/**
	 * Initialize and start worker
	 *
	 * @throws WorkerException
	 *             when worker fails to start
	 */
	private void startWorker() throws WorkerException {
		if (getId() != null) {
			logger.info(String.format("Starting worker %s ", this.getId()));
			// Set worker name , update worker meta information on start action
			updateWorkerState(WorkerState.RUNNING);
			// set current thread reference
			setCurrentThread();
		} else {
			// throw worker exception
			throw new WorkerException("Failed to start worker, workerId is null : ");
		}

	}

	/**
	 * Create worker name and append worker Id
	 *
	 * @param String
	 */
	private String createWorkerName(String workerId) {
		return "Worker-" + workerId;
	}

	/**
	 * set current thread name and reference
	 */
	private void setCurrentThread() {
		Thread.currentThread().setName(this.getId());
		this.threadRef.set(Thread.currentThread());
	}

	/**
	 * Change worker state
	 *
	 * @param state
	 */
	private void changeWorkerState(WorkerState state) {
		updateWorkerState(state);
	}

	/**
	 * Update worker state and set worker name
	 *
	 * @param state,
	 *            worker state
	 * @param name,
	 *            worker name
	 */
	private void updateWorkerState(WorkerState state) {
		workerState.set(state);
	}

	/**
	 * Set toggle paused and change worker state
	 *
	 * @param paused,
	 * @param state,
	 */
	private void togglePause(boolean paused, WorkerState state) {
		this.paused.compareAndSet(this.paused.get(), paused);
		//changeWorkerState(state);
		// notify waiting actions
		synchronized (this.paused) {
			this.paused.notifyAll();
		}
	}

	/**
	 * Checks to see if worker is paused and idle. If so, wait until unpaused.
	 *
	 * @throws IOException
	 *             if there was an error creating the pause message
	 */
	protected void checkIdleness() throws IOException {
	    if(this.paused.get()){
		  synchronized (this.paused) {
			while (this.paused.get()) {
				try {
					logger.info("Worker paused, is waiting ..................");
					this.paused.wait();
				} catch (InterruptedException ie) {
					logger.error("Worker interrupted" + ie.getMessage());
				}
			}
		  }
	    }
	}
}
