/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bgu.spl.a2.sim;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectOutputStream;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import bgu.spl.a2.WorkStealingThreadPool;
import bgu.spl.a2.sim.conf.ManufactoringPlan;
import bgu.spl.a2.sim.tasks.TaskReservation;
import bgu.spl.a2.sim.tools.GcdScrewDriver;
import bgu.spl.a2.sim.tools.NextPrimeHammer;
import bgu.spl.a2.sim.tools.RandomSumPliers;

/**
 * A class describing the simulator for part 2 of the assignment
 */
public class Simulator {
	private static Warehouse _warehouse = new Warehouse();
	private static WorkStealingThreadPool _work;
	private static TaskReservation[][] _waves;

	/**
	 * Begin the simulation Should not be called before
	 * attachWorkStealingThreadPool()
	 */
	public static ConcurrentLinkedQueue<Product> start() {
		ConcurrentLinkedQueue<Product> ProductQueue = new ConcurrentLinkedQueue<Product>();
		for (int i = 0; i < _waves.length; i++) {
			CountDownLatch counter = new CountDownLatch(_waves[i].length);
			for (int j = 0; j < _waves[i].length; j++) {
				_work.submit(_waves[i][j]);
				int k = i;
				int t = j;
				_waves[i][j].getResult().whenResolved(() -> {
					ProductQueue.addAll(_waves[k][t].getResult().get());
					counter.countDown();
				});
			}
			try {
				counter.await();
			} catch (InterruptedException e) {
			}
		}
		return ProductQueue;
	}

	/**
	 * attach a WorkStealingThreadPool to the Simulator, this
	 * WorkStealingThreadPool will be used to run the simulation
	 * 
	 * @param myWorkStealingThreadPool
	 *            - the WorkStealingThreadPool which will be used by the
	 *            simulator
	 */
	public static void attachWorkStealingThreadPool(WorkStealingThreadPool myWorkStealingThreadPool) {
		_work = myWorkStealingThreadPool;
	}

	
	/**
	 * The main function.
	 * read a json file and convert it to SerReader.
	 * Performs all the tasks in the json file and Initialize the products. 
	 * Start the pool.
	 * Sorted the final products queue.
	 * shut down the pool at the end of the manufactoring.
	 * 
	 * @param args- json file that contains  all the products to produce in the manufactoring. 
	 */
	public static int main(String[] args) {
		JsonParser parser = new JsonParser();
		try (FileReader reader = new FileReader(args[0])) {
			Object o = parser.parse(reader);
			JsonObject JsonObject = (JsonObject) o;
			int numberOfThreads = JsonObject.get("threads").getAsInt();
			attachWorkStealingThreadPool(new WorkStealingThreadPool(numberOfThreads));
			JsonArray tools = JsonObject.get("tools").getAsJsonArray();
			JsonArray plans = JsonObject.get("plans").getAsJsonArray();
			JsonArray waves = JsonObject.get("waves").getAsJsonArray();
			Initalplans(plans);
			InitalTools(tools);
			initialWaves(waves);
			_work.start();			
			ConcurrentLinkedQueue<Product> finishProductList = start();	
			int size = finishProductList.size();
			ConcurrentLinkedQueue<Product> sortedProductList = sortQueue(finishProductList,size);
			FileOutputStream fout = new FileOutputStream("result.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(sortedProductList);
			oos.close();		
		
			
			_work.shutdown();
		} catch (Exception e) {
		}
		 return 0;
	}
	/**
	 * Sort the finishProductList by the index field of each product using a helper array
	 * @param  finishProductList - ConcurrentLinkedQueue<Product> to be sorted
	 * 		    size - the size of the list	
	 * @return the sorted ConcurrentLinkedQueue<Product>.
	 */
	private static ConcurrentLinkedQueue<Product> sortQueue(ConcurrentLinkedQueue<Product> finishProductList,int size ){
		Product[] sortedArray = new Product[size];
		for (int i = 0; i < size; i++) {
			Product p = finishProductList.poll();
			sortedArray[p.getIndex().get()] = p;
		}
		ConcurrentLinkedQueue<Product> sortedProductList = new ConcurrentLinkedQueue<Product>();
		for (int j = 0; j < size; j++) {
			sortedProductList.add(sortedArray[j]);
		}
		return sortedProductList;
	}
	
	/**
	 * Converting a JsonArray to String[]
	 * @param arr - the JsonArray to convert
	 * @return a String[] stringArr 
	 */
	private static String[] convertToStringArray(JsonArray arr) {
		String[] stringArr = new String[arr.size()];
		for (int i = 0; i < arr.size(); i++)
			stringArr[i] = arr.get(i).getAsString();
		return stringArr;
	}
	
	/**
	 * Initialize the plans that was read from the Json input file
	 * @param arr - the JsonArray of plans which we read
	 */
	private static void Initalplans(JsonArray plans) {
		for (int i = 0; i < plans.size(); i++) {
			JsonObject currentJsonPlan = plans.get(i).getAsJsonObject();
			String currentPlanProductName = currentJsonPlan.get("product").getAsString();
			String[] currentPlanToolsNames = convertToStringArray(currentJsonPlan.get("tools").getAsJsonArray());
			String[] currentPlanPartsNames = convertToStringArray(currentJsonPlan.get("parts").getAsJsonArray());
			_warehouse.addPlan(
					new ManufactoringPlan(currentPlanProductName, currentPlanPartsNames, currentPlanToolsNames));
		}
	}
	
	/**
	 * Initialize the tools of the warehouse that was read from the Json input file
	 * @param arr - the JsonArray of tools which we read
	 */
	private static void InitalTools(JsonArray tools) {
		for (Object o : tools) {
			JsonObject obj = (JsonObject) o;
			String tool = obj.get("tool").getAsString();
			int qty = obj.get("qty").getAsInt();
			switch (tool) {
			case "gs-driver":
				_warehouse.addTool(new GcdScrewDriver(), qty);
			case "np-hammer":
				_warehouse.addTool(new NextPrimeHammer(), qty);
			case "rs-pliers":
				_warehouse.addTool(new RandomSumPliers(), qty);
			}
		}
	}
	
	/**
	 * Initialize the waves[][] array of the waves that was read from the Json input file
	 * @param arr - the JsonArray of waves which we read
	 */
	private static void initialWaves(JsonArray waves) {
		_waves = new TaskReservation[waves.size()][];
		AtomicInteger index = new AtomicInteger();
		for (int i = 0; i < waves.size(); i++) {
			JsonArray arr = waves.get(i).getAsJsonArray();
			TaskReservation[] wave = new TaskReservation[arr.size()];
			for (int j = 0; j < arr.size(); j++) {
				JsonObject in = arr.get(j).getAsJsonObject();
				String product = in.get("product").getAsString();
				int qty = in.get("qty").getAsInt();
				long startId = in.get("startId").getAsLong();
				TaskReservation taskReservation = new TaskReservation(product, qty, startId, _warehouse, index);
				index.set(index.get() + qty);
				wave[j] = taskReservation;
			}
			_waves[i] = wave;
		}
	}
}
