package com.perennate.ptgame.world.ability;

import java.util.Iterator;

import com.perennate.ptgame.world.Ability;
import com.perennate.ptgame.world.Tower;
import com.perennate.ptgame.world.Unit;
import com.perennate.ptgame.world.World;

public class AceticAbility extends Ability {
	int effectTimer;
	
	public AceticAbility(Tower tower, World world) {
		super(tower, world);
		effectTimer = 0;
	}
	
	public void update(int ticks) {
		effectTimer -= ticks;
		
		if(effectTimer <= 0) {
			//damage every aquatic unit by 6
			Iterator<Unit> units = world.getUnits();
			
			while(units.hasNext()) {
				Unit unit = units.next();
				
				if(unit.getType() == Unit.TYPE_AQUATIC) {
					unit.damage(6);
				}
			}
			
			effectTimer = 1000; //every second
		}
	}
	
	public void reset() {
		//nothing to do
	}
	
	public Ability copy(Tower tower) {
		return new AceticAbility(tower, world);
	}
}
