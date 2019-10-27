package com.perennate.ptgame.world;

public class Projectile {
	Point location;
	double damage;
	
	public Projectile(Point location, double damage) {
		this.location = location;
		this.damage = damage;
	}
	
	public double updateProjectile(Unit unit, int ticks) {
		if(Point.moveTowards(location, unit.location, 0.3 * ticks)) {
			return damage;
		} else {
			return 0;
		}
	}
	
	public Point getLocation() {
		return location;
	}
}
