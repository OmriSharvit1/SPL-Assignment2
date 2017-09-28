package bgu.spl.a2.sim.tools;

import bgu.spl.a2.sim.Product;

/**
 * a class that represent a NextPrimeHammer tool.
 **/
public class NextPrimeHammer implements Tool {

	/**
	 * @return the type of the tool
	 */
	@Override
	public String getType() {
		return "np-hammer";
	}
	
	/**
	 * @return the sum of first prime numbers following the product id of each part of the product parts
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
	 * @return the first prime number following the product id 
	 * @param  - the product id to activate the useOn function on
	 */
	public long func(long id) {
		  long v =id + 1;
	        while (!isPrime(v)) {
	            v++;
	        }
	        return v;
	}
	/**
	 * @return true if the value isPrime and false otherwise
	 * @param value - the number to be checked if prime or not
	 */
	private boolean isPrime(long value) {
	      if(value < 2) return false;
	    	if(value == 2) return true;
	        long sq = (long) Math.sqrt(value);
	        for (long i = 2; i <= sq; i++) {
	            if (value % i == 0) {
	                return false;
	            }
	        }
	        return true;
	}

}
