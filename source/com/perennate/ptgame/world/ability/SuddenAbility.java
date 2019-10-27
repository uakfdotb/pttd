package com.perennate.ptgame.world.ability;

import java.util.Iterator;

import com.perennate.ptgame.world.Ability;
import com.perennate.ptgame.world.Point;
import com.perennate.ptgame.world.Tower;
import com.perennate.ptgame.world.Unit;
import com.perennate.ptgame.world.World;

public class SuddenAbility extends Ability {
	int updateTimer;
	double factor;
	
	public SuddenAbility(Tower tower, World world, double factor) {
		super(tower, world);
		this.factor = factor;
		
		updateTimer = 0;
	}
	
	public void update(int ticks) {
		updateTimer -= ticks;
		
		if(updateTimer <= 0) {
			Iterator<Unit> units = world.getUnits();
			
			while(units.hasNext()) {
				Unit unit = units.next();
				
				if(unit.getHealth() < unit.getOriginalHealth() * factor &&
						Point.distance(tower.getLocation(), unit.getLocation()) < 180) {
					//kill the unit by damaging for it's health
					unit.damage(unit.getHealth());
				}
			}
		}
	}
	
	public void reset() {
		
	}
	
	public Ability copy(Tower tower) {
		return new SuddenAbility(tower, world, factor);
	}
}
