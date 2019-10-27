package com.perennate.ptgame.world.ability;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.perennate.ptgame.world.Ability;
import com.perennate.ptgame.world.Point;
import com.perennate.ptgame.world.Tower;
import com.perennate.ptgame.world.World;

public class FastAttackAbility extends Ability {
	int updateTimer;
	Map<Tower, String> fastTowers;
	int iFactor;
	double factor;
	
	public FastAttackAbility(Tower tower, World world, int factor) {
		super(tower, world);
		this.iFactor = factor;
		this.factor = 1 + iFactor * 0.1;
		
		updateTimer = 0;
		fastTowers = new HashMap<Tower, String>();
	}
	
	public void update(int ticks) {
		updateTimer -= ticks;
		
		if(updateTimer <= 0) {
			Iterator<Tower> towers = world.getTowers();
			
			while(towers.hasNext()) {
				Tower nTower = towers.next();
				
				//check by name in case the tower has upgraded
				// in that case we need to update it's attack speed again
				if(!fastTowers.containsKey(nTower) || !fastTowers.get(nTower).equals(nTower.getName())) {
					if(Point.distance(tower.getLocation(), nTower.getLocation()) < 200) {
						fastTowers.put(nTower, nTower.getName());
						nTower.setDamagePeriod(nTower.getDamagePeriod() / factor);
					}
				}
			}
			
			//update every second, this isn't critical operation
			updateTimer = 1000;
		}
	}
	
	public void reset() {
		//degrade all towers to original attack speed
		Iterator<Tower> towers = fastTowers.keySet().iterator();
		
		while(towers.hasNext()) {
			Tower tower = towers.next();
			tower.setDamagePeriod(tower.getDamagePeriod() * factor);
		}
	}
	
	public Ability copy(Tower tower) {
		return new FastAttackAbility(tower, world, iFactor);
	}
}
