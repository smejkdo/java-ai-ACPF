package cz.cvut.fit.smejkdo1.bak.gui;

import cz.cvut.fit.smejkdo1.bak.acpf.game.GameInstance;
import cz.cvut.fit.smejkdo1.bak.acpf.game.GameTurn;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class MenuPanel extends JPanel {
    File selectedMapFile;

    private JSplitPane selections = new JSplitPane();

    private MenuFMSelectionPanel redSelection = new MenuFMSelectionPanel("RED");
    private MenuFMSelectionPanel bluSelection = new MenuFMSelectionPanel("BLU");
    private JButton exitBtn = new JButton(new AbstractAction("Exit") {
        public void actionPerformed(ActionEvent event) {
            AcpfApplication.exit();
        }
    });
    private JTextField mapNumTxt = new JTextField();
    private JButton startBtn = new JButton(new AbstractAction("Start") {
        public void actionPerformed(ActionEvent event) {
            MenuPanel.this.start();
        }
    });


    AbstractButton specifyMapBtn = new JRadioButton(new AbstractAction("Specify location") {
        public void actionPerformed(ActionEvent event) {
            System.out.println(event.getActionCommand());

            JFileChooser fc = new JFileChooser("resources/Maps/");
            FileFilter filter = new FileNameExtensionFilter("Map", "map");
            fc.setFileFilter(filter);

            JFrame frame = new JFrame("Select file");
            frame.add(fc);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);

            int i = fc.showOpenDialog(MenuPanel.this);
            frame.dispose();
            if (i == JFileChooser.APPROVE_OPTION) {
                selectedMapFile = fc.getSelectedFile();
            }
        }
    });

    public MenuPanel() {
        selections.setOrientation(0);
        selections.setTopComponent(redSelection);
        selections.setBottomComponent(bluSelection);
        mapNumTxt.setMaximumSize(new Dimension(100, 20));

        JSplitPane selectionsAndMenu = new JSplitPane();
        selectionsAndMenu.setLeftComponent(selections);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.add(new JLabel("Map number:"));
        menuPanel.add(mapNumTxt);
        menuPanel.add(specifyMapBtn);
        menuPanel.add(startBtn);
        menuPanel.add(exitBtn);

        selectionsAndMenu.setRightComponent(menuPanel);

        add(selectionsAndMenu);
    }

    public void start() {
        double width = 1800;
        double height = 1000;
        int mapNumber;
        try {
            mapNumber = Integer.decode(mapNumTxt.getText());
        } catch (Exception e) {
            e.printStackTrace();
            ErrorWindow.build("Map number " + mapNumTxt.getText() + " is invalid.");
            return;
        }

        File redFM = redSelection.getSelected();
        File bluFM = bluSelection.getSelected();
        if (redFM == null) {
            ErrorWindow.build("Select all needed options in selection of red FM.");
            return;
        }
        if (bluFM == null) {
            ErrorWindow.build("Select all needed options in selection of blu FM.");
            return;
        }
        GameTurn gameTurn;
        if (selectedMapFile == null)
            gameTurn = GameInstance.createGameInstance(mapNumber, redFM, bluFM);
        else
            gameTurn = GameInstance.createGameInstance(
                    selectedMapFile.getName()
                            .substring(0, selectedMapFile.getName()
                                    .length() - 4), mapNumber, redFM, bluFM);

        MapPanel panel = new MapPanel(gameTurn);
        panel.setPreferredSize(new Dimension(((int) width), ((int) height)));
        panel.cellSize = (int) Math.min(
                width / (gameTurn.getGameMap().getMapSize().y + 1),
                height / (gameTurn.getGameMap().getMapSize().x + 1));

        ButtonPanel buttons = new ButtonPanel(panel);
        buttons.setPreferredSize(new Dimension(150, 200));
        JPanel jPanel = new JPanel();
        jPanel.add(panel, BorderLayout.CENTER);
        jPanel.add(buttons, BorderLayout.EAST);

        JFrame acpfFrame = new JFrame("ACPF");
        acpfFrame.add(jPanel);
        acpfFrame.setLocationRelativeTo(null);
        acpfFrame.pack();
        acpfFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        acpfFrame.setVisible(true);
    }
}
