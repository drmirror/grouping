package net.drmirror.test;

import net.drmirror.GroupingStrategy;

public class TestGroupingStrategy implements GroupingStrategy<TestItem> {

	/**
	 * A simple example distance function that counts the number of
	 * equal attributes in both items and normalizes the count to
	 * a number between 0 and 1.
	 */
	public double distance (TestItem a, TestItem b) {
		int matchCount = 0;
		if (a.firstName.equals(b.firstName)) matchCount++;
		if (a.lastName.equals(b.lastName)) matchCount++;
		if (a.email.equals(b.email)) matchCount++;
		if (a.phone.equals(b.phone)) matchCount++;
		if (a.country.equals(b.country)) matchCount++;
		return 1.0 - (double)matchCount / 5.0;
	}
}
