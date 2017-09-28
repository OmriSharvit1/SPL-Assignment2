package bgu.spl.a2;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * represents a work stealing thread pool - to understand what this class does
 * please refer to your assignment.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class WorkStealingThreadPool {
	private final Processor[] _processors;
	private final Thread[] _threads;
	private ArrayList<ConcurrentLinkedDeque<Task<?>>> _Tasks;
	private final VersionMonitor vm = new VersionMonitor();   
	
	/**
	 * creates a {@link WorkStealingThreadPool} which has nthreads {@link
	 * Processor}s. Note, threads should not get started until calling to the
	 * {@link #start()} method.
	 *
	 * Implementors note: you may not add other constructors to this class nor
	 * you allowed to add any other parameter to this constructor - changing
	 * this may cause automatic tests to fail..
	 *
	 * @param nthreads the number of threads that should be started by this
	 * thread pool
	 */
	public WorkStealingThreadPool(int nthreads) {
		_processors = new Processor[nthreads];
		_Tasks = new ArrayList<ConcurrentLinkedDeque<Task<?>>>();
		_threads = new Thread[nthreads];
		for (int i = 0; i < nthreads; i++) {
			_Tasks.add(i, new ConcurrentLinkedDeque<Task<?>>());
			_processors[i] = new Processor(i, this);
			_threads[i] = new Thread(_processors[i]);
		}
	}

	/**
	 * submits a task to be executed by a processor belongs to this thread pool
	 * @param task -  the task to execute
	 */
	public void submit(Task<?> task) {
		Random randomno = new Random();	
		int k= randomno.nextInt(_threads.length);
		_Tasks.get(k).addFirst(task);
		vm.inc();
	}

	/**
	 * closes the thread pool - this method interrupts all the threads and wait
	 * for them to stop - it is returns *only* when there are no live threads in
	 * the queue.
	 *
	 * after calling this method - one should not use the queue anymore.
	 *
	 * @throws InterruptedException
	 *             if the thread that shut down the threads is interrupted
	 * @throws UnsupportedOperationException
	 *             if the thread that attempts to shutdown the queue is itself a
	 *             processor of this queue
	 */
	public void shutdown() throws InterruptedException {
		for (int i = 0; i < _threads.length; i++) {
			if (!(_threads[i].equals(Thread.currentThread()))) {
				vm.inc();
				_threads[i].interrupt();
				_threads[i].join();
			} else
				throw new UnsupportedOperationException(
						"The thread that attempts to shutdown the queue is itself a processor of this queue");
		}
	}

	/**
	 * start the threads belongs to this thread pool
	 */
	public void start() {
		for (Thread currentThread : _threads) {
			currentThread.start();
		}
	}
	/**
	 * steal tasks from a processor in a circular operation.
	 * @param id - of the processor who asks to steal, oldVm - the Version monitor at the time that function was called.
	 */
	/* package */ protected void steal(int id, int oldVM) {
		int victim = (id+1)%_threads.length;
		boolean steal = false;
		while (victim != id && !steal) {			
			if (victim != id && _Tasks.get(victim).size() > 1)
				steal = true;
			else
				victim=(victim+1)%_threads.length;
		}
		int half = 0;
		while (steal && ((_Tasks.get(victim).size())/ 2) > half) {
				Task <?> t= _Tasks.get(victim).pollLast();
				if(t!=null){
					_Tasks.get(id).addFirst(t);
					vm.inc();
					half++;
			}
		}
		if (!steal) {
			try {
				vm.await(oldVM);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}
	
	/**
	 * return a ConcurrentLinkedDeque<Task<?>> of the tasks
	 */
	public ConcurrentLinkedDeque<Task<?>> getTasks(int i) {
		return _Tasks.get(i);
	}
	
	/**
	 * return the VersionMonitor
	 */
	public VersionMonitor getVM(){
		return vm;
	}
}
