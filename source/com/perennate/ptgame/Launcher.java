package com.perennate.ptgame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class Launcher extends JPanel implements ActionListener {
    JButton launch;
    
    public Launcher() {
        super();
        
        launch = new JButton("Launch pttd!");
        launch.addActionListener(this);
        add(launch);
    }
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == launch) {
            PTGame.main(new String[] {});
        }
    }    
}