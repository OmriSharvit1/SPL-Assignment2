package bgu.spl.a2;

/**
 * this class represents a single work stealing processor, it is
 * {@link Runnable} so it is suitable to be executed by threads.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 */
public class Processor implements Runnable {
	private final WorkStealingThreadPool pool;
	private final int id;

	/**
	 * constructor for this class
	 *
	 * IMPORTANT: 1) this method is package protected, i.e., only classes inside
	 * the same package can access it - you should *not* change it to
	 * public/private/protected
	 *
	 * 2) you may not add other constructors to this class nor you allowed to
	 * add any other parameter to this constructor - changing this may cause
	 * automatic tests to fail..
	 *
	 * @param id - the processor id (every processor need to have its own unique id inside its thread pool)
	 * @param pool - the thread pool which owns this processor
	 */
	public Processor(int id, WorkStealingThreadPool pool) {
		this.id = id;
		this.pool = pool;
	}
	
    /**
    *Running all the tasks of this processor.
    *    
    */
	@Override
	public void run() {
		int oldVM = pool.getVM().getVersion();
		while (!Thread.currentThread().isInterrupted()) {
			while (!(pool.getTasks(id).isEmpty())) {
				oldVM = pool.getVM().getVersion();
				Task<?> t = pool.getTasks(id).pollFirst();
				if (t != null) {
					t.handle(this);
				}
			}
 			pool.steal(id, oldVM);
			run();
		}
	}

    /**
    *
    * @return the id of this processor.
    */
	public int getId() {
		return id;
	}
	
    /**
    *
    * @return the WorkStealingThreadPool of the processor.
    */
	public WorkStealingThreadPool getPool() {
		return pool;
	}
}
