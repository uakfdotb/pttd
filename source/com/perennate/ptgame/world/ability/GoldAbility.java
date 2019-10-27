package com.perennate.ptgame.world.ability;

import com.perennate.ptgame.world.Ability;
import com.perennate.ptgame.world.Tower;
import com.perennate.ptgame.world.World;

public class GoldAbility extends Ability {
	int effectTimer;
	int effectPeriod;
	
	public GoldAbility(Tower tower, World world, int effectPeriod) {
		super(tower, world);
		this.effectPeriod = effectPeriod;
		
		effectTimer = 0;
	}
	
	public void update(int ticks) {
		effectTimer -= ticks;
		
		if(effectTimer <= 0) {
			world.incrementQuarks();
			effectTimer = effectPeriod;
		}
	}
	
	public void reset() {
		
	}
	
	public Ability copy(Tower tower) {
		return new GoldAbility(tower, world, effectPeriod);
	}
}
