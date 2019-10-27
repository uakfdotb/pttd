package com.perennate.ptgame.graphics;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.perennate.ptgame.GameThread;
import com.perennate.ptgame.world.World;

public class PTFrame extends JFrame implements WindowListener {
	World world;
	Map<String, JPanel> panels;
	GameThread gameThread;
	
	//panels
	StartPanel startPanel;
	GamePanel gamePanel;
	
	public PTFrame(World world) {
		super("PTTD - pttd.perennate.com");
		
		this.world = world;
		gameThread = new GameThread(world, this);
		panels = new HashMap<String, JPanel>();
	}
	
	public void init() {
		startPanel = new StartPanel(this);
		addScreen("start", startPanel);
		
		gamePanel = new GamePanel(world);
		gamePanel.init();
		addScreen("game", gamePanel);

		setScreen("start");
		pack();
		addWindowListener(this);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	public void addScreen(String name, JPanel pane) {
		panels.put(name, pane);
	}

	public void gameUpdated() {
		gamePanel.repaint();
	}
	
	public void startGame(String mode) {
		System.out.println("[PTFrame] Starting with mode: " + mode);
		
		world.init(mode);
		
		setScreen("game");
		gamePanel.requestFocus();
		gameThread.init();
	}
	
	public void setScreen(String name) {
		getContentPane().removeAll();
		getContentPane().add(panels.get(name));
		panels.get(name).revalidate();
		repaint();
		pack();
		requestFocus();
		
		System.out.println("[PTFrame] Set screen to " + name + " (" + panels.get(name) + ")");
		System.out.println("[PTFrame] Size: " + getSize() + ", " + panels.get(name).getSize());
	}

	public void windowClosed(WindowEvent e) {
		//shut down client and all
		gameThread.quit();
	}
	
	public void windowActivated(WindowEvent e) {
		repaint();
	}
	
	public void windowClosing(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
}
