package com.perennate.ptgame.world;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.perennate.ptgame.PTGame;

public class World {
	public TowerFactory factory;
	List<Unit> units;
	List<Tower> towers;
	Queue<UnitTime> unitSequence;
	
	int quarks; //current number of quarks
	int life; //health remaining
	double ticksElapsed;
	int playerWin = 0; //-1 if player lost, 1  if player won
	int numWaves = 0; //number of waves total
	int victoryCounter = 0; //units spawned since victory
	boolean paused; //whether the state is paused
	
	//manage limits on number of towers
	Map<String, String> towerToLimit; //maps from tower name to limit class
	Map<String, Limit> limits; //maps from limit class name to limit
	
	//user properties (based on tower)
	double upgradeTime = 10000;
	double constructTime = 30000;
	double transitionCost = 1;
	double upgradeCost = 1;
	double constructCost = 1;
	
	public World() {
		factory = new TowerFactory(this);
		units = new ArrayList<Unit>();
		towers = new ArrayList<Tower>();
		unitSequence = new LinkedList<UnitTime>();
		towerToLimit = new HashMap<String, String>();
		limits = new HashMap<String, Limit>();
		
		quarks = 100;
		life = 30;
	}
	
	public void init(String mode) {
		factory.init(mode);
		initializeTowers();
		initializeUnits(mode);
	}
	
	public void update(int ticks) {
		if(paused) return; //don't update when paused
		
		synchronized(this) {
			ticksElapsed += ticks;
			
			//spawn units
			if(unitSequence.isEmpty()) {
				//if there are no more units, this player has won!
				//assuming player hasn't won/lost already
				if(units.isEmpty() && playerWin == 0) {
					playerWin = 1; //win
				} else if(playerWin == 1 && units.size() < 50) {
					//create some units just because
					victoryCounter++;
					
					Queue<Point> points = new LinkedList<Point>();
					Point start = new Point(-30, 370);
					points.add(new Point(885, 370));
					points.add(new Point(885, 15));
					points.add(new Point(-30, 15));
					
					Unit unit = new Unit(start, points, 0, 60, victoryCounter * 1000, 0);
					unit.setWorld(this);
					units.add(unit);
				}
			} else {
				if(unitSequence.peek().time <= ticksElapsed) {
					Unit unit = unitSequence.poll().unit;
					unit.setWorld(this);
					units.add(unit);
				}
			}
			
			Iterator<Unit> unitIterator = units.iterator();
			while(unitIterator.hasNext()) {
				Unit unit = unitIterator.next();
				if(unit.deleteMe) {
					unitIterator.remove();
				} else {
					unit.update(ticks);
				}
			}
			
			for(Tower tower : towers) {
				tower.update(ticks);
			}
			
			//check life
			if(life <= 0 && playerWin == 0) {
				playerWin = -1; //loss
			}
		}
	}
	
	public void makeTower(int x, int y, String type, int cost) {
		Tower tower;
		
		if(type == null) {
			tower = new Tower(factory.get("base"));
		} else {
			TowerCost[] towerCost = {new TowerCost("base", cost)};
			tower = new Tower(type, 0, 0, 0, 0, towerCost);
		}
		
		tower.setLocation(new Point(50 + x * 47, 50 + y * 47));
		tower.setWorld(this);
		towers.add(tower);
	}
	
	public void addLimit(String tower, String limitClass, int max) {
		if(limits.containsKey(limitClass)) {
			Limit limit = limits.get(limitClass);
			
			if(max != -1 && limit.maximum != max) {
				if(limit.maximum == -1) {
					limit.maximum = max;
				} else {
					System.out.println("Warning: ignoring max=" + max + " for " + limitClass + " on " + tower + ": already set!");
				}
			}
			
			towerToLimit.put(tower, limitClass);
		} else {
			limits.put(tower, new Limit(max));
			towerToLimit.put(tower, limitClass);
		}
	}
	
	//true means everything is ok
	//upgrading from oldTower to tower
	public boolean checkLimit(String oldTower, String tower) {
		String oldLimitClass = null;
		String newLimitClass = null;
		
		if(towerToLimit.containsKey(oldTower)) {
			oldLimitClass = towerToLimit.get(oldTower);
		}
		
		if(towerToLimit.containsKey(tower)) {
			newLimitClass = towerToLimit.get(tower);
		}
		
		System.out.println("[World] Checking limits from " + oldTower + " (" + oldLimitClass + ") to " + tower + " (" + newLimitClass + ")");
		
		if(oldLimitClass == null && newLimitClass != null) {
			//new one has limit
			Limit limit = limits.get(newLimitClass);
			return limit.increment();
		} else if(oldLimitClass != null && newLimitClass == null) {
			//old limit can be removed
			Limit limit = limits.get(oldLimitClass);
			limit.decrement();
			return true;
		} else if((oldLimitClass == null && newLimitClass == null) || oldLimitClass.equals(newLimitClass)) {
			//nothing to do, limit class is same
			return true;
		} else {
			//both have limits, but they're not the same
			Limit oldLimit = limits.get(oldLimitClass);
			Limit newLimit = limits.get(newLimitClass);
			
			//have to be careful here, because tower will be built or not built
			// depending on what we return
			if(newLimit.increment()) {
				oldLimit.decrement();
				return true;
			} else {
				return false;
			}
		}
	}
	
	public Iterator<Unit> getUnits() {
		return units.iterator();
	}
	
	public Iterator<Tower> getTowers() {
		return towers.iterator();
	}
	
	public int getQuarks() {
		return quarks;
	}
	
	public void incrementQuarks() {
		quarks++;
	}
	
	public void cheat() {
		quarks += 100;
	}
	
	public int getLife() {
		return life;
	}
	
	public int getResultStatus() {
		return playerWin;
	}

	public double getTicks() {
		return ticksElapsed;
	}
	
	public int getTime() {
		return (int) (ticksElapsed / 1000);
	}
	
	public int getNextWave() {
		return 60 - (getTime() + 40) % 60;
	}
	
	public int getWaveNumber() {
		return (getTime() + 40) / 60;
	}
	
	public int getWaveTotal() {
		return numWaves;
	}
	
	public void togglePause() {
		paused = !paused;
	}
	
	public void initializeUnits(String mode) {
		try {
			BufferedReader in;
			
			if(PTGame.appletLocation == null) {
				in = new BufferedReader(new FileReader("data_" + mode + "/units.txt"));
			} else {
				URL targetURL = new URL(PTGame.appletLocation, "data_" + mode + "/units.txt");
				in = new BufferedReader(new InputStreamReader(targetURL.openStream()));
			}
			
			String line;
			int counter = 0;
			
			while((line = in.readLine()) != null) {
				String[] parts = line.split(" ");
				int numUnits = Integer.parseInt(parts[0]);
				int type = Integer.parseInt(parts[1]);
				double speed = Double.parseDouble(parts[2]);
				double health = Double.parseDouble(parts[3]);
				int quarks = Integer.parseInt(parts[4]);
				int timeDifference = Integer.parseInt(parts[5]);
				
				for(int i = 0; i < numUnits; i++) {
					Queue<Point> points1 = new LinkedList<Point>();
					Point start1 = new Point(-30, 370);
					points1.add(new Point(885, 370));
					points1.add(new Point(885, 15));
					points1.add(new Point(-30, 15));
					
					Queue<Point> points2 = new LinkedList<Point>();
					Point start2 = new Point(930, 15);
					points2.add(new Point(10, 15));
					points2.add(new Point(10, 370));
					points2.add(new Point(930, 370));

					Unit unit1 = new Unit(start1, points1, type, speed, health, quarks);
					Unit unit2 = new Unit(start2, points2, type, speed, health, quarks);
					int time = 20000 + counter * 60000 + i * timeDifference;

					unitSequence.add(new UnitTime(time, unit1));
					unitSequence.add(new UnitTime(time, unit2));
				}
				
				counter++;
			}
			
			numWaves = counter;
			
			in.close();
		} catch(IOException ioe) {
			System.out.println("Error while reading unit sequence: " + ioe.getLocalizedMessage());
		}
	}
	
	public void initializeTowers() {
		makeTower(0, 0, null, 0);
		makeTower(0, 1, null, 0);
		makeTower(0, 2, null, 0);
		makeTower(0, 3, null, 0);
		makeTower(0, 4, null, 0);
		makeTower(0, 5, null, 0);
		makeTower(0, 6, null, 0);
		makeTower(1, 1, null, 0);
		makeTower(1, 2, null, 0);
		makeTower(1, 3, null, 0);
		makeTower(1, 4, null, 0);
		makeTower(1, 5, null, 0);
		makeTower(1, 6, null, 0);
		makeTower(2, 3, "sc", 200);
		makeTower(2, 4, "y", 250);
		makeTower(2, 5, "lu", 300);
		makeTower(2, 6, "lr", 350);
		makeTower(3, 3, "ti", 200);
		makeTower(3, 4, "zr", 250);
		makeTower(3, 5, "hf", 300);
		makeTower(3, 6, "rf", 350);
		makeTower(4, 3, "v", 200);
		makeTower(4, 4, "nb", 250);
		makeTower(4, 5, "ta", 300);
		makeTower(4, 6, "db", 350);
		makeTower(5, 3, "cr", 200);
		makeTower(5, 4, "mo", 250);
		makeTower(5, 5, "w", 300);
		makeTower(5, 6, "sg", 350);
		makeTower(6, 3, "mn", 200);
		makeTower(6, 4, "tc", 250);
		makeTower(6, 5, "re", 300);
		makeTower(6, 6, "bh", 350);
		makeTower(7, 3, "fe", 200);
		makeTower(7, 4, "ru", 250);
		makeTower(7, 5, "os", 300);
		makeTower(7, 6, "hs", 350);
		makeTower(8, 3, "co", 200);
		makeTower(8, 4, "rh", 250);
		makeTower(8, 5, "ir", 300);
		makeTower(8, 6, "mt", 350);
		makeTower(9, 3, "ni", 200);
		makeTower(9, 4, "pd", 250);
		makeTower(9, 5, "pt", 300);
		makeTower(9, 6, "ds", 350);
		makeTower(10, 3, "cu", 200);
		makeTower(10, 4, "ag", 250);
		makeTower(10, 5, "au", 300);
		makeTower(10, 6, "rg", 350);
		makeTower(11, 3, "zn", 200);
		makeTower(11, 4, "cd", 250);
		makeTower(11, 5, "hg", 300);
		makeTower(11, 6, "cn", 350);
		makeTower(12, 1, null, 0);
		makeTower(12, 2, null, 0);
		makeTower(12, 3, null, 0);
		makeTower(12, 4, null, 0);
		makeTower(12, 5, null, 0);
		makeTower(12, 6, null, 0);
		makeTower(13, 1, null, 0);
		makeTower(13, 2, null, 0);
		makeTower(13, 3, null, 0);
		makeTower(13, 4, null, 0);
		makeTower(13, 5, null, 0);
		makeTower(13, 6, null, 0);
		makeTower(14, 1, null, 0);
		makeTower(14, 2, null, 0);
		makeTower(14, 3, null, 0);
		makeTower(14, 4, null, 0);
		makeTower(14, 5, null, 0);
		makeTower(14, 6, null, 0);
		makeTower(15, 1, null, 0);
		makeTower(15, 2, null, 0);
		makeTower(15, 3, null, 0);
		makeTower(15, 4, null, 0);
		makeTower(15, 5, null, 0);
		makeTower(15, 6, null, 0);
		makeTower(16, 1, null, 0);
		makeTower(16, 2, null, 0);
		makeTower(16, 3, null, 0);
		makeTower(16, 4, null, 0);
		makeTower(16, 5, null, 0);
		makeTower(16, 6, null, 0);
		makeTower(17, 0, null, 0);
		makeTower(17, 1, null, 0);
		makeTower(17, 2, null, 0);
		makeTower(17, 3, null, 0);
		makeTower(17, 4, null, 0);
		makeTower(17, 5, null, 0);
		makeTower(17, 6, null, 0);
	}
}

class Limit {
	int current;
	int maximum;
	
	public Limit(int maximum) {
		this.maximum = maximum;
		current = 0;
	}
	
	public boolean increment() {
		if(current >= maximum) return false;
		else {
			current++;
			return true;
		}
	}
	
	public void decrement() {
		current--;
	}
}