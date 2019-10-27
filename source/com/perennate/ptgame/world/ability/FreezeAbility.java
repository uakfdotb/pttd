package com.perennate.ptgame.world.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.perennate.ptgame.world.Ability;
import com.perennate.ptgame.world.Point;
import com.perennate.ptgame.world.Tower;
import com.perennate.ptgame.world.Unit;
import com.perennate.ptgame.world.World;

public class FreezeAbility extends Ability {
	int effectTimer;
	List<FrozenUnit> frozenUnits;
	int effectPeriod;
	int freezeDuration;
	
	public FreezeAbility(Tower tower, World world, int effectPeriod, int freezeDuration) {
		super(tower, world);
		this.effectPeriod = effectPeriod;
		this.freezeDuration = freezeDuration;
		
		effectTimer = 0;
		frozenUnits = new ArrayList<FrozenUnit>();
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
				frozenUnits.add(new FrozenUnit(target, freezeDuration));
			}
			
			effectTimer = effectPeriod; //reset the timer
		}
		
		Iterator<FrozenUnit> it = frozenUnits.iterator();
		
		while(it.hasNext()) {
			if(it.next().update(ticks)) {
				it.remove();
			}
		}
	}
	
	public void reset() {
		Iterator<FrozenUnit> it = frozenUnits.iterator();
		
		while(it.hasNext()) {
			it.next().update(freezeDuration + 1000);
		}
	}
	
	public Ability copy(Tower tower) {
		return new FreezeAbility(tower, world, effectPeriod, freezeDuration);
	}
}

class FrozenUnit {
	Unit unit;
	int freezeTimer;
	
	public FrozenUnit(Unit unit, int freezeTimer) {
		this.unit = unit;
		this.freezeTimer = freezeTimer;
		unit.setSpeed(0);
	}
	
	public boolean update(int ticks) {
		freezeTimer -= ticks;
		
		if(freezeTimer <= 0) {
			unit.setSpeed(unit.getOriginalSpeed());
			return true;
		} else {
			//in case another effect restored speed after ending, we make sure it stays zero until we end
			unit.setSpeed(0);
			return false;
		}
	}
}