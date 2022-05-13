package cz.cvut.fit.smejkdo1.bak.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ButtonPanel extends JPanel {
    private MapPanel mapPanel;
    JButton stepBtn = new JButton(new AbstractAction("Make step") {
        public void actionPerformed(ActionEvent event) {
            mapPanel.step();
        }
    });
    JButton animateBtn = new JButton(new AbstractAction("Start animation") {
        public void actionPerformed(ActionEvent event) {
            mapPanel.animate();
        }
    });
    JButton stopBtn = new JButton(new AbstractAction("Stop animation") {
        public void actionPerformed(ActionEvent event) {
            mapPanel.animating = false;
        }
    });
    JButton resetBtn = new JButton(new AbstractAction("Reset") {
        public void actionPerformed(ActionEvent event) {
            mapPanel.reset();
        }
    });

    public ButtonPanel(MapPanel mapPanel) {
        this.mapPanel = mapPanel;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(stepBtn);
        add(animateBtn);
        add(stopBtn);
        add(resetBtn);
    }
}
