package com.perennate.ptgame;

import javax.swing.JApplet;

public class PTApplet extends JApplet {

	public void init() {
		PTGame.appletLocation = getCodeBase();
		
		Launcher launcher = new Launcher();
		add(launcher);
		setSize(launcher.launch.getPreferredSize());
	}
}
