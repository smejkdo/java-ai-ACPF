package cz.cvut.fit.smejkdo1.bak.gui;

import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.InputStyle;
import cz.cvut.fit.smejkdo1.bak.acpf.machine.util.OutputStyle;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Objects;

public class MenuFMSelectionPanel extends JPanel {
    File selectedFMFile;
    String name;

    ButtonGroup FMTransitionGroup = new ButtonGroup();
    AbstractButton transitionListBtn = new JRadioButton("Transition List");
    AbstractButton intTreeBtn = new JRadioButton("Integer Tree");
    AbstractButton boolTreeBtn = new JRadioButton("Boolean Tree");

    ButtonGroup FMInputGroup = new ButtonGroup();
    AbstractButton infVicBtn = new JRadioButton("Informed vicinity");
    AbstractButton eightVicBtn = new JRadioButton("Eight vicinity");
    AbstractButton dataFromMapBtn = new JRadioButton("Data from map");

    ButtonGroup specifiedFMGroup = new ButtonGroup();
    AbstractButton bestBtn = new JRadioButton("Best");
    AbstractButton lastBtn = new JRadioButton("Last");
    AbstractButton specifyFMBtn = new JRadioButton(new AbstractAction("Specify location") {
        public void actionPerformed(ActionEvent event) {
            System.out.println(event.getActionCommand());

            JFileChooser fc = new JFileChooser("resources/FiniteAutomatons/");
            FileFilter filter = new FileNameExtensionFilter("Finite Automaton", "wad");
            fc.setFileFilter(filter);

            JFrame frame = new JFrame("Select file");
            frame.add(fc);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);

            int i = fc.showOpenDialog(MenuFMSelectionPanel.this);
            frame.dispose();
            if (i == JFileChooser.APPROVE_OPTION) {
                selectedFMFile = fc.getSelectedFile();
            }
        }
    });

    public MenuFMSelectionPanel(String name) {
        this.name = name;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JSplitPane split = new JSplitPane();
        JSplitPane rightSplit = new JSplitPane();
        JSplitPane bottomSplit = new JSplitPane();

        JPanel left = new JPanel();
        JPanel center = new JPanel();
        JPanel right = new JPanel();
        JPanel top = new JPanel();

        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));

        top.add(new JLabel(name));

        FMTransitionGroup.add(transitionListBtn);
        FMTransitionGroup.add(intTreeBtn);
        FMTransitionGroup.add(boolTreeBtn);
        left.add(transitionListBtn);
        left.add(intTreeBtn);
        left.add(boolTreeBtn);


        FMInputGroup.add(infVicBtn);
        FMInputGroup.add(eightVicBtn);
        FMInputGroup.add(dataFromMapBtn);
        center.add(infVicBtn);
        center.add(eightVicBtn);
        center.add(dataFromMapBtn);

        specifiedFMGroup.add(bestBtn);
        specifiedFMGroup.add(lastBtn);
        specifiedFMGroup.add(specifyFMBtn);
        right.add(bestBtn);
        right.add(lastBtn);
        right.add(specifyFMBtn);

        split.setOrientation(0);
        split.setTopComponent(top);
        split.setBottomComponent(bottomSplit);

        bottomSplit.setLeftComponent(left);
        bottomSplit.setRightComponent(rightSplit);
        rightSplit.setLeftComponent(center);
        rightSplit.setRightComponent(right);

        add(split);
    }

    public File getSelected() {
        if (specifiedFMGroup.getSelection() == null
                || FMInputGroup.getSelection() == null
                || FMTransitionGroup == null) {
            return null;
        }
        if (specifyFMBtn.isSelected())
            return selectedFMFile;
        StringBuilder fileLocation = new StringBuilder();
        fileLocation.append("resources/FiniteAutomatons/");
        if (bestBtn.isSelected()) {
            fileLocation.append("Best/");
            if (transitionListBtn.isSelected())
                fileLocation.append(OutputStyle.TRANSITION_LIST).append("_");
            else if (intTreeBtn.isSelected())
                fileLocation.append(OutputStyle.INT_TREE).append("_");
            else if (boolTreeBtn.isSelected())
                fileLocation.append(OutputStyle.BOOL_TREE).append("_");

            if (infVicBtn.isSelected())
                fileLocation.append(InputStyle.INFORMED_VICINITY).append(".wad");
            else if (eightVicBtn.isSelected())
                fileLocation.append(InputStyle.EIGHT_VICINITY).append(".wad");
            else if (dataFromMapBtn.isSelected())
                fileLocation.append(InputStyle.DATA_FROM_MAP).append(".wad");

            File file = new File(fileLocation.toString());
            if (file.exists())
                return file;
            else
                return null;
        }
        if (lastBtn.isSelected()) {
            fileLocation.append("Evolved/");
            if (infVicBtn.isSelected())
                fileLocation.append(InputStyle.INFORMED_VICINITY).append("/");
            else if (eightVicBtn.isSelected())
                fileLocation.append(InputStyle.EIGHT_VICINITY).append("/");
            else if (dataFromMapBtn.isSelected())
                fileLocation.append(InputStyle.DATA_FROM_MAP).append("/");

            if (transitionListBtn.isSelected())
                fileLocation.append(OutputStyle.TRANSITION_LIST);
            else if (intTreeBtn.isSelected())
                fileLocation.append(OutputStyle.INT_TREE);
            else if (boolTreeBtn.isSelected())
                fileLocation.append(OutputStyle.BOOL_TREE);

            File folder = new File(fileLocation.toString());
            if (!folder.exists()) {
                folder.mkdirs();
                return null;
            }
            if (!folder.isDirectory())
                return null;
            File[] folders = folder.listFiles();
            if (folders == null || folders.length == 0)
                return null;
            String[] fileNames = null;
            int i = folders.length - 1;
            for (; i >= 0; i--) {
                fileNames = folders[i].list();
                if (fileNames != null && fileNames.length > 0) {
                    if (Objects.requireNonNull(
                            new File(
                                    folders[i].getPath()
                                            + "/" + fileNames[fileNames.length - 1])
                                    .list()).length > 0)
                        break;
                }
            }
            if (fileNames == null || fileNames.length == 0)
                return null;
            String pathName = folders[i].getPath() + "/" + fileNames[fileNames.length - 1];
            File lastGeneration = new File(pathName);
            if (!lastGeneration.exists())
                return null;
            String[] lastGenerationFileList = lastGeneration.list();
            assert lastGenerationFileList != null;
            pathName = folders[i].getPath() + "/" + fileNames[fileNames.length - 1]
                    + "/" + lastGenerationFileList[lastGenerationFileList.length - 1];
            File file = new File(pathName);
            if (file.exists())
                return file;
            else
                return null;
        }

        return null;
    }
}
