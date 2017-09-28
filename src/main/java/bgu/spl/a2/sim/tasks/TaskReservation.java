package bgu.spl.a2.sim.tasks;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;

/**
 * a class that represents reservation of some products which was read under "Wave" category.
 **/
public class TaskReservation extends Task<ConcurrentLinkedQueue<Product>> {
	private ConcurrentLinkedQueue<Product> _taskReservation;
	private Warehouse _warehouse;
	private String _product;
	private int _qty;
	private long _startId;
	private AtomicInteger _index;
	
	/**
	 * TaskReservation constructor
	 * @param product - the product name
	 * @param qty- the quantity of products to manufacture
	 * @param startId- The start id of this product
	 * @param warehouse- The Warehouse that will be used to manufacture the product
	 * @param index - a index represent the "Wave" number
	 */
	public TaskReservation(String product, int qty, long startId, Warehouse warehouse, AtomicInteger  index) {
		_product = product;
		_qty = qty;
		_startId = startId;
		_warehouse=warehouse;
		_taskReservation= new ConcurrentLinkedQueue<Product> ();
		_index = new AtomicInteger();
		_index.set(index.intValue());
	}
	
	/**
	 * Spawns a qty amount of productTask for a specific product
	 */
	@Override
	protected void start() {
		ArrayList<productTask> myTasks = new ArrayList<productTask>();
		for (int i = 0; i < _qty; i++) {
			productTask task = new productTask(_product, _startId +i , _warehouse);
			myTasks.add(task);
			spawn(task);
		}
		whenResolved(myTasks, () -> {			
			for (int i = 0; i < myTasks.size(); i++){
				myTasks.get(i).getResult().get().SetIndex(_index.get());
				_index.incrementAndGet();
				_taskReservation.add(myTasks.get(i).getResult().get());
				//_taskReservation.addAll(myTasks.get(i).getResult().get().getParts());
			}
			complete(_taskReservation);
		});
	}

}
