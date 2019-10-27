import java.io.*;
import java.util.*;

public class FormatUS { //format unit sequence
	public static void main(String args[]) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("data_" + args[0] + "/units.txt"));
		String line;
		
		//print results to standard output
		System.out.println("<html>");
		System.out.println("<body>");
		System.out.println("<p>The unit/wave sequence is shown below. Quarks is how many quarks you get for killing the unit. Dark units are vulnerable to noble gas towers but are immune to abilities like freeze and reflect; aquatic units take double damage from acidic towers. Units come from two sides, so total number of units is actually two times the number shown.</p>");
		
		int waveCounter = 0;
		
		while((line = in.readLine()) != null) {
			waveCounter++;
			System.out.println("<h3>Wave " + waveCounter + "</h3>");
			
			String[] parts = line.split(" ");
			int num = Integer.parseInt(parts[0]);
			int type = Integer.parseInt(parts[1]);
			int speed = Integer.parseInt(parts[2]);
			int health = Integer.parseInt(parts[3]);
			int quarks = Integer.parseInt(parts[4]);
			
			System.out.println("<table>");
			
			Cell[] cells = new Cell[5];
			for(int i = 0; i < cells.length; i++) cells[i] = new Cell();
			
			cells[0].a = "Number of units";
			cells[0].b = num + "";
			
			cells[1].a = "Unit type";
			cells[1].b = "Unknown";
			
			if(type == 0) cells[1].b = "Normal";
			else if(type == 1) cells[1].b = "Aquatic";
			else if(type == 2) cells[1].b = "Small";
			else if(type == 5) cells[1].b = "Dark";
			
			cells[2].a = "Speed";
			cells[2].b = speed + "";
			
			cells[3].a = "Health";
			cells[3].b = health + "";
			
			cells[4].a = "Quarks";
			cells[4].b = quarks + "";
			
			for(Cell cell : cells) {
				System.out.println("<tr>");
				System.out.println("<td>");
				System.out.println(cell.a);
				System.out.println("</td>");
				System.out.println("<td>");
				System.out.println(cell.b);
				System.out.println("</td>");
				System.out.println("<tr>");
			}
			
			System.out.println("</table>");
		}
		
		System.out.println("</body>");
		System.out.println("</html>");
	}
}
