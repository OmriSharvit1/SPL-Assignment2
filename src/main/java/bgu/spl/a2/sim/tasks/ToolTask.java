package bgu.spl.a2.sim.tasks;

import bgu.spl.a2.Deferred;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;
import bgu.spl.a2.sim.tools.Tool;

/**
 * a class that represent a acquireTool and useOn of a tool .
 **/
public class ToolTask extends Task<Tool> {
	private Product _product;
	private String _toolName;
	private Warehouse _warehouse;

	/**
	 * TaskReservation constructor
	 * @param product - the product that the require the tool
	 * @param toolName- the of the tool to acquire and useOn
	 * @param warehouse- The Warehouse that will be used to manufacture the product
	 */
	public ToolTask(Product product, String toolName, Warehouse warehouse) {
		_product = product;
		_toolName = toolName;
		_warehouse = warehouse;
	}
	
	/**
	 * acquire the tool and use it on the product parts when the tool is resolved
	 */
	@Override
	protected void start() {
		Deferred<Tool> promise = _warehouse.acquireTool(_toolName);
		if (promise.isResolved()) {
			_product.setFinalId(promise.get().useOn(_product));
			_warehouse.releaseTool(promise.get());
			complete(promise.get());
		} 
		else {
			promise.whenResolved(() -> {
				_product.setFinalId(promise.get().useOn(_product));
				_warehouse.releaseTool(promise.get());
				complete(promise.get());
			});
		}

	}

}
