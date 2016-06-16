package net.drmirror;

import java.util.ArrayList;
import java.util.List;

/**
 * Identifies clusters (groups) in a set of items, according to a given distance
 * metric between the items, and a threshold which items should be considered
 * "close" to each other.
 * 
 * Items can be any data type, as long as there is a distance function between them,
 * defined in an implementation of the interface GroupingStrategy.
 *
 * Usage: Instantiate the class by passing in the list of items and a distance function.
 * You can then perform the grouping my calling mergeOnce(threshold) until it returns false,
 * or call merge(threshold) to perform all groupings until there are no more items with a
 * distance less than the threshold. You can then retrieve the individual groups using
 * getGroup() or getGroupItems().
 * 
 * Note: This implementation uses the naive algorithm as found in Wikipedia which has cubic
 * complexity. A future version should use the optimized quadratic algorithm referred to in that
 * article. (en.wikipedia.org/wiki/Single-linkage_clustering)
 * 
 * @author andre.spiegel@mongodb.com
 */
public class GroupingMatrix<Item> {

	private GroupingStrategy<Item> strategy = null;

	private List<Item> items = null;
	private List<List<Integer>> groups = null;
	private List<List<Double>>  matrix = null;
	
	public GroupingMatrix (List<Item> items, GroupingStrategy<Item> strategy) {	
		this.strategy = strategy;
		this.items = items;
		initializeGroups();
		initializeMatrix();
	}

	private void initializeGroups() {
		groups = new ArrayList<List<Integer>>(items.size());
		for (int i=0; i<items.size(); i++) {
			List<Integer> group = new ArrayList<Integer>();
			group.add(i);
			groups.add(group);
		}
	}
	
	private void initializeMatrix() {
		matrix = new ArrayList<List<Double>>(groups.size());
		for (int i=0; i<groups.size(); i++) {
			List<Double> row = new ArrayList<Double>(i+1);
			matrix.add(row);
			for (int j=0; j<=i; j++) {
				row.add(strategy.distance(items.get(i), items.get(j)));
			}
		}
	}
	
	private class Distance {
		public int a, b;
		public double distance;
		public Distance(int a, int b, double distance) {
			this.a = a;
			this.b = b;
			this.distance = distance;
		}
	}
	
	private Distance minDistance() {
		Distance result = new Distance(-1, -1, 2);
		for (int i=1; i<matrix.size(); i++) {
			List<Double> row = matrix.get(i);
			for (int j=0; j<i; j++) {
				if (row.get(j) < result.distance) {
					result.a = i;
					result.b = j;
					result.distance = row.get(j);
				}
			}
		}
		return result;
	}
	
	private void mergeGroups(int a, int b) {
		// make sure a is less than b
		if (b < a) {
			int c = b;
			b = a;
			a = c;
		}
		// add new group at highest index
		List<Integer> newGroup = new ArrayList<Integer>(
		  groups.get(a).size() + groups.get(b).size()
		);
		newGroup.addAll(groups.get(a));
		newGroup.addAll(groups.get(b));
		groups.add(newGroup);
		// compute distances to all existing groups
		List<Double> row = new ArrayList<Double>(groups.size());
		for (int i=0; i<groups.size()-1; i++) {
			double distA = distance(i,a);
			double distB = distance(i,b);
			row.add(Math.min(distA, distB));
		}
		row.add(0.0);
		matrix.add(row);
		// remove old groups, rows and columns
		groups.remove(b);
		groups.remove(a);
		matrix.remove(b);
		matrix.remove(a);
		for (int i=0; i<matrix.size(); i++) {
			List<Double> rowI = matrix.get(i);
			if (rowI.size() > b) rowI.remove(b);
			if (rowI.size() > a) rowI.remove(a);
		}
	}
	
	/**
	 * Returns the distance between two groups, identified by their index in the group list.
	 * @param a index of the first group in the list, must be in the range 0..matrix.size()-1
	 * @param b index of the second group in the list, must be in the range 0..matrix.size()-1
	 * @return a number between 0 (zero distance) and 1 (maximum distance)
	 */
	public double distance(int a, int b) {
		if (a == b) return 0;
		if (a < b) return matrix.get(b).get(a);
		return matrix.get(a).get(b);
	}

	/**
	 * Returns the group with the given index, as a list of item indexes (the position
	 * of each item in the item list)
	 * @param i index of the group
	 * @return a list of the indexes of items in that group
	 */
	public List<Integer> getGroup (int i) {
		return groups.get(i);
	}
	
	/**
	 * Returns the list of items that are in the group with the given index. 
	 * @param i index of the group
	 * @return the list of items in that group
	 */
	public List<Item> getGroupItems (int i) {
		List<Item> result = new ArrayList<Item>();
		for (int x : groups.get(i)) {
			result.add(items.get(x));
		}
		return result;
	}
	
	public List<Integer> getGroupsWithMinimumSize (int size_threshold) {
		List<Integer> result = new ArrayList<Integer>();
		for (int i=0; i<groups.size(); i++) {
			List<Integer> group = groups.get(i);
			if (group.size() >= size_threshold) result.add(i);
		}
		return result;
	}
	
	/**
	 * Returns the maximum distance between any two elements of the group i.
	 * @param i index of the group
	 * @return the distance
	 */
	public double intraGroupDistance (int i) {
	    double maxDistance = 0.0;
	    List<Item> items = getGroupItems(i);
	    for (Item a : items) {
	        for (Item b : items) {
	            double distance = strategy.distance(a, b);
	            if (distance > maxDistance) maxDistance = distance;
	        }
	    }
	    return maxDistance;
	}
	
	/**
	 * Returns the current size of the matrix, which is the
	 * total number of groups.
	 */
	public int size() {
		return matrix.size();
	}

	/**
	 * Finds the two groups with the smallest distance and merges them into one,
	 * updating the rest of the matrix to reflect the new distances to the merged group.
	 * If there are no two groups with a distance less than threshold, no merge is performed.
	 * @param threshold a number between 0 (zero distance) and 1 (maximum distance)
	 * @return true if a merge was performed, false otherwise
	 */
	public boolean mergeOnce(double threshold) {
		Distance minDistance = minDistance();
		if (minDistance.distance < threshold) {
			mergeGroups(minDistance.a, minDistance.b);
			return true;
		}
		return false;
	}
	
	/**
	 * For the given threshold, merges items into groups until all items with a distance
	 * less than the given threshold have been grouped.
	 * @param threshold a number between 0 (zero distance) and 1 (maximum distance)
	 */
	public void merge(double threshold) {
	   while (mergeOnce(threshold));	
	}
	
}
