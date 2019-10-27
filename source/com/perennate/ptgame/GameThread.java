package com.perennate.ptgame;

import com.perennate.ptgame.graphics.PTFrame;
import com.perennate.ptgame.world.World;

public class GameThread extends Thread {
	World world;
	PTFrame frame;
	boolean quit;
	
	public GameThread(World world, PTFrame frame) {
		this.world = world;
		this.frame = frame;
		quit = false;
	}
	
	public void init() {
		start();
	}
	
	public void quit() {
		quit = true;
	}
	
	public void run() {
		long lastMillis = System.currentTimeMillis();
		
		while(!quit) {
			try {
				Thread.sleep(Math.max(5, 25 - (System.currentTimeMillis() - lastMillis)));
			} catch(InterruptedException e) {}
			
			int elapsed = (int) (System.currentTimeMillis() - lastMillis);
			world.update(elapsed);
			frame.gameUpdated();
			
			lastMillis = System.currentTimeMillis();
		}
	}
}
