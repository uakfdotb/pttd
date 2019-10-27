import java.io.*;
import java.util.*;

public class Format {
	public static void main(String args[]) throws IOException {
		BufferedReader in = new BufferedReader(new FileReader("data_" + args[0] + "/towers.txt"));
		Map<String, Tower> towerMap = new HashMap<String, Tower>();
		List<Tower> towers = new ArrayList<Tower>();
		Map<String, Integer> limits = new HashMap<String, Integer>(); //stores limits
		Tower previousTower = null;
		
		String line = in.readLine();
		String[] parts = line.split(" ");
		Tower baseTower = new Tower();
		baseTower.name = "base";
		baseTower.damageType = "N/A";
		baseTower.damage = 0;
		baseTower.damagePeriod = 0;
		baseTower.range = 0;
		baseTower.upgrades = new ArrayList<Tower>();
		
		for(int i = 0; i < parts.length; i++) {
			TowerCost towerCost = parseTowerCost(parts[i]);
			
			//add tower if it doesn't exist
			if(!towerMap.containsKey(towerCost.towerName)) {
				Tower uTower = new Tower();
				uTower.name = towerCost.towerName;
				towerMap.put(uTower.name, uTower);
				towers.add(uTower);
			}
			
			towerMap.get(towerCost.towerName).quarks = towerCost.quarks;
			baseTower.upgrades.add(towerMap.get(towerCost.towerName));
		}
		
		towerMap.put("base", baseTower);
		towers.add(0, baseTower); //make sure base tower goes first
		
		while((line = in.readLine()) != null) {
			parts = line.split(" ");
			
			if(parts.length == 0) continue;
			
			if(parts[0].charAt(0) == ':') {
				if(parts[0].equals(":a")) {
					//ability
					if(parts[1].equals("nuke")) {
						previousTower.damageType = "Area";
						previousTower.damage = 800 * Integer.parseInt(parts[2]);
						previousTower.damagePeriod = 6000;
					} else if(parts[1].equals("fastattack")) {;
						int factor = Integer.parseInt(parts[2]);
						previousTower.ability = "Catalyst (increases nearby towers' attack speed by " + (factor * 10) + "%)";
					} else if(parts[1].equals("slow")) {
						int factor = 100 - (int) Math.floor(Double.parseDouble(parts[2]) * 100);
						previousTower.ability = "Slow (decreases movement speed of nearby units by " + factor + "%)";
					} else if(parts[1].equals("freeze")) {
						int effectPeriod = Integer.parseInt(parts[2]);
						int duration = Integer.parseInt(parts[3]);
						previousTower.ability = "Freeze (stalls one unit every " + effectPeriod + " ms for " + duration + " ms)";
					} else if(parts[1].equals("poison")) {
						int effectPeriod = Integer.parseInt(parts[2]);
						double damage = Double.parseDouble(parts[3]);
						int duration = Integer.parseInt(parts[4]);
						previousTower.ability = "'Poison' (one unit every " + effectPeriod + " ms will take " + damage + " every second for " + duration + " ms)";
					} else if(parts[1].equals("acetic")) {
						previousTower.ability = "Acetic acid (every aquatic unit on the map takes 6 damage per second)";
					} else if(parts[1].equals("unstable")) {
						previousTower.ability = "Unstable (every unit on the map takes 8 damage per second)";
					} else if(parts[1].equals("gold")) {
						int period = Integer.parseInt(parts[2]);
						previousTower.ability = "Quark production (produces one quark every " + period + " ms)";
					} else if(parts[1].equals("reflect")) {
						previousTower.ability = "Reflect (one unit is reflected every second for two seconds)";
					} else if(parts[1].equals("sudden")) {
						double factor = Double.parseDouble(parts[2]);
						previousTower.ability = "Sudden (kills units with less than " + factor + " of their original health remaining)";
					}
				} else if(parts[0].equals(":b")) {
					//bonus
					if(parts[1].equals("dark")) {
						previousTower.bonus = "Damagex10000 for dark units";
					} else if(parts[1].equals("aquatic")) {
						previousTower.bonus = "Damagex2 for aquatic units";
					}
				} else if(parts[0].equals(":w")) {
					//win
					previousTower.ability = "If you get this tower, you win the game!";
				} else if(parts[0].equals(":m")) {
					//modifier
					String type = "unknown";
					
					if(parts[1].equals("ut")) {
						type = "upgrade time";
					} else if(parts[1].equals("uc")) {
						type = "upgrade cost";
					} else if(parts[1].equals("mc")) {
						type = "transition metal cost";
					} else if(parts[1].equals("ct")) {
						type = "construction time";
					} else if(parts[1].equals("cc")) {
						type = "construction cost";
					}
					
					double factor = Double.parseDouble(parts[2]);
					previousTower.modifier = "multiplies " + type + " by " + factor;
				} else if(parts[0].equals(":l")) {
						previousTower.limitClass = parts[1];
						
						int max = -1;
						
						if(parts.length >= 3) {
							max = Integer.parseInt(parts[2]);
						}
						
						if(max != -1) {
							limits.put(previousTower.limitClass, max);
						}
					}
			}
			
			if(parts.length >= 6 && parts[1].equals("t")) {
				String name = parts[0];
				//ignore parts[1], just has to be t
				int type = Integer.parseInt(parts[2]);
				double damage = Double.parseDouble(parts[3]);
				double damagePeriod = Double.parseDouble(parts[4]);
				double range = Double.parseDouble(parts[5]);
				
				List<Tower> upgrades = new ArrayList<Tower>();
				
				for(int i = 6; i < parts.length; i++) {
					TowerCost towerCost = parseTowerCost(parts[i]);
					
					//add tower if it doesn't exist
					if(!towerMap.containsKey(towerCost.towerName)) {
						Tower uTower = new Tower();
						uTower.name = towerCost.towerName;
						towerMap.put(uTower.name, uTower);
						towers.add(uTower);
					}
					
					towerMap.get(towerCost.towerName).quarks = towerCost.quarks;
					upgrades.add(towerMap.get(towerCost.towerName));
				}
				
				//add tower if it doesn't exist
				if(!towerMap.containsKey(name)) {
					towerMap.put(name, new Tower());
					towers.add(towerMap.get(name));
				}
				
				Tower tower = towerMap.get(name);
				
				tower.name = name;
				tower.damageType = type == 0 ? "Projectile" : "Area";
				tower.damage = damage;
				tower.damagePeriod = damagePeriod;
				tower.range = (int) range;
				tower.upgrades = upgrades;
				
				previousTower = tower;
			}
		}
		
		in.close();
		
		//now read in tower info and fill in information
		in = new BufferedReader(new FileReader("data/tower_info.txt"));
		
		while((line = in.readLine()) != null) {
			parts = line.split(" ");
			
			if(towerMap.containsKey(parts[0])) {
				Tower tower = towerMap.get(parts[0]);
				tower.element = parts[1];
				tower.elementName = parts[3];
			}
		}
		
		in.close();
		
		//calculate total quarks recursively
		updateQuarks(null, baseTower);
		
		//print results to standard output
		System.out.println("<html>");
		System.out.println("<body>");
		System.out.println("<p>Towers are listed on this page. Total quarks includes previous upgrade costs. Damage period is the milliseconds between dealing damage. DPS means damage per second. DPSPQ means damage per second per quark (based on total quarks). Number in parentheses for upgrades is number of quarks to upgrade.</p>");
		
		for(Tower tower : towers) {
			System.out.println("<a name=\"" + tower.name + "\"></a><h3>" + tower.elementName + "</h3>");
			System.out.println("<table>");
			
			List<Cell> cells = new ArrayList<Cell>();
			Cell cell;
			
			cell = new Cell();
			cells.add(cell);
			cell.a = "Element";
			cell.b = tower.element;
			
			cell = new Cell();
			cells.add(cell);
			cell.a = "Damage type";
			cell.b = tower.damageType;
			
			cell = new Cell();
			cells.add(cell);
			cell.a = "Damage";
			cell.b = tower.damage + "";
			
			cell = new Cell();
			cells.add(cell);
			cell.a = "Damage period";
			cell.b = tower.damagePeriod + " ms";
			
			cell = new Cell();
			cells.add(cell);
			cell.a = "Range";
			cell.b = tower.range + "";
			
			cell = new Cell();
			cells.add(cell);
			cell.a = "Quarks";
			cell.b = tower.quarks + "";
			
			cell = new Cell();
			cells.add(cell);
			cell.a = "Total quarks";
			cell.b = tower.totalQuarks + "";
			
			if(tower.damage != 0) {
				cell = new Cell();
				cells.add(cell);
				cell.a = "DPS";
				cell.b = round2(tower.damage * 1000 / tower.damagePeriod) + "";
			
				cell = new Cell();
				cells.add(cell);
				cell.a = "DPSPQ";
				cell.b = round6(tower.damage * 1000 / tower.damagePeriod / tower.totalQuarks) + "";
			}
			
			if(tower.ability != null) {
				cell = new Cell();
				cells.add(cell);
				cell.a = "Ability";
				cell.b = tower.ability;
			}
			
			if(tower.bonus != null) {
				cell = new Cell();
				cells.add(cell);
				cell.a = "Bonus";
				cell.b = tower.bonus;
			}
			
			if(tower.modifier != null) {
				cell = new Cell();
				cells.add(cell);
				cell.a = "Modifier";
				cell.b = tower.modifier;
			}
			
			if(tower.limitClass != null) {
				cell = new Cell();
				cells.add(cell);
				cell.a = "Limit";
				cell.b = "Only " + limits.get(tower.limitClass) + " of type [" + tower.limitClass + "]";
			}
			
			cell = new Cell();
			cells.add(cell);
			cell.a = "Upgrades";
			cell.b = "";
			
			if(!tower.upgrades.isEmpty()) {
				for(int i = 0; i < tower.upgrades.size(); i++) {
					Tower upgrade = tower.upgrades.get(i);
					
					//add comma if this isn't first
					if(i != 0) cell.b += ", ";
					
					cell.b += "<a href=\"#" + upgrade.name + "\">" + upgrade.elementName + " (" + upgrade.quarks + ")</a>";
				}
			} else {
				cell.b += "none";
			}
			
			if(tower.parent != null) {
				cell = new Cell();
				cells.add(cell);
				cell.a = "Upgraded from";
				cell.b = "<a href=\"#" + tower.parent.name + "\">" + tower.parent.elementName + "</a>";
			}
			
			for(Cell c : cells) {
				System.out.println("<tr>");
				System.out.println("<td>");
				System.out.println(c.a);
				System.out.println("</td>");
				System.out.println("<td>");
				System.out.println(c.b);
				System.out.println("</td>");
				System.out.println("<tr>");
			}
			
			System.out.println("</table>");
		}
		
		System.out.println("</body>");
		System.out.println("</html>");
	}
	
	public static double round2(double d) {
		return Math.round(d * 100) / 100.0;
	}
	
	public static double round6(double d) {
		return Math.round(d * 1000000) / 1000000.0;
	}
	
	public static void updateQuarks(Tower parent, Tower tower) {
		int baseQuarks = parent == null ? 0 : parent.totalQuarks;
		baseQuarks += tower.quarks;
		
		tower.totalQuarks = baseQuarks;
		tower.parent = parent;
		
		for(Tower upgrade : tower.upgrades) {
			updateQuarks(tower, upgrade);
		}
	}
	
	public static TowerCost parseTowerCost(String str) {
		String[] parts = str.split(":");
		
		if(parts.length >= 2) {
			TowerCost towerCost = new TowerCost();
			towerCost.quarks = Integer.parseInt(parts[1]);
			towerCost.towerName = parts[0];
			return towerCost;
		} else {
			return null;
		}
	}
}

class Tower {
	String name;
	String element;
	String elementName;
	
	String damageType;
	double damage;
	double damagePeriod;
	int range;
	int quarks;
	int totalQuarks;
	
	String ability;
	String bonus;
	String modifier;
	String limitClass;
	
	List<Tower> upgrades;
	Tower parent;
}

class TowerCost {
	String towerName;
	int quarks;
}

class Cell {
	String a;
	String b;
}
