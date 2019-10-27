package com.perennate.ptgame.world.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.perennate.ptgame.world.Ability;
import com.perennate.ptgame.world.Point;
import com.perennate.ptgame.world.Tower;
import com.perennate.ptgame.world.Unit;
import com.perennate.ptgame.world.World;

public class PoisonAbility extends Ability {
	int effectTimer;
	List<PoisonUnit> poisonUnits;
	int effectPeriod;
	double damage;
	int duration;
	
	public PoisonAbility(Tower tower, World world, int effectPeriod, double damage, int duration) {
		super(tower, world);
		this.effectPeriod = effectPeriod;
		this.damage = damage;
		this.duration = duration;
		
		effectTimer = 0;
		poisonUnits = new ArrayList<PoisonUnit>();
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
				
				if(distance < 150) {
					target = unit;
					
					//favor closer units
					if(Math.random() >= Math.max(0.6, distance / 150)) {
						break;
					}
				}
			}
			
			if(target != null) {
				poisonUnits.add(new PoisonUnit(target, duration / 1000, 1000, damage));
			}
			
			effectTimer = effectPeriod; //reset the timer
		}
		
		Iterator<PoisonUnit> it = poisonUnits.iterator();
		
		while(it.hasNext()) {
			if(it.next().update(ticks)) {
				it.remove();
			}
		}
	}
	
	public void reset() {
		//nothing to do
	}
	
	public Ability copy(Tower tower) {
		return new PoisonAbility(tower, world, effectPeriod, damage, duration);
	}
}

class PoisonUnit {
	Unit unit;
	int count;
	int poisonTimer;
	int originalTimer;
	double damage;
	
	//count is number of times to poison
	//deals damage every poisonTimer ms until count reaches zero
	public PoisonUnit(Unit unit, int count, int poisonTimer, double damage) {
		this.unit = unit;
		this.count = count;
		this.poisonTimer = poisonTimer;
		this.damage = damage;
		
		originalTimer = poisonTimer;
	}
	
	public boolean update(int ticks) {
		poisonTimer -= ticks;
		
		if(poisonTimer <= 0) {
			unit.damage(damage);
			count--;
			
			if(count <= 0) {
				return true;
			} else {
				//continue poisoning until count is zero
				poisonTimer = originalTimer;
				return false;
			}
		} else {
			return false;
		}
	}
}