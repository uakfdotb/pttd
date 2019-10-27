import java.util.*;
import java.io.*;

public class FormatReadme {
	public static void main(String args[]) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("readme.txt"));
		String line;
		
		System.out.println("<html>");
		System.out.println("<head><title>pttd readme</title></head>");
		System.out.println("<body>");
		
		while((line = in.readLine()) != null) {
			if(line.startsWith("--")) {
				//next three lines are part of header
				System.out.println("<h1>" + in.readLine() + "</h1>");
				in.readLine();
				in.readLine();
			} else if(!line.startsWith("#") && !line.trim().isEmpty()) {
				//replace links
				int index1 = -1;
				
				while(true) {
					index1 = line.indexOf('<', index1 + 1);
				
					if(index1 != -1) {
						int oldLength = line.length();
						int index2 = line.indexOf('>', index1);
					
						if(index2 != -1) {
							String text = line.substring(index1 + 1, index2);
						
							if(text.contains("http") || text.contains("com") || text.contains("org") || text.contains("edu") || text.contains("net")) {
								String link = text;
							
								if(!link.startsWith("http://")) {
									link = "http://" + link;
								}
							
								link = "<a href=\"" + link + "\">" + link + "</a>";
								
								line = line.substring(0, index1) + link + line.substring(index2 + 1);
							} else {
								//change to &lt;
								line = line.substring(0, index1) + "&lt;" + line.substring(index1 + 1, index2) + "&gt;" + line.substring(index2 + 1);
							}
						} else {
							//change to &lt;
							line = line.substring(0, index1) + "&lt;" + line.substring(index1 + 1);
						}
						
						//update index1 according to length change
						index1 = index2 + line.length() - oldLength;
					} else {
						break;
					}
				}
				
				//next line should either be blank or --
				String nextLine = in.readLine();
				
				if(nextLine != null && nextLine.startsWith("--")) {
					//subheader
					System.out.println("<h2>" + line + "</h2>");
				} else {
					System.out.println("<p>" + line + "</p>");
				}
			}
		}
		
		in.close();
		
		System.out.println("</body>");
		System.out.println("</html>");
	}
}
