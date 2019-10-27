package com.perennate.ptgame.world;

public abstract class Sprite {
	boolean deleteMe;
	World world;
	
	public Sprite() {
		deleteMe = false;
	}
	
	public void setWorld(World world) {
		this.world = world;
	}
	
	public abstract void update(int ticks);
}
