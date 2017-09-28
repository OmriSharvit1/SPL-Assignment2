package bgu.spl.a2.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A class that represents a product produced during the simulation.
 */
public class Product implements java.io.Serializable {
	private final long _startId;
	private final String _name;
	private List<Product> _productParts;
	private long _finalId;
	private AtomicInteger _index;

	/**
	 * Constructor
	 * 
	 * @param startId-
	 *            Product start id
	 * @param name-
	 *            Product name
	 */
	public Product(long startId, String name) {
		_name = name;
		_startId = startId;
		_productParts = new ArrayList<Product>();
		_finalId = _startId;
		_index = new AtomicInteger();
	}

	/**
	 * @return The product name as a string
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @return The product start ID as a long. start ID should never be changed.
	 */
	public long getStartId() {
		return _startId;
	}

	/**
	 * @return The product final ID as a long. final ID is the ID the product
	 *         received as the sum of all UseOn();
	 */
	public long getFinalId() {
		return _finalId;
	}

	/**
	 * sets the index internal field
	 * 
	 * @param the
	 *            index to be set.
	 */
	public void SetIndex(int index) {
		_index.set(index);
	}

	/**
	 * @return The index of this product
	 */
	public AtomicInteger getIndex() {
		return _index;
	}

	/**
	 * @return Returns all parts of this product as a List of Products
	 */
	public List<Product> getParts() {
		return _productParts;
	}

	/**
	 * Add a new part to the product
	 * 
	 * @param p
	 *            - part to be added as a Product object
	 */
	public void addPart(Product p) {
		_productParts.add(p);
	}

	/**
	 * set the final id
	 * 
	 * @param p
	 *            - finalId to be summed with the currentId
	 */
	public void setFinalId(long finalId) {
		this._finalId = _finalId + finalId;
	}


}
