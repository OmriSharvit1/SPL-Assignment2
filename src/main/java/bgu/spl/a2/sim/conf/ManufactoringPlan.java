package bgu.spl.a2.sim.conf;

/**
 * a class that represents a manufacturing plan.
 *
 **/
public class ManufactoringPlan {
	private String _product;
	private String[] _parts;
	private String[] _tools;

	/**
	 * ManufactoringPlan constructor
	 * @param product - product name
	 * @param parts- array of strings describing the plans part names
	 * @param tools- array of strings describing the plans tools names
	 */
	public ManufactoringPlan(String product, String[] parts, String[] tools) {
		this._product = product;
		_parts = new String[parts.length];
		_tools = new String[tools.length];
		for (int i = 0; i < parts.length; i++) {
			_parts[i] = parts[i];
		}
		for (int i = 0; i < tools.length; i++) {
			_tools[i] = tools[i];
		}
	}

	/**
	 * @return array of strings describing the plans part names
	 */
	public String[] getParts() {
		return _parts;
	}

	/**
	 * @return string containing product name
	 */
	public String getProductName() {
		return _product;
	}

	/**
	 * @return array of strings describing the plans tools names
	 */
	public String[] getTools() {
		return _tools;
	}

}
