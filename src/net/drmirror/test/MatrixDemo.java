package net.drmirror.test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import net.drmirror.GroupingMatrix;

/**
 * A demo of the GroupingMatrix which uses a set of eight sample items and
 * shows how the distance matrix evolves after each merge operation.
 * 
 * @author drmirror
 */
public class MatrixDemo {

	public static String groupToString(List<TestItem> group) {
		if (group.size() == 1) {
			return group.get(0).toString();
		} else {
			StringWriter result = new StringWriter();
			for(int i=0; i<group.size()-1; i++) {
				char c = group.get(i).toString().charAt(0);
				result.append(c + "/");
			}
			char c = group.get(group.size()-1).toString().charAt(0);
			result.append(c);
			return result.getBuffer().toString();
		}
	}
	
	public static String matrixToString(GroupingMatrix<TestItem> matrix) {
		if (matrix.size() == 1) {
			return groupToString(matrix.getGroupItems(0)) + "  ./.";
		}
		StringWriter result = new StringWriter();
		PrintWriter out = new PrintWriter (result);
		out.print("          ");
		for (int j=0; j<matrix.size()-1; j++) {
			out.printf("%11s", groupToString(matrix.getGroupItems(j)));
		}
		out.println();
		for (int i=1; i<matrix.size(); i++) {
			out.printf("%-9s ", groupToString(matrix.getGroupItems(i)));
			for (int j=0; j<i; j++) {
				out.printf("      %1.3f", matrix.distance(i, j));
			}
			out.println();
		}
		return result.getBuffer().toString();
	}
	
	public static void main(String[] args) {
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
		System.out.println(matrixToString(matrix));
		
		while(matrix.mergeOnce(1.0)) { System.out.println(matrixToString(matrix)); }
		
	}
	
}
