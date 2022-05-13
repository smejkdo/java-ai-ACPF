package cz.cvut.fit.smejkdo1.bak.gui;

import javax.swing.*;

public class AcpfApplication {
    private static JFrame frame;

    public static void main(String[] args) {
        frame = new JFrame("ACPF menu");
        frame.add(new MenuPanel());
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }


    public static void exit() {
        frame.dispose();
        System.exit(0);
    }
}
