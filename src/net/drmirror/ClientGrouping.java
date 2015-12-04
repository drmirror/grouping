package net.drmirror;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class ClientGrouping {

	private static MongoClient client = null;
	
	private static MultiMap<String,Document> buckets = new MultiMap<String,Document>();
	
	private static GroupingStrategy<Document> strategy = new GroupingStrategy<Document>() {
		
		public double distance (Document a, Document b) {
			return Distance.nameDistance(a, b);
//			Document maestro_a = (Document)a.get("maestro");
//			Document maestro_b = (Document)b.get("maestro");
//			String nombre_a = maestro_a.getString("nombre");
//			String nombre_b = maestro_b.getString("nombre");
//			List<Object> apellidos_a = (List<Object>)maestro_a.get("apellidos");
//			List<Object> apellidos_b = (List<Object>)maestro_b.get("apellidos");
//			String apellido_0_a = (String)apellidos_a.get(0);
//			String apellido_1_a = apellidos_a.size() == 2 ? (String)apellidos_a.get(1) : null;
//			String apellido_0_b = (String)apellidos_b.get(0);
//			String apellido_1_b = apellidos_b.size() == 2 ? (String)apellidos_b.get(1) : null;
//			int count = 0;
//			if (   (nombre_a == null && nombre_b == null)
//				|| nombre_a.equals(nombre_b)) count++;
//			if (   (apellido_0_a == null && apellido_0_b == null)
//			    || (apellido_0_a != null && apellido_0_a.equals(apellido_0_b))) count++;
//			if (   (apellido_1_a == null && apellido_1_b == null)
//				|| (apellido_1_a != null && apellido_1_a.equals(apellido_1_b))) count++;
//			return 1.0 - (double)count/3.0;
		}
		
	};
	
	public static String asString(Document d) {
		StringWriter result = new StringWriter();
		PrintWriter out = new PrintWriter (result);
		Document maestro = (Document)d.get("maestro");
		out.print(maestro.getString("fecha_nacimiento") + " ");
		out.print(maestro.getString("nombre") + " ");
		List<String> apellidos = (List<String>)maestro.get("apellidos");
		out.print(apellidos.get(0) + " ");
		if (apellidos.size() == 2) out.print(apellidos.get(1));
		out.print("  ");
		List<Document> fuentes = (List<Document>)d.get("fuentes");
		for (Document f : fuentes) {
			out.print(f.getString("_id") + " ");
		}
		return result.toString();
	}
	
	public static double intraClusterDistance (List<Document> cluster) {
		double maxDistance = 0.0;
		for (Document d : cluster) {
			for (Document e : cluster) {
				double distance = Distance.nameDistance(d, e);
				if (distance > maxDistance) maxDistance = distance;
			}
		}
		return maxDistance;
	}
	
	public static void printCluster (double distance, List<Document> cluster) {
		System.out.printf("%.3f %s\n", distance, asString(cluster.get(0)));
		for (int i=1; i<cluster.size(); i++) {
			System.out.printf("      %s\n", asString(cluster.get(i)));
		}
		System.out.println();
	}
	
	public static void main (String[] args) {
		client = new MongoClient("localhost:27017");
		MongoDatabase db = client.getDatabase("cliente360");
		MongoCollection<Document> personas = db.getCollection("personas");
		
		Document query = new Document (
		    "maestro.fecha_nacimiento", new Document("$gte", "1990").append("$lt", "2000")
		);
		int i=0;
		for (Document d : personas.find(query)) {
			i++;
			Document maestro = (Document)d.get("maestro");
			String dob = maestro.getString("fecha_nacimiento");
			buckets.put(dob, d);
			if (i % 100000 == 0) {
				System.out.printf("documents: %d buckets: %d avg: %.1f\n", i, buckets.size(), (double)i / (double)buckets.size());
			}
		}
		System.out.printf("documents: %d buckets: %d avg: %.1f\n", i, buckets.size(), (double)i / (double)buckets.size());

		Map<Integer,Integer> clusterSizes = new HashMap<Integer,Integer>();
		
		for (String key : buckets.keySet()) {
			List<Document> clients = buckets.getAll(key);
			GroupingMatrix<Document> matrix = new GroupingMatrix<Document>(clients,strategy);
			matrix.merge(0.075);
			List<Integer> clusters = matrix.getClusters(2);
			for (int c : clusters) {
				int size = matrix.getGroup(c).size();
				Integer numSize = clusterSizes.get(size);
				if (numSize == null) numSize = 0; 
				clusterSizes.put(size, numSize+1);
				List<Document> cluster = matrix.getGroupItems(c);
				double intraClusterDistance = intraClusterDistance(cluster);
				if (intraClusterDistance >= 0.07)
					printCluster(intraClusterDistance, cluster);
			}
		}
		
		SortedSet<Integer> sizes = new TreeSet<Integer>(clusterSizes.keySet());
		for (int size : sizes) {
			System.out.printf("%d: %d ", size, clusterSizes.get(size));
		}
		System.out.println();
	}
	
}
