package com.perennate.ptgame;

import java.net.URL;

import com.perennate.ptgame.graphics.PTFrame;
import com.perennate.ptgame.world.World;

public class PTGame {
	//base URL if this is being run as applet, and applet is not local
	// null otherwise
	public static URL appletLocation = null;
	
	public static void main(String args[]) {
		World world = new World();
		PTFrame frame = new PTFrame(world);
		frame.init();
	}
}
