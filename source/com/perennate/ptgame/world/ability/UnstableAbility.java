package com.perennate.ptgame.world.ability;

import java.util.Iterator;

import com.perennate.ptgame.world.Ability;
import com.perennate.ptgame.world.Tower;
import com.perennate.ptgame.world.Unit;
import com.perennate.ptgame.world.World;

public class UnstableAbility extends Ability {
	int effectTimer;
	
	public UnstableAbility(Tower tower, World world) {
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
				unit.damage(8);
			}
			
			effectTimer = 1000; //every second
		}
	}
	
	public void reset() {
		
	}
	
	public Ability copy(Tower tower) {
		return new UnstableAbility(tower, world);
	}
}
