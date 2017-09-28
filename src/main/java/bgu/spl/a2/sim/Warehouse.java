package bgu.spl.a2.sim;

import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;
import bgu.spl.a2.sim.tools.Tool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import bgu.spl.a2.Deferred;

/**
 * A class representing the warehouse in your simulation
 * 
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 */
public class Warehouse {
	private ConcurrentHashMap<String, AtomicInteger> _tools;
	private ConcurrentHashMap<String, ManufactoringPlan> _plans;
	
	private ConcurrentLinkedQueue<Deferred<Tool>> _pliersWaitingList;
	private ConcurrentLinkedQueue<Deferred<Tool>> _driverWaitingList;
	private ConcurrentLinkedQueue<Deferred<Tool>> _hammerWaitingList;

	/**
	 * Constructor: initialize the tools and plans maps to a new ConcurrentHashMap
	 */
	public Warehouse() {
		_tools = new ConcurrentHashMap<String, AtomicInteger>();
		_plans = new ConcurrentHashMap<String, ManufactoringPlan>();
		
		_pliersWaitingList=new ConcurrentLinkedQueue<Deferred<Tool>>();
		_driverWaitingList=new ConcurrentLinkedQueue<Deferred<Tool>>();
		_hammerWaitingList=new ConcurrentLinkedQueue<Deferred<Tool>>();
	}

	/**
	 * Tool acquisition procedure Note that this procedure is non-blocking and
	 * should return immediatly
	 * 
	 *  * this method is synchronized because there is a case when one threat gets the number of the tool from the Warehouse
	 * and then the scheduler gives a CPU time to another thread that gets the number of the tool as well. 
	 * And both threads change the value of the product to the same value, while the value should be smaller. 
	 * 
	 * @param type- string describing the required tool
	 * @return a deferred promise for the requested tool
	 */
	public synchronized Deferred<Tool> acquireTool(String type) {
		AtomicInteger newValue=_tools.get(type);
		Deferred<Tool> tool=new Deferred<Tool>();
		if(newValue.intValue()>0){
			newValue.decrementAndGet();
			_tools.replace(type, newValue);
			switch(type){
			case "rs-pliers" : tool.resolve(new RandomSumPliers()); break;
			case "gs-driver" : tool.resolve(new GcdScrewDriver()); break;
			case "np-hammer" : tool.resolve(new NextPrimeHammer()); break;
			}
		}
		else{
			switch(type){
			case "rs-pliers" : _pliersWaitingList.add(tool); break;
			case "gs-driver" : _driverWaitingList.add(tool); break;
			case "np-hammer" : _hammerWaitingList.add(tool); break;
			}
		}
		return tool;
	}

	/**
	 * Tool return procedure - releases a tool which becomes available in the
	 * warehouse upon completion.
	 * 
	 * this method is synchronized because there is a case when one threat gets the number of the tool from the Warehouse
	 * and then the scheduler gives a CPU time to another thread that gets the number of the tool as well. 
	 * And both threads change the value of the product to the same value, while the value should be bigger. 
	 * 
	 * @param tool - The tool to be returned        
	 */
	public synchronized void releaseTool(Tool tool) {
		String type=tool.getType();
		if(type=="rs-pliers"){
			if(!_pliersWaitingList.isEmpty()){
				Deferred<Tool> promiseTool=_pliersWaitingList.remove();
				promiseTool.resolve(tool);
				return;
			}
		}
		else if(type=="gs-driver"){
			if(!_driverWaitingList.isEmpty()){
				Deferred<Tool> promiseTool=_driverWaitingList.remove();
				promiseTool.resolve(tool);
				return;
			}
		}
		else if(type=="np-hammer"){
			if(!_hammerWaitingList.isEmpty()){
				Deferred<Tool> promiseTool=_hammerWaitingList.remove();
				promiseTool.resolve(tool);
				return;
			}
		}
		AtomicInteger newValue=_tools.get(tool.getType());
		newValue.incrementAndGet();
		_tools.replace(tool.getType(), newValue);
	}

	/**
	 * Getter for ManufactoringPlans
	 * @param product- a string with the product name for which a ManufactoringPlan is desired
	 * @return A ManufactoringPlan for product
	 */
	public ManufactoringPlan getPlan(String product) {
		return _plans.get(product);
	}

	/**
	 * Store a ManufactoringPlan in the warehouse for later retrieval
	 * @param plan- a ManufactoringPlan to be stored
	 */
	public void addPlan(ManufactoringPlan plan) {
		_plans.putIfAbsent(plan.getProductName(), plan);
	}

	/**
	 * Store a qty Amount of tools of type tool in the warehouse for later
	 * retrieval
	 * @param tool- type of tool to be stored   
	 * @param qty- amount of tools of type tool to be stored   
	 */
	public void addTool(Tool tool, int qty) {
		_tools.putIfAbsent(tool.getType(), new AtomicInteger(qty));
	}

}
