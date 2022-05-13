package cz.cvut.fit.smejkdo1.bak.gui;

import javax.swing.*;

public class ErrorWindow {
    public static void build(String message) {
        JFrame frame = new JFrame("Error");
        frame.add(new JLabel(message));
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
