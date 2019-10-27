package com.perennate.ptgame.world;

public abstract class Ability {
	protected Tower tower;
	protected World world;
	
	public Ability(Tower tower, World world) {
		this.tower = tower;
		this.world = world;
	}
	
	public abstract void update(int ticks);
	public abstract void reset();
	public abstract Ability copy(Tower tower);
}
