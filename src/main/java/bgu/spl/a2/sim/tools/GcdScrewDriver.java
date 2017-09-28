package bgu.spl.a2.sim.tools;

import java.math.BigInteger;
import bgu.spl.a2.sim.Product;

/**
 * a class that represent a GcdScrewDriver .
 **/
public class GcdScrewDriver implements Tool {
	
	/**
	 * @return the type of the tool
	 */
	@Override
	public String getType() {
		return("gs-driver");
	}
	
	/**
	 * @return the sum of the greatest common dividers of [product id] and reverse([product id]) of each part of the product
	 * @param p - the product to activate the useOn function on it's parts
	 */
	@Override
	public long useOn(Product p) {
		long value=0;
    	for(Product part : p.getParts()){
    		value+=Math.abs(func(part.getFinalId()));
    		
    	}
      return value;	}
	/**
	 * @return the sum of the greatest common dividers of [product id] and reverse([product id]) 
	 * @param id - the id of the product to calculate 
	 */
	public long func(long id){
    	BigInteger b1 = BigInteger.valueOf(id);
    	BigInteger b2 = BigInteger.valueOf(reverse(id));
    	long value= (b1.gcd(b2)).longValue();
    	return value;
    }
	
	/**
	 * @return the reverse id of a product
	 * @param n - the id to reverse
	 */
	public long reverse(long n) {
		long reverse = 0;
		while (n != 0) {
			reverse = reverse * 10;
			reverse = reverse + n % 10;
			n = n / 10;
		}
		return reverse;
	}	
	

	

	
	

}
