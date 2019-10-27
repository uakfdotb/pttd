package com.perennate.ptgame.world;

public class Tower extends Sprite {
	public static int TYPE_PROJECTILE = 0;
	public static int TYPE_REGION = 1;
	public static int WIDTH = 45;
	
	Point location;
	double periodTimer;
	double upgradeTimer;
	Unit target;
	
	//properties
	String name;
	int type;
	double damage;
	double damagePeriod;
	double range;
	TowerCost[] upgrades;
	Bonus bonus;
	Modifier modifier;
	Ability ability;
	boolean win;
	
	public Tower(String name, int type, double damage, double damagePeriod, double range, TowerCost[] upgrades) {
		this(name, type, damage, damagePeriod, range, upgrades, null, null, null, false);
	}
	
	public Tower(String name, int type, double damage, double damagePeriod, double range, TowerCost[] upgrades, Bonus bonus, Modifier modifier, Ability ability, boolean win) {
		this.name = name;
		this.type = type;
		this.damage = damage;
		this.damagePeriod = damagePeriod;
		this.range = range;
		this.upgrades = upgrades;
		this.bonus = bonus;
		this.modifier = modifier;
		this.win = win;
		
		if(ability != null) {
			this.ability = ability.copy(this);
		}
		
		periodTimer = 0;
		location = new Point(0, 0);
	}
	
	public Tower(Tower copy) {
		this(copy.name, copy.type, copy.damage, copy.damagePeriod, copy.range, copy.upgrades, copy.bonus, copy.modifier, copy.ability, copy.win);
	}
	
	public void copy(Tower copy) {
		this.name = copy.name;
		this.type = copy.type;
		this.damage = copy.damage;
		this.damagePeriod = copy.damagePeriod;
		this.range = copy.range;
		this.upgrades = copy.upgrades;
		this.bonus = copy.bonus;
		this.modifier = copy.modifier;
		this.ability = copy.ability;
		this.win = copy.win;
		
		if(copy.ability != null) {
			this.ability = copy.ability.copy(this);
		}
	}
	
	public void setLocation(Point point) {
		this.location = point;
	}
	
	public Point getLocation() {
		return location;
	}
	
	public double getRange() {
		return range;
	}
	
	public double getDamage() {
		return damage;
	}
	
	public double getDamagePeriod() {
		return damagePeriod;
	}
	
	//used by fast attack
	public void setDamagePeriod(double damagePeriod) {
		this.damagePeriod = damagePeriod;
	}
	
	public int getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public TowerCost[] getUpgrades() {
		return upgrades;
	}
	
	public boolean isUpgrading() {
		return upgradeTimer > 0;
	}
	
	public int getTimeSinceLastDamage() {
		return (int) periodTimer;
	}
	
	public void selectTarget() {
		//find closest unit
		target = null; //target might not have been null, but should be invalid
		
		for(Unit unit : world.units) {
			double distance = Point.distance(location, unit.location);
			
			if(distance <= range) {
				target = unit;
				
				//favor closer units
				if(Math.random() >= Math.max(0.6, distance / range)) {
					break;
				}
			}
		}
	}
	
	public void upgrade(String str) {
		if(isUpgrading()) return;
		
		//check for the tower cost instance
		TowerCost found = null;
		Tower tower = null;
		
		for(TowerCost towerCost : upgrades) {
			tower = world.factory.get(towerCost.towerName);
			
			if(tower.name.equals(str)) {
				found = towerCost;
				break;
			}
		}
		
		if(found != null && world.quarks >= found.quarks) {
			System.out.println("[Tower] Upgrading from " + name + " to " + tower.name + "...");
			
			//make sure adding this won't exceed the limit
			if(world.checkLimit(name, found.towerName)) {
				world.quarks -= found.quarks * world.upgradeCost;
				
				//reset ability if we had one
				if(ability != null) {
					ability.reset();
				}
				
				//cancel the previous modifier, if any
				if(modifier != null) {
					if(modifier.property == Modifier.PROPERTY_CONSTRUCTCOST) {
						world.constructCost /= modifier.factor;
					} else if(modifier.property == Modifier.PROPERTY_CONSTRUCTTIME) {
						world.constructTime /= modifier.factor;
					} else if(modifier.property == Modifier.PROPERTY_TRANSITIONCOST) {
						world.transitionCost /= modifier.factor;
					} else if(modifier.property == Modifier.PROPERTY_UPGRADETIME) {
						world.upgradeTime /= modifier.factor;
					} else if(modifier.property == Modifier.PROPERTY_UPGRADECOST) {
						world.upgradeCost /= modifier.factor;
					}
				}
				
				upgradeTimer = world.upgradeTime;
				copy(tower); //copy tower properties
				
				//new modifier goes into effect after construction is complete
			} else {
				System.out.println("[Tower] Limit exceeded for " + found.towerName + "!");
			}
		} else {
			System.out.println("[Tower] Upgrading from " + name + " to " + tower.name + " failed: to tower not found in upgrades or not enough quarks");
		}
	}
	
	public void damage() {
		if(damage == 0 || range == 0) return;
		
		if(type == TYPE_PROJECTILE) {
			if(target == null || target.deleteMe || Point.distance(location, target.location) > range) {
				selectTarget();
			}
			
			//target might still be null if no one is in range
			if(target != null) {
				double targetDamage = damage;
				
				if(bonus != null) {
					targetDamage = bonus.adjust(target, targetDamage);
				}
				
				Projectile projectile = new Projectile(new Point(location), targetDamage);
				target.addProjectile(projectile);
			}
		} else {
			for(Unit unit : world.units) {
				if(Point.distance(location, unit.location) < range) {
					double targetDamage = damage;
					
					if(bonus != null) {
						targetDamage = bonus.adjust(target, targetDamage);
					}
					
					unit.damage(targetDamage);
				}
			}
		}
	}
	
	public void update(int ticks) {
		if(upgradeTimer == 0) {
			periodTimer += ticks;
			
			if(periodTimer >= damagePeriod) {
				damage();
				periodTimer = 0;
			}
			
			if(ability != null) {
				ability.update(ticks);
			}
		} else {
			upgradeTimer -= ticks;
			
			if(upgradeTimer <= 0) {
				upgradeTimer = 0;
				
				if(modifier != null) {
					if(modifier.property == Modifier.PROPERTY_CONSTRUCTCOST) {
						world.constructCost *= modifier.factor;
					} else if(modifier.property == Modifier.PROPERTY_CONSTRUCTTIME) {
						world.constructTime *= modifier.factor;
					} else if(modifier.property == Modifier.PROPERTY_TRANSITIONCOST) {
						world.transitionCost *= modifier.factor;
					} else if(modifier.property == Modifier.PROPERTY_UPGRADETIME) {
						world.upgradeTime *= modifier.factor;
					} else if(modifier.property == Modifier.PROPERTY_UPGRADECOST) {
						world.upgradeCost *= modifier.factor;
					}
				}
				
				if(win && world.playerWin == 0) {
					world.playerWin = 1;
				}
			}
		}
	}
}
