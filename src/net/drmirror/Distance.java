package net.drmirror;

import java.util.List;

import org.bson.Document;

public class Distance {

	public static class Name {
		public String nombre0 = "", nombre1 = "", apellido0 = "", apellido1 = "";
		public Name(Document d) {
			Document maestro = (Document)d.get("maestro");
			if (maestro == null) return;
			String nombre = maestro.getString("nombre");
			List<String> apellidos = (List<String>)maestro.get("apellidos");
			if (nombre == null || nombre.equals("")) {
				nombre0 = ""; nombre1 = "";
			} else {
				int indexOfFirstBlank = nombre.indexOf(" ");
				if (indexOfFirstBlank == -1) {
					nombre0 = nombre; nombre1 = "";
				} else {
					nombre0 = nombre.substring(0,indexOfFirstBlank);
					nombre1 = nombre.substring(indexOfFirstBlank+1);
				}
			}
			if (apellidos == null || apellidos.size() == 0) {
				apellido0 = ""; apellido1 = "";
			} else if (apellidos.size() == 1) {
				apellido0 = apellidos.get(0); apellido1 = "";
				if (apellido0 == null) apellido0 = "";
			} else {
				apellido0 = apellidos.get(0); apellido1 = apellidos.get(1);
				if (apellido0 == null) apellido0 = "";
				if (apellido1 == null) apellido1 = "";
			}
		}
		public Name(String nombre0, String nombre1, String apellido0, String apellido1) {
			this.nombre0 = nombre0;
			this.nombre1 = nombre1;
			this.apellido0 = apellido0;
			this.apellido1 = apellido1;
		}
	}
	
	public static double nameDistance (Document a, Document b) {
		return nameDistance (new Name(a), new Name(b));
	}
	
	public static double nameDistance (Name a, Name b) {
		double nombre0_distance = componentDistance(a.nombre0, b.nombre0, 0.5);
		double nombre1_distance = componentDistance(a.nombre1, b.nombre1, 0.1);
		double apellido0_distance = componentDistance(a.apellido0, b.apellido0, 0.5);
		double apellido1_distance = componentDistance(a.apellido1, b.apellido1, 0.1);
		return 0.3 * nombre0_distance
		    +  0.2 * nombre1_distance
		    +  0.3 * apellido0_distance
		    +  0.2 * apellido1_distance;
	}
	
	private static double componentDistance(String a, String b, double missingDistance) {
		if (a.equals("") && b.equals("")) {
			return 0.0;
		} else if (a.equals("") || b.equals("")) {
			return missingDistance;
		} else {
			return normalizedLevenshtein(a, b);
		}
	}
	
	/**
	 * From {@link https://en.wikibooks.org/wiki/Algorithm_Implementation/Strings/Levenshtein_distance#Java}
	 * @param lhs
	 * @param rhs
	 * @return
	 */
	public static int rawLevenshtein (CharSequence lhs, CharSequence rhs) {                          
	    int len0 = lhs.length() + 1;                                                     
	    int len1 = rhs.length() + 1;                                                     
	                                                                                    
	    // the array of distances                                                       
	    int[] cost = new int[len0];                                                     
	    int[] newcost = new int[len0];                                                  
	                                                                                    
	    // initial cost of skipping prefix in String s0                                 
	    for (int i = 0; i < len0; i++) cost[i] = i;                                     
	                                                                                    
	    // dynamically computing the array of distances                                  
	                                                                                    
	    // transformation cost for each letter in s1                                    
	    for (int j = 1; j < len1; j++) {                                                
	        // initial cost of skipping prefix in String s1                             
	        newcost[0] = j;                                                             
	                                                                                    
	        // transformation cost for each letter in s0                                
	        for(int i = 1; i < len0; i++) {                                             
	            // matching current letters in both strings                             
	            int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;             
	                                                                                    
	            // computing cost for each transformation                               
	            int cost_replace = cost[i - 1] + match;                                 
	            int cost_insert  = cost[i] + 1;                                         
	            int cost_delete  = newcost[i - 1] + 1;                                  
	                                                                                    
	            // keep minimum cost                                                    
	            newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
	        }                                                                           
	                                                                                    
	        // swap cost/newcost arrays                                                 
	        int[] swap = cost; cost = newcost; newcost = swap;                          
	    }                                                                               
	                                                                                    
	    // the distance is the cost for transforming all letters in both strings        
	    return cost[len0 - 1];                                                          
	}
	
	public static double normalizedLevenshtein (CharSequence lhs, CharSequence rhs) {
		int maxLength = Math.max (lhs.length(), rhs.length());
		return (double)rawLevenshtein(lhs,rhs) / (double)maxLength;
		
	}
	
	public static void main(String[] args) {
		System.out.println(nameDistance(new Name("ANA", "CHRISTINA", "DOMINGUEZ", "GUTIERREZ"),
				                        new Name("ANA", "CHRISTINA", "DOMINGUES", "GUTIERREZ")));
	}
}
