package net.drmirror.test;

import java.util.ArrayList;
import java.util.List;

import net.drmirror.*;

/**
 * Groups a set of random integers from a given RANGE, where the distance
 * function is the arithmetic distance between integers.  For example,
 * if RANGE is 1000, then a merge threshold of 0.002 means that numbers
 * with a distance of two or less should be grouped.
 * 
 * @author drmirror
 */
public class NumberMatrix {
	
	public static int SIZE = 2000;
	public static double RANGE = 100000.0;
	
	public static void main(String[] args) {
		
		List<Integer> items = new ArrayList<Integer>();
		for (int i=0; i<SIZE; i++) {
			items.add((int)Math.floor(Math.random()*RANGE));
		}
		GroupingMatrix<Integer> matrix = new GroupingMatrix<Integer>(
				items,
				new GroupingStrategy<Integer>() {
					public double distance(Integer a, Integer b) {
						return (double)Math.abs(a-b) / (double)RANGE;
					}
				}
		);
		System.out.println("size before merge: " + matrix.size());
		matrix.merge(0.0002);
		System.out.println("size after merge:  " + matrix.size());
		for (int i=0; i<matrix.size(); i++) {
			System.out.printf("%d %s\n", i, matrix.getGroupItems(i));
		}
	}
}
