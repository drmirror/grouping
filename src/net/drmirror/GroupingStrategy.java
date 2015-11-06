package net.drmirror;

public interface GroupingStrategy<Item> {

	/**
	 * Returns the distance between two items.
	 * @param a the first item
	 * @param b the second item
	 * @return a number between 0 (zero distance, items are identical)
	 * and 1 (maximum distance, items have nothing in common)
	 */
	public double distance (Item a, Item b);
	
}
