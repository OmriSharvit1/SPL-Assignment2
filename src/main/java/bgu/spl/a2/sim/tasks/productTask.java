package bgu.spl.a2.sim.tasks;

import java.util.ArrayList;
import bgu.spl.a2.Task;
import bgu.spl.a2.sim.Product;
import bgu.spl.a2.sim.Warehouse;

/**
 * a class that represents a product task of one product.
 **/
public class productTask extends Task<Product> {
	private Warehouse _warehouse;
	private Product _product;
	private long _startId;
	private String _productName;
	
	/**
	 * productTask constructor
	 * @param productName - product name
	 * @param startId- The start id of this product
	 * @param warehouse- The Warehouse that will be used to manufacture the product
	 */
	public productTask(String productName, long startId, Warehouse warehouse) {
		_startId = startId;
		_warehouse = warehouse;
		_productName = productName;
		_product = new Product(_startId, _productName);
	}
	/**
	 * Start the manufacture procedure of the product 
	 */
	@Override
	protected void start() {
		String[] parts = _warehouse.getPlan(_product.getName()).getParts();
		String[] tools = _warehouse.getPlan(_product.getName()).getTools();
		ArrayList<productTask> productTasksToWait = new ArrayList<productTask>();
		ArrayList<ToolTask> toolTasksToWait = new ArrayList<ToolTask>();
		for (int i = 0; i < parts.length; i++) {
			productTask productTask = new productTask(parts[i], _startId +1, _warehouse);
			productTasksToWait.add(productTask);
			spawn(productTask);
		}
		if(parts.length>0){
		whenResolved(productTasksToWait, () -> {
			for (int i = 0; i < productTasksToWait.size(); i++)
				_product.addPart(productTasksToWait.get(i).getResult().get());
			for (int i = 0; i < tools.length; i++) {
				ToolTask toolTask = new ToolTask(_product, tools[i], _warehouse);
				toolTasksToWait.add(toolTask);
				spawn(toolTask);
			}
			whenResolved(toolTasksToWait, () -> {
				complete(_product);
			});
		});
		}
		else complete(_product);
	}
}
