package net.drmirror.test;

import java.util.ArrayList;
import java.util.List;

import net.drmirror.GroupingMatrix;
import junit.framework.TestCase;

public class MatrixTest extends TestCase {

	public void testDuckburg() {
		List<TestItem> items = new ArrayList<TestItem>();
		items.add(new TestItem("Donald", "Duck", "donald@duck.com", "1234", "US"));
		items.add(new TestItem("Daisy", "Duck", "daisy@duck.com", "1234", "US"));
		items.add(new TestItem("Scrooge", "Duck", "scrooge@duck.com", "7890", "US"));
		items.add(new TestItem("Mickey", "Mouse", "mickey@mouse.com", "5678", "US"));
		items.add(new TestItem("Minnie", "Mouse", "minnie@mouse.com", "6789", "US"));
		items.add(new TestItem("Huey", "Duck", "kids@duck.com", "1234", "US"));
		items.add(new TestItem("Louie", "Duck", "kids@duck.com", "1234", "US"));
		items.add(new TestItem("Dewey", "Duck", "kids@duck.com", "1234", "US"));
		GroupingMatrix<TestItem> matrix = new GroupingMatrix<TestItem> (
	        items, new TestGroupingStrategy()
	    );
		assertEquals(8, matrix.size());
		boolean result = matrix.mergeOnce(0.3);
		assertTrue(result);
		assertEquals(7, matrix.size());
		assertEquals(2, matrix.getGroup(6).size());
		assertEquals("Huey", matrix.getGroupItems(6).get(0).firstName);
		assertEquals("Louie", matrix.getGroupItems(6).get(1).firstName);
		
		result = matrix.mergeOnce(0.3);
		assertTrue(result);
		assertEquals(6, matrix.size());
		assertEquals(3, matrix.getGroup(5).size());
		assertEquals("Dewey", matrix.getGroupItems(5).get(0).firstName);
		assertEquals("Huey", matrix.getGroupItems(5).get(1).firstName);
		assertEquals("Louie", matrix.getGroupItems(5).get(2).firstName);
		
		result = matrix.mergeOnce(0.3);
		assertFalse(result);
		assertEquals(6, matrix.size());
		
		assertTrue(matrix.mergeOnce(1.0));
		assertEquals(5, matrix.size());

		assertTrue(matrix.mergeOnce(1.0));
		assertEquals(4, matrix.size());

		assertTrue(matrix.mergeOnce(1.0));
		assertEquals(3, matrix.size());

		assertTrue(matrix.mergeOnce(1.0));
		assertEquals(2, matrix.size());

		assertTrue(matrix.mergeOnce(1.0));
		assertEquals(1, matrix.size());
		assertEquals(8, matrix.getGroup(0).size());
    }

	public void testEmpty() {
		List<TestItem> items = new ArrayList<TestItem>();
		GroupingMatrix<TestItem> matrix = new GroupingMatrix<TestItem> (
	        items, new TestGroupingStrategy()
	    );
		assertEquals(0, matrix.size());
		assertFalse(matrix.mergeOnce(1.0));
		assertEquals(0, matrix.size());
	}

	public void testSizeOne() {
		List<TestItem> items = new ArrayList<TestItem>();
		items.add(new TestItem("Donald", "Duck", "donald@duck.com", "1234", "US"));
		GroupingMatrix<TestItem> matrix = new GroupingMatrix<TestItem> (
	        items, new TestGroupingStrategy()
	    );
		assertEquals(1, matrix.size());
		matrix.merge(1.0);
		assertEquals(1, matrix.size());
		assertEquals("Donald", matrix.getGroupItems(0).get(0).firstName);
	}

	public void testSizeTwo() {
		List<TestItem> items = new ArrayList<TestItem>();
		items.add(new TestItem("Donald", "Duck", "donald@duck.com", "1234", "US"));
		items.add(new TestItem("Daisy", "Duck", "daisy@duck.com", "1234", "US"));
		GroupingMatrix<TestItem> matrix = new GroupingMatrix<TestItem> (
	        items, new TestGroupingStrategy()
	    );
		assertEquals(2, matrix.size());
		matrix.merge(0.8);
		assertEquals(1, matrix.size());
		assertEquals("Donald", matrix.getGroupItems(0).get(0).firstName);
		assertEquals("Daisy", matrix.getGroupItems(0).get(1).firstName);
	}

}
