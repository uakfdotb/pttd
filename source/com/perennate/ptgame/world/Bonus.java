package com.perennate.ptgame.world;

public class Bonus {
	String type;
	
	public Bonus(String type) {
		this.type = type;
	}
	
	public double adjust(Unit target, double damage) {
		if(type.equals("aquatic") && target.type == Unit.TYPE_AQUATIC) {
			return damage * 2;
		} else if(type.equals("dark") && target.type == Unit.TYPE_DARK) {
			return damage * 10000;
		} else {
			return damage;
		}
	}
}
