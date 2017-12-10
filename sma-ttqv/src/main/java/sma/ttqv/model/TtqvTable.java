package sma.ttqv.model;

public abstract class TtqvTable {

	protected String name;

	public TtqvTable(String pName) {
		super();
		name = pName;
	}

	public String getName() {
		return name;
	}

	public void setName(String pName) {
		name = pName;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " [name=" + name + "]";
	}

}
