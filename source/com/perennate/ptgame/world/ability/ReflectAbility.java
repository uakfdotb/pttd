package com.perennate.ptgame.world.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.perennate.ptgame.world.Ability;
import com.perennate.ptgame.world.Point;
import com.perennate.ptgame.world.Tower;
import com.perennate.ptgame.world.Unit;
import com.perennate.ptgame.world.World;

public class ReflectAbility extends Ability {
	int effectTimer;
	List<ReflectUnit> reflectUnits;
	
	public ReflectAbility(Tower tower, World world) {
		super(tower, world);
		
		effectTimer = 0;
		reflectUnits = new ArrayList<ReflectUnit>();
	}
	
	public void update(int ticks) {
		effectTimer -= ticks;
		
		if(effectTimer <= 0) {
			//damage every aquatic unit by 6
			Iterator<Unit> units = world.getUnits();
			Unit target = null;
			
			while(units.hasNext()) {
				Unit unit = units.next();
				double distance = Point.distance(tower.getLocation(), unit.getLocation());
				
				if(distance < 150 && unit.getType() != Unit.TYPE_DARK) {
					target = unit;
					
					//favor closer units
					if(Math.random() >= Math.max(0.6, distance / 150)) {
						break;
					}
				}
			}
			
			if(target != null) {
				reflectUnits.add(new ReflectUnit(target, 2000));
			}
			
			effectTimer = 1000; //reset the timer
		}
		
		Iterator<ReflectUnit> it = reflectUnits.iterator();
		
		while(it.hasNext()) {
			if(it.next().update(ticks)) {
				it.remove();
			}
		}
	}
	
	public void reset() {
		Iterator<ReflectUnit> it = reflectUnits.iterator();
		
		while(it.hasNext()) {
			it.next().update(2000);
		}
	}
	
	public Ability copy(Tower tower) {
		return new ReflectAbility(tower, world);
	}
}

class ReflectUnit {
	Unit unit;
	int reflectTimer;
	
	public ReflectUnit(Unit unit, int reflectTimer) {
		this.unit = unit;
		this.reflectTimer = reflectTimer;
		unit.setSpeed(-0.1 * unit.getOriginalSpeed());
	}
	
	public boolean update(int ticks) {
		reflectTimer -= ticks;
		
		if(reflectTimer <= 0) {
			unit.setSpeed(unit.getOriginalSpeed());
			return true;
		} else {
			//update speed every time
			// in case another effect ended and restored speed
			unit.setSpeed(-0.1 * unit.getOriginalSpeed());
			return false;
		}
	}
}