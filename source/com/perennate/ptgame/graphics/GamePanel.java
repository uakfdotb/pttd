package com.perennate.ptgame.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import com.perennate.ptgame.PTGame;
import com.perennate.ptgame.world.Point;
import com.perennate.ptgame.world.Projectile;
import com.perennate.ptgame.world.Tower;
import com.perennate.ptgame.world.TowerCost;
import com.perennate.ptgame.world.Unit;
import com.perennate.ptgame.world.World;

public class GamePanel extends JPanel implements MouseListener, KeyListener {
	public static int WIDTH = 900;
	public static int HEIGHT = 450;
	
	World world;
	Map<String, TowerInfo> towerInfoMap;
	Tower selectedTower;
	TowerInfo selectedTowerInfo;
	
	boolean cheater = false; //whether or not this guy used cheats
	
	List<PanelButton> currentButtons; //for selected target
	
	public GamePanel(World world) {
		this.world = world;
		towerInfoMap = new HashMap<String, TowerInfo>();
		currentButtons = new ArrayList<PanelButton>();
		
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		addMouseListener(this);
		addKeyListener(this);
	}
	
	public void init() {
		try {
			BufferedReader in;
			
			if(PTGame.appletLocation == null) {
				in = new BufferedReader(new FileReader("data/tower_info.txt"));
			} else {
				URL targetURL = new URL(PTGame.appletLocation, "data/tower_info.txt");
				in = new BufferedReader(new InputStreamReader(targetURL.openStream()));
			}
			
			String line;
			
			while((line = in.readLine()) != null) {
				String[] parts = line.split(" ");
				
				if(parts.length >= 5) {
					towerInfoMap.put(parts[0], new TowerInfo(parts[0], parts[1], parts[2], parts[3], parts[4]));
				} else {
					System.out.println("[GamePanel] Warning: invalid tower info skipped: " + line);
				}
			}
		} catch(IOException ioe) {
			System.out.println("[GamePanel] Tower info error: " + ioe.getLocalizedMessage());
		}
	}
	
	public void paintComponent(Graphics g_) {
		//long startTime = System.currentTimeMillis();
		
		super.paintComponent(g_);
		Graphics2D g = (Graphics2D) g_;
		
		AffineTransform savedTransform = g.getTransform();
		AffineTransform scaleTransform = AffineTransform.getScaleInstance((double) getWidth() / WIDTH, (double) getHeight() / HEIGHT);
		g.transform(scaleTransform);
		
		//fill white
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		//print towers
		synchronized(world) {
			Iterator<Tower> towers = world.getTowers();
			
			while(towers.hasNext()) {
				Tower tower = towers.next();
				TowerInfo towerInfo = towerInfoMap.get(tower.getName());
				
				if(towerInfo != null) {
					g.setColor(towerInfo.color);
					
					if(tower.isUpgrading()) { 
						g.setColor(Color.BLACK);
					}
					
					g.fillRect((int) (tower.getLocation().x - Tower.WIDTH / 2.0), (int) (tower.getLocation().y - Tower.WIDTH / 2.0), Tower.WIDTH, Tower.WIDTH);
					g.setColor(Color.BLACK);
					g.drawRect((int) (tower.getLocation().x - Tower.WIDTH / 2.0), (int) (tower.getLocation().y - Tower.WIDTH / 2.0), Tower.WIDTH, Tower.WIDTH);
					g.drawString(towerInfo.element, (float) (tower.getLocation().x - Tower.WIDTH / 2.0 + 3), (float) (tower.getLocation().y - Tower.WIDTH / 2.0 + 12));
				
					if(tower.getType() == Tower.TYPE_REGION && !tower.isUpgrading()) {
						//draw region if timer is low
						if(tower.getTimeSinceLastDamage() < 800) {
							g.setColor(Color.RED);
							g.drawOval((int) (tower.getLocation().x - tower.getRange()),
									(int) (tower.getLocation().y - tower.getRange()),
									(int) (tower.getRange() * 2),
									(int) (tower.getRange() * 2));
						}
					}
				}
			}
		}
		
		//print units
		synchronized(world) {
			Iterator<Unit> units = world.getUnits();
			
			while(units.hasNext()) {
				Unit unit = units.next();
				
				g.setColor(Color.RED);
				
				if(unit.getType() == Unit.TYPE_AQUATIC) {
					g.setColor(Color.BLUE);
				} else if(unit.getType() == Unit.TYPE_DARK){
					g.setColor(Color.BLACK);
				}
				
				g.fillOval((int) (unit.getLocation().x - unit.getWidth() / 2.0), (int) (unit.getLocation().y - unit.getHeight() / 2.0), unit.getWidth(), unit.getHeight());
			
				Iterator<Projectile> projectiles = unit.getProjectiles();
				
				while(projectiles.hasNext()) {
					Projectile projectile = projectiles.next();
					g.setColor(Color.BLACK);
					g.drawOval((int) (projectile.getLocation().x - 2), (int) (projectile.getLocation().y - 2), 4, 4);
				}
			}
		}
		
		//print buttons
		for(PanelButton button : currentButtons) {
			g.setColor(Color.GREEN);
			g.fillRect((int) (button.p.x - button.width / 2), (int) (button.p.y - button.height / 2), button.width, button.height);
			
			g.setColor(Color.BLACK);
			for(int i = 0; i < button.text.length; i++) {
				g.drawString(button.text[i],
						(int) (button.p.x - button.width / 2 + 3),
						(int) (button.p.y - button.height / 2 + 12 + i * 12));
			}
		}
		
		//print information
		g.setColor(Color.BLACK);
		g.drawString("Life: " + world.getLife(), 10, 400);
		g.drawString("Quarks: " + world.getQuarks(), 10, 412);
		g.drawString("Time: " + world.getTime(), 10, 424);
		g.drawString("Next wave: " + world.getNextWave(), 10, 436);
		
		g.drawString("Wave " + world.getWaveNumber() + " of " + world.getWaveTotal(), 300, 140);
		
		if(cheater) {
			g.drawString("You are a cheater!", 300, 152);
		}
		
		if(selectedTower != null) {
			g.drawString("Selected: " + selectedTowerInfo.element, 757, 400);
			g.drawString("Name: " + selectedTowerInfo.elementName, 757, 412);
			g.drawString("Atomic number: " + selectedTowerInfo.atomicNumber, 757, 424);
		}
		
		//print win/loss
		int result = world.getResultStatus();

		if(result != 0) {
			g.setFont(new Font("Arial", Font.BOLD, 200));
			
			if(result == -1) {
				g.setColor(Color.RED);
				g.drawString("Defeated", 20, 300);
			} else if(result == 1) {
				g.setColor(Color.BLUE);
				
				if(cheater) {
					g.drawString("Cheater", 120, 300);
				} else {
					g.drawString("Victory", 120, 300);
				}
			}
		}
		
		g.setTransform(savedTransform);
		
		//System.out.println("[GamePanel] Time: " + (System.currentTimeMillis() - startTime));
	}
	
	private void clearSelection() {
		//clear any current selections
		selectedTower = null;
		selectedTowerInfo = null;
		currentButtons.clear();
	}
	
	public void selectButton(PanelButton button) {
		if(button.action == PanelButton.ACTION_UPGRADE) {
			synchronized(world) {
				selectedTower.upgrade(button.actionString);
			}
		}
		
		clearSelection();
	}
	
	public void mousePressed(MouseEvent e) {
		//request focus just in case
		requestFocus();
		
		double x = e.getX() * ((double) WIDTH / getWidth());
		double y = e.getY() * ((double) HEIGHT / getHeight());
		Point p = new Point(x, y);

		if(selectedTower != null) {
			//check if user pressed one of the buttons
			for(PanelButton button : currentButtons) {
				if(button.isPressed(p)) {
					selectButton(button);
					return;
				}
			}
		}
		
		//clear any current selections
		clearSelection();
		
		//check if user selected a tower
		synchronized(world) {
			Iterator<Tower> towers = world.getTowers();
			
			while(towers.hasNext()) {
				Tower tower = towers.next();
				if(Point.isPointContained(p, tower.getLocation(), Tower.WIDTH, true)) {
					selectedTower = tower;
					selectedTowerInfo = towerInfoMap.get(selectedTower.getName());
					
					if(selectedTowerInfo == null) {
						System.out.println("[GamePanel] Error: unknown tower " + selectedTower.getName() + "!");
						return;
					}
					
					Point panelPoint = new Point(665, 404);
					int panelWidth = 160;
					int panelHeight = 28;
					
					//now we have to update buttons
					for(TowerCost upgrade : selectedTower.getUpgrades()) {
						Tower upgradeTower = world.factory.get(upgrade.towerName);
						TowerInfo upgradeTowerInfo = towerInfoMap.get(upgrade.towerName);
						String towerIdentifier = upgrade.towerName;
						
						if(upgradeTowerInfo != null) {
							towerIdentifier = upgradeTowerInfo.elementName;
						}
						
						String[] text = {"Upgrade to " + towerIdentifier, "Cost: " + upgrade.quarks};
						int action = PanelButton.ACTION_UPGRADE;
						String actionString = upgradeTower.getName();
						
						currentButtons.add(new PanelButton(text, action, actionString, new Point(panelPoint), panelWidth, panelHeight));
						
						panelPoint.x -= panelWidth + 2;
						
						if(panelPoint.x < 80) {
							panelPoint.x = 665;
							panelPoint.y = 434;
						}
					}
					
					break;
				}
			}
		}
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_U && !currentButtons.isEmpty()) {
			//select first button
			selectButton(currentButtons.get(0));
		} else if(e.getKeyCode() == KeyEvent.VK_P || e.getKeyCode() == KeyEvent.VK_ESCAPE) {
			synchronized(world) {
				world.togglePause();
			}
		} else if(e.getKeyCode() == KeyEvent.VK_SPACE) {
			synchronized(world) {
				int time = world.getNextWave() - 1;
				
				for(int i = 0; i < time * 100; i++) {
					world.update(10);
				}
			}
		} else if(e.getKeyCode() == KeyEvent.VK_F1) {
			synchronized(world) {
				cheater = true;
				world.cheat();
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
}

class TowerInfo {
	String name;
	String element;
	Color color;
	String elementName;
	String atomicNumber;
	
	public TowerInfo(String name, String element, String color, String elementName, String atomicNumber) {
		this.name = name;
		this.element = element;
		
		this.color = Color.WHITE;
		
		if(color.equals("blue")) {
			this.color = Color.BLUE;
		} else if(color.equals("pink")) {
			this.color = Color.PINK;
		} else if(color.equals("red")) {
			this.color = Color.RED;
		} else if(color.equals("teal")) {
			this.color = Color.CYAN;
		} else if(color.equals("yellow")) {
			this.color = Color.YELLOW;
		} else if(color.equals("grey")) {
			this.color = Color.GRAY;
		} else if(color.equals("brown")) {
			this.color = new Color(251, 185, 23);
		} else if(color.equals("orange")) {
			this.color = Color.ORANGE;
		} else if(color.equals("lgrey")) {
			this.color = Color.LIGHT_GRAY;
		} else if(color.equals("purple")) {
			this.color = new Color(160, 92, 240);
		} else if(color.equals("gold")) {
			this.color = new Color(212, 175, 55);
		} else if(color.equals("green")) {
			this.color = Color.GREEN;
		}
		
		this.elementName = elementName;
		this.atomicNumber = atomicNumber;
	}
}

class PanelButton {
	public static int ACTION_UPGRADE = 0;
	
	String[] text;
	int action;
	String actionString;
	
	Point p;
	int width;
	int height;
	
	public PanelButton(String[] text, int action, String actionString, Point p, int width, int height) {
		this.text = text;
		this.action = action;
		this.actionString = actionString;
		this.p = p;
		this.width = width;
		this.height = height;
	}
	
	public boolean isPressed(Point x) {
		return Point.isPointContainedRect(x, p, width, height);
	}
}