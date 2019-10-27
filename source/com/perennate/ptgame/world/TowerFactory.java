package com.perennate.ptgame.world;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.perennate.ptgame.PTGame;
import com.perennate.ptgame.world.ability.AceticAbility;
import com.perennate.ptgame.world.ability.FastAttackAbility;
import com.perennate.ptgame.world.ability.FreezeAbility;
import com.perennate.ptgame.world.ability.GoldAbility;
import com.perennate.ptgame.world.ability.PoisonAbility;
import com.perennate.ptgame.world.ability.ReflectAbility;
import com.perennate.ptgame.world.ability.SlowAbility;
import com.perennate.ptgame.world.ability.SuddenAbility;
import com.perennate.ptgame.world.ability.UnstableAbility;

public class TowerFactory {
	World world;
	Map<String, Tower> towers;
	List<TowerCost> baseTowers;
	
	public TowerFactory(World world) {
		this.world = world;
		towers = new HashMap<String, Tower>();
		baseTowers = new ArrayList<TowerCost>();
	}
	
	public void init(String mode) {
		try {
			BufferedReader in;
			
			if(PTGame.appletLocation == null) {
				in = new BufferedReader(new FileReader("data_" + mode + "/towers.txt"));
			} else {
				URL targetURL = new URL(PTGame.appletLocation, "data_" + mode + "/towers.txt");
				in = new BufferedReader(new InputStreamReader(targetURL.openStream()));
			}
			
			//first line contains the base towers and their costs
			// we process this at the end, when the towers are fully constructed
			// this is actually unnecessary but whatever, right?
			String line = in.readLine();
			String baseLine = line;
			Tower previousTower = null;
			
			while((line = in.readLine()) != null) {
				String[] parts = line.split(" ");
				
				if(parts.length == 0) continue;
				
				if(parts[0].charAt(0) == ':') {
					if(parts[0].equals(":a")) {
						//ability
						if(parts[1].equals("nuke")) {
							previousTower.type = Tower.TYPE_REGION;
							previousTower.damage = 800 * Integer.parseInt(parts[2]);
							previousTower.damagePeriod = 6000;
							previousTower.range = 100;
						} else if(parts[1].equals("fastattack")) {
							int factor = Integer.parseInt(parts[2]);
							previousTower.ability = new FastAttackAbility(previousTower, world, factor);
						} else if(parts[1].equals("slow")) {
							double factor = Double.parseDouble(parts[2]);
							previousTower.ability = new SlowAbility(previousTower, world, factor);
						} else if(parts[1].equals("freeze")) {
							int effectPeriod = Integer.parseInt(parts[2]);
							int duration = Integer.parseInt(parts[3]);
							previousTower.ability = new FreezeAbility(previousTower, world, effectPeriod, duration);
						} else if(parts[1].equals("poison")) {
							int effectPeriod = Integer.parseInt(parts[2]);
							double damage = Double.parseDouble(parts[3]);
							int duration = Integer.parseInt(parts[4]);
							previousTower.ability = new PoisonAbility(previousTower, world, effectPeriod, damage, duration);
						} else if(parts[1].equals("acetic")) {
							previousTower.ability = new AceticAbility(previousTower, world);
						} else if(parts[1].equals("unstable")) {
							previousTower.ability = new UnstableAbility(previousTower, world);
						} else if(parts[1].equals("gold")) {
							previousTower.ability = new GoldAbility(previousTower, world, Integer.parseInt(parts[2]));
						} else if(parts[1].equals("reflect")) {
							previousTower.ability = new ReflectAbility(previousTower, world);
						} else if(parts[1].equals("sudden")) {
							double factor = Double.parseDouble(parts[2]);
							previousTower.ability = new SuddenAbility(previousTower, world, factor);
						}
					} else if(parts[0].equals(":b")) {
						//bonus
						previousTower.bonus = new Bonus(parts[1]);
					} else if(parts[0].equals(":w")) {
						//win
						previousTower.win = true;
					} else if(parts[0].equals(":m")) {
						//modifier
						int type = 0;
						
						if(parts[1].equals("ut")) {
							type = Modifier.PROPERTY_UPGRADETIME;
						} else if(parts[1].equals("uc")) {
							type = Modifier.PROPERTY_UPGRADECOST;
						} else if(parts[1].equals("mc")) {
							type = Modifier.PROPERTY_TRANSITIONCOST;
						} else if(parts[1].equals("ct")) {
							type = Modifier.PROPERTY_CONSTRUCTTIME;
						} else if(parts[1].equals("cc")) {
							type = Modifier.PROPERTY_CONSTRUCTCOST;
						}
						
						previousTower.modifier = new Modifier(type, Double.parseDouble(parts[2]));
					} else if(parts[0].equals(":l")) {
						//limit number of towers
						String limitClass = parts[1];
						int max = -1;
						
						if(parts.length >= 3) {
							max = Integer.parseInt(parts[2]);
						}
						
						world.addLimit(previousTower.getName(), limitClass, max);
					}
				}
				if(parts.length >= 6 && parts[1].equals("t")) {
					String name = parts[0];
					//ignore parts[1], just has to be t
					int type = Integer.parseInt(parts[2]);
					double damage = Double.parseDouble(parts[3]);
					double damagePeriod = Double.parseDouble(parts[4]);
					double range = Double.parseDouble(parts[5]);
					
					TowerCost[] upgrades = new TowerCost[parts.length - 6];
					
					for(int i = 6; i < parts.length; i++) {
						upgrades[i - 6] = parseTowerCost(parts[i]);
					}
					
					previousTower = new Tower(name, type, damage, damagePeriod, range, upgrades);
					towers.put(name, previousTower);
				}
			}
			
			in.close();
			
			//now go through base towers
			String[] parts = baseLine.split(" ");
			
			for(String part : parts) {
				TowerCost towerCost = parseTowerCost(part);
				
				if(towerCost != null) {
					baseTowers.add(towerCost);
				}
			}
			
			//create the base tower
			TowerCost[] towerCost = new TowerCost[baseTowers.size()];
			
			for(int i = 0; i < baseTowers.size(); i++) {
				towerCost[i] = baseTowers.get(i);
			}
			
			Tower baseTower = new Tower("base", 0, 0, 0, 0, towerCost);
			towers.put("base", baseTower);
		} catch(IOException ioe) {
			ioe.printStackTrace();
			System.out.println("Error reading towers: " + ioe.getLocalizedMessage());
		}
	}
	
	public TowerCost parseTowerCost(String str) {
		String[] parts = str.split(":");
		
		if(parts.length >= 2) {
			TowerCost towerCost = new TowerCost(parts[0], Integer.parseInt(parts[1]));
			return towerCost;
		} else {
			return null;
		}
	}
	
	public Tower get(String str) {
		return towers.get(str);
	}
}
