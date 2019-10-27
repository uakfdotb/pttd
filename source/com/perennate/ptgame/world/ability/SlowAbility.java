package com.perennate.ptgame.world.ability;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.perennate.ptgame.world.Ability;
import com.perennate.ptgame.world.Point;
import com.perennate.ptgame.world.Tower;
import com.perennate.ptgame.world.Unit;
import com.perennate.ptgame.world.World;

public class SlowAbility extends Ability {
	int updateTimer;
	Set<Unit> slowUnits;
	double factor;
	
	public SlowAbility(Tower tower, World world, double factor) {
		super(tower, world);
		this.factor = factor;
		
		updateTimer = 0;
		slowUnits = new HashSet<Unit>();
	}
	
	public void update(int ticks) {
		updateTimer -= ticks;
		
		if(updateTimer <= 0) {
			//damage every aquatic unit by 6
			Iterator<Unit> units = world.getUnits();
			
			while(units.hasNext()) {
				Unit unit = units.next();
				
				if(Point.distance(tower.getLocation(), unit.getLocation()) < 300 && !slowUnits.contains(unit)) {
					slowUnits.add(unit);
					unit.setSpeed(unit.getSpeed() * factor);
				}
			}
			
			//remove out-of-range units
			Iterator<Unit> it = slowUnits.iterator();
			
			while(it.hasNext()) {
				Unit unit = it.next();
				
				if(Point.distance(tower.getLocation(), unit.getLocation()) > 120) {
					unit.setSpeed(unit.getSpeed() / factor);
					it.remove();
				}
			}
			
			updateTimer = 250; //every four times a second
		}
	}
	
	public void reset() {
		Iterator<Unit> it = slowUnits.iterator();
		
		while(it.hasNext()) {
			Unit unit = it.next();
			unit.setSpeed(unit.getSpeed() / factor);
		}
	}
	
	public Ability copy(Tower tower) {
		return new SlowAbility(tower, world, factor);
	}
}