package com.perennate.ptgame.world;

public class Modifier {
	public static int PROPERTY_UPGRADETIME = 0;
	public static int PROPERTY_CONSTRUCTTIME = 1;
	public static int PROPERTY_UPGRADECOST = 2;
	public static int PROPERTY_CONSTRUCTCOST = 3;
	public static int PROPERTY_TRANSITIONCOST = 4;
	
	int property;
	double factor;
	
	public Modifier(int property, double factor) {
		this.property = property;
		this.factor = factor;
	}
}
