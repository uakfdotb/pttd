package com.perennate.ptgame.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Unit extends Sprite {
	public static int TYPE_NORMAL = 0;
	public static int TYPE_AQUATIC = 1;
	public static int TYPE_SMALL = 2;
	public static int TYPE_DARK = 5;
	
	Point location;
	Queue<Point> path;
	double health;
	double speed;
	List<Projectile> projectiles; //projectiles that are coming at us
	
	int height = 10;
	int width = 10;
	
	//properties
	double originalSpeed;
	double originalHealth;
	int type;
	int quarks;
	
	public Unit() {
		path = new LinkedList<Point>();
		location = new Point(0, 0);
		projectiles = new ArrayList<Projectile>();
	}
	
	public Unit(Point location, Queue<Point> path) {
		this(location, path, 0, 0, 0, 0);
	}
	
	public Unit(Point location, Queue<Point> path, int type, double speed, double health, int quarks) {
		this.location = location;
		this.path = path;
		originalSpeed = speed;
		originalHealth = health;
		this.type = type;
		this.quarks = quarks;
		
		projectiles = new ArrayList<Projectile>();
		
		this.health = originalHealth;
		this.speed = originalSpeed;
		
		if(type == TYPE_SMALL) {
			height = 5;
			width = 5;
		}
	}
	
	public void appendPath(Point x) {
		path.add(x);
	}
	
	public void addProjectile(Projectile p) {
		projectiles.add(p);
	}
	
	public Point getLocation() {
		return location;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getType() {
		return type;
	}
	
	public double getSpeed() {
		return speed;
	}
	
	public double getOriginalSpeed() {
		return originalSpeed;
	}
	
	public double getHealth() {
		return health;
	}
	
	public double getOriginalHealth() {
		return originalHealth;
	}
	
	public Iterator<Projectile> getProjectiles() {
		return projectiles.iterator();
	}
	
	public void damage(double amount) {
		health -= amount;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	public void update(int ticks) {
		//update projectiles
		for(int i = 0; i < projectiles.size(); i++) {
			Projectile projectile = projectiles.get(i);
			double damage = projectile.updateProjectile(this, ticks);
			
			if(damage != 0) {
				projectiles.remove(i);
				damage(damage);
			}
		}
		
		//update position
		boolean reachedTarget = Point.moveTowards(location, path.peek(), speed * ticks / 1000.0);
		
		if(reachedTarget) {
			path.poll();
		}
		
		if(path.isEmpty() || health <= 0) {
			deleteMe = true;
			
			if(health > 0) {
				//we passed last waypoint, decrease health
				world.life--;
			} else {
				//give user some money for killing us
				world.quarks += quarks;
			}
		}
	}
}
