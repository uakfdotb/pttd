package com.perennate.ptgame.world;

public class Point {
	public double x;
	public double y;
	
	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Point(Point p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public boolean equals(Point p) {
		return x == p.x && y == p.y;
	}
	
	public String toString() {
		return "(" + x + "," + y + ")";
	}
	
	public static double distance(Point a, Point b) {
		//calculate straight-line Euclidean distance
		double dx = a.x - b.x;
		double dy = a.y - b.y;
		return Math.sqrt(dx * dx + dy * dy);
	}
	
	//updates location to be the next step based on speed
	public static boolean moveTowards(Point location, Point target, double speed) {
		double distance = distance(location, target);
		
		if(distance < speed) {
			//we can reach the destination
			location.x = target.x;
			location.y = target.y;
			return true;
		} else {
			double fraction = speed / distance;
			location.x += (target.x - location.x) * fraction;
			location.y += (target.y - location.y) * fraction;
			return false;
		}
	}
	
	//true if point is contained in region, false otherwise
	public static boolean isPointContained(Point p, Point center, int width, boolean square) {
		if(!square) {
			//simple circular distance
			return distance(p, center) < width;
		} else {
			//rectangle, bit more complicated
			return p.x >= center.x - width / 2 && p.y >= center.y - width / 2 &&
					p.x <= center.x + width / 2 && p.y <= center.y + width / 2;
		}
	}
	
	//true if point is contained in rectangle, false otherwise
	public static boolean isPointContainedRect(Point p, Point center, int width, int height) {
		return p.x >= center.x - width / 2 && p.y >= center.y - height / 2 &&
				p.x <= center.x + width / 2 && p.y <= center.y + height / 2;
	}
}
