package com.perennate.ptgame.graphics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

public class StartPanel extends JPanel implements ActionListener {
	PTFrame frame;
	JComboBox modeBox;
	JButton startButton;
	
	public StartPanel(PTFrame frame) {
		super();
		this.frame = frame;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		modeBox = new JComboBox(new String[] {"newbie", "easy", "normal", "hard", "impossible"});
		modeBox.setEditable(false);
		
		startButton = new JButton("Start");
		startButton.addActionListener(this);
		
		add(modeBox);
		add(startButton);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == startButton) {
			frame.startGame(modeBox.getSelectedItem().toString());
		}
	}
}
