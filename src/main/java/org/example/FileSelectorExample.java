// FileSelectorExample.java
package org.example;

import com.deepl.api.GlossaryInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FileSelectorExample extends JFrame {

    public static final String SUCCESSFULLY = "Translated successfully";
    private JLabel inputFileLabel;
    private JLabel outputFileLabel;
    private JLabel progress;
    public final boolean html = true;
    private Translate t;
    private String[] languages = {"en", "de", "pl", "nl", "es", "da", "sv", "tr", "it", "fr"};
    private JComboBox<String> glossaryComboBox;
    private JCheckBox useGlossaryCheckBox;
    private GlossaryManager glossaryManager;

    public FileSelectorExample(MainMenuManager mainMenu) throws IOException {
        t = new Translate();
        glossaryManager = new GlossaryManager();
        initializeUIComponents();
        setupActionListeners();
        loadGlossaries();
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                mainMenu.setVisible(true);
            }
        });
    }

    private void initializeUIComponents() {
        setTitle("Translator v 0.1");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        JButton inputButton = new JButton("Select Input Folder");
        JButton outputButton = new JButton("Select Output Folder");

        inputFileLabel = new JLabel("No file selected");
        outputFileLabel = new JLabel("No file selected");

        JComboBox<String> languageComboBox = new JComboBox<>(languages);
        glossaryComboBox = new JComboBox<>();
        useGlossaryCheckBox = new JCheckBox("Use Glossary");

        JButton jb = new JButton("Translate to selected Language");
        JTextField jtf = new JTextField("replace with api-key");
        JButton trans_all = new JButton("Translate to all Languages");

        progress = new JLabel("Progress will show here");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1));
        panel.add(jtf);
        panel.add(jb);
        panel.add(inputButton);
        panel.add(inputFileLabel);
        panel.add(outputButton);
        panel.add(outputFileLabel);
        panel.add(trans_all);
        panel.add(progress);
        panel.add(useGlossaryCheckBox);
        panel.add(glossaryComboBox);
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(languageComboBox, BorderLayout.NORTH);
    }

    private void setupActionListeners() {
        JButton inputButton = (JButton) ((JPanel) getContentPane().getComponent(0)).getComponent(2);
        JButton outputButton = (JButton) ((JPanel) getContentPane().getComponent(0)).getComponent(4);
        JComboBox<String> languageComboBox = (JComboBox<String>) getContentPane().getComponent(1);
        JTextField jtf = (JTextField) ((JPanel) getContentPane().getComponent(0)).getComponent(0);
        JButton jb = (JButton) ((JPanel) getContentPane().getComponent(0)).getComponent(1);
        JButton trans_all = (JButton) ((JPanel) getContentPane().getComponent(0)).getComponent(6);

        inputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(FileSelectorExample.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    inputFileLabel.setText(filePath);
                }
            }
        });

        outputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showSaveDialog(FileSelectorExample.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                    outputFileLabel.setText(filePath);
                }
            }
        });

        jb.addActionListener(in -> handleTranslation(languageComboBox, jtf));

        trans_all.addActionListener(in -> handleTranslationForAllLanguages(languageComboBox, jtf));
    }

    private void loadGlossaries() {
        List<GlossaryInfo> glossaries = glossaryManager.getGlossaries();
        for (GlossaryInfo glossary : glossaries) {
            String displayText = String.format("%s (%s -> %s) - Created on: %s",
                    glossary.getName(),
                    glossary.getSourceLang(),
                    glossary.getTargetLang(),
                    glossary.getCreationTime().toString());
            glossaryComboBox.addItem(displayText);
        }
    }

    private void handleTranslation(JComboBox<String> languageComboBox, JTextField jtf) {
        try {
            System.out.println("Translate button clicked");
            if (!inputFileLabel.getText().equals("No file selected")) {
                File toTranslate = new File(inputFileLabel.getText());
                if (toTranslate.isDirectory()) {
                    File[] fileList = toTranslate.listFiles();
                    for (File file : fileList) {
                        if (!html) {
                            if (!file.isDirectory()) {
                                t.translate(file, (String) languageComboBox.getSelectedItem(), jtf.getText());
                            }
                        } else {
                            if (!file.isDirectory()) {
                                if (useGlossaryCheckBox.isSelected()) {
                                    t.translateHtmlWithGlossary(file, "en", (String) languageComboBox.getSelectedItem(), jtf.getText(), new File(outputFileLabel.getText()), progress, (String) glossaryComboBox.getSelectedItem());
                                } else {
                                    t.translateHtml(file, (String) languageComboBox.getSelectedItem(), jtf.getText(), new File(outputFileLabel.getText()), progress);
                                }
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(FileSelectorExample.this, SUCCESSFULLY);
                }
            } else {
                JOptionPane.showMessageDialog(FileSelectorExample.this, "Please enter File Path(s) / Api key");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(progress, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void handleTranslationForAllLanguages(JComboBox<String> languageComboBox, JTextField jtf) {
        try {
            if (!inputFileLabel.getText().equals("No file selected")) {
                File toTranslate = new File(inputFileLabel.getText());
                if (toTranslate.isDirectory()) {
                    File[] fileList = toTranslate.listFiles();
                    for (File file : fileList) {
                        for (String language : languages) {
                            if (useGlossaryCheckBox.isSelected()) {
                                t.translateHtmlWithGlossary(file, "en", language, jtf.getText(), new File(outputFileLabel.getText()), progress, (String) glossaryComboBox.getSelectedItem());
                            } else {
                                t.translateHtml(file, language, jtf.getText(), new File(outputFileLabel.getText()), progress);
                            }
                        }
                    }
                    progress.setText("Finished");
                    JOptionPane.showMessageDialog(FileSelectorExample.this, SUCCESSFULLY);
                }
                if (!html) {
                    t.translate(toTranslate, (String) languageComboBox.getSelectedItem(), jtf.getText());
                    JOptionPane.showMessageDialog(FileSelectorExample.this, SUCCESSFULLY);
                } else {
                    if (useGlossaryCheckBox.isSelected()) {
                        t.translateHtmlWithGlossary(toTranslate, "en", (String) languageComboBox.getSelectedItem(), jtf.getText(), new File(outputFileLabel.getText()), progress, (String) glossaryComboBox.getSelectedItem());
                    } else {
                        t.translateHtml(toTranslate, (String) languageComboBox.getSelectedItem(), jtf.getText(), new File(outputFileLabel.getText()), progress);
                    }
                    JOptionPane.showMessageDialog(FileSelectorExample.this, SUCCESSFULLY);
                }
            } else {
                JOptionPane.showMessageDialog(FileSelectorExample.this, "Please enter File Path(s) / Api key");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(progress, e.getMessage());
            throw new RuntimeException();
        }
    }
}