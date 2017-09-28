package bgu.spl.a2;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Describes a monitor that supports the concept of versioning - its idea is
 * simple, the monitor has a version number which you can receive via the method
 * {@link #getVersion()} once you have a version number, you can call
 * {@link #await(int)} with this version number in order to wait until this
 * version number changes.
 *
 * you can also increment the version number by one using the {@link #inc()}
 * method.
 *
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class VersionMonitor {
	private AtomicInteger _version = new AtomicInteger(0);

	/**
	 * @return The monitor's version number.
	 * 
	 */
	public int getVersion() {
		return _version.get();
	}

	/**
	 * 
	 * increase by 1 the value of this monitor's version.
	 * 
	 * this method is synchronized because only one thread should increase this
	 * monitor's version at any time. there is a case when one thread is on the
	 * inc() function right before the wait action ,after he passed the while
	 * condition. then thread 2 get CPU time, and he is operating the inc()
	 * method. then thread 1 is getting a CPU time and because he passed the
	 * while condition he will get into wait mode, even though thread 2 called
	 * notify all, and thread 1 should'nt get into wait mode.
	 *  
	 */
	public synchronized void inc() {
		_version.incrementAndGet();
		notifyAll();
	}

	/**
	 * 
	 * this method is synchronized because there is a situation when one thread
	 * passed the while condition and then there is a context switch which gives
	 * another thread a CPU time . the other thread could invoke the
	 * {@link #inc()} method, resulting the notifyAll() method to be executed
	 * too. The first thread regain the CPU time, and because the first thread
	 * wasn't in a wait state he missed the notify that the second thread
	 * invoked therefore he is missing work that maybe done by him.
	 * 
	 * @param version- the version that if it is the current version the thread should be await.
	 * @throws InterruptedException if the thread is already in a wait state and wait () action is invoked. 
	 */
	public synchronized void await(int version) throws InterruptedException {
		while (this.getVersion() == version)
			wait();

	}
}
