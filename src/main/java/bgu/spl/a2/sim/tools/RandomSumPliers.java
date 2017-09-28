package bgu.spl.a2.sim.tools;

import java.util.Random;

import bgu.spl.a2.sim.Product;

/**
 * a class that represent a RandomSumPliers tool.
 **/
public class RandomSumPliers implements Tool{

	/**
	 * @return the type of the tool
	 */
	@Override
	public String getType() {
		return "rs-pliers";
	}
	
	/**
	 * @return return the sum of [product id % 10000] random integers from the Random object on each of the product part's.
	 * @param p - the product to activate the useOn function on it's parts
	 */
	@Override
	public long useOn(Product p) {
		long value=0;
    	for(Product part : p.getParts()){
    		value+=Math.abs(func(part.getFinalId()));
    		
    	}
      return value;
      }
	/**
	 * @return the sum of [product id % 10000] random integers from the Random object
	 * @param  - the product id to activate the useOn function on
	 */
    public long func(long id){
    	Random r = new Random(id);
        long  sum = 0;
        for (long i = 0; i < id % 10000; i++) {
            sum += r.nextInt();
        }

        return sum;
    }

}
