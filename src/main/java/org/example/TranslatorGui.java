package org.example;

import com.deepl.api.GlossaryInfo;
import exceptions.EnvFileException;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class TranslatorGui extends JFrame {

    public static final String SUCCESSFULLY = "Translated successfully";
    private JLabel inputFileLabel;
    private JLabel outputFileLabel;
    private JLabel progress;
    public boolean html = true;
    private Translate t;
    private String[] languages = {"en-us", "de", "pl", "nl", "es", "da", "sv", "tr", "it", "fr"};
    private JComboBox<String> glossaryComboBox;
    private JCheckBox useGlossaryCheckBox;
    private JComboBox<String> sourceLanguageComboBox;
    private GlossaryManager glossaryManager;
    private JLabel sourceLanguageLabel;
    private JLabel throbberLabel;
    private JLabel buffer;
    private Map<String, GlossaryInfo> glossaryMap;
    JButton jb;
    public TranslatorGui(MainMenuManager mainMenu) throws IOException {
        glossaryMap = new HashMap<>();
        t = new Translate();

        try {
            glossaryManager = new GlossaryManager();
        } catch (EnvFileException e) {
            JOptionPane.showMessageDialog(null, ".env file not found. Please ensure the .env file is present in the application directory.", "Error", JOptionPane.ERROR_MESSAGE);
            mainMenu.setVisible(true);
        }

        initializeUIComponents();
        setupActionListeners();
        loadGlossaries(mainMenu);
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
        setSize(850, 400);
        setLocationRelativeTo(null);

        JButton inputButton = new JButton("Select Input Folder");
        JButton outputButton = new JButton("Select Output Folder");
        inputFileLabel = new JLabel("No file selected");
        outputFileLabel = new JLabel("No file selected");
        buffer = new JLabel(" ");
        URL throbberUrl = getClass().getResource("/cropped.gif");
        if (throbberUrl != null) {
            throbberLabel = new JLabel(new ImageIcon(throbberUrl));
        } else {
            throbberLabel = new JLabel("Loading...");
            System.err.println("Throbber GIF not found at /translator/main/resources/throbber_13.gif");
        }
        throbberLabel.setVisible(false);

        JComboBox<String> languageComboBox = new JComboBox<>(languages);
        glossaryComboBox = new JComboBox<>();
        glossaryComboBox.setVisible(false);
        useGlossaryCheckBox = new JCheckBox("Use Glossary");
        sourceLanguageComboBox = new JComboBox<>(languages);
        sourceLanguageComboBox.setVisible(false);
        sourceLanguageLabel = new JLabel("Source Language:");
        sourceLanguageLabel.setVisible(false);
        jb = new JButton("Translate to selected Language");
        JButton trans_all = new JButton("Translate to all Languages");
        JCheckBox htmlCheckBox = new JCheckBox("HTML translation mode");
        htmlCheckBox.setSelected(true);
        progress = new JLabel("Progress will show here");
        JLabel targetLanguageLabel = new JLabel("Target Language:");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(11, 1)); // Adjusted to 11 rows after removing API key field
        panel.add(throbberLabel);
        panel.add(inputButton);
        panel.add(inputFileLabel);
        panel.add(outputButton);
        panel.add(outputFileLabel);
        panel.add(trans_all);
        panel.add(progress);
        panel.add(targetLanguageLabel);
        panel.add(languageComboBox);
        panel.add(jb);
        panel.add(buffer);
        panel.add(useGlossaryCheckBox);
        panel.add(glossaryComboBox);
        panel.add(sourceLanguageLabel);
        panel.add(sourceLanguageComboBox);
        panel.add(htmlCheckBox);
        getContentPane().add(panel, BorderLayout.CENTER);
    }

    private void setupActionListeners() {
        JPanel panel = (JPanel) getContentPane().getComponent(0);
        JLabel throbberLabel = (JLabel) panel.getComponent(0);
        JButton inputButton = (JButton) panel.getComponent(1);
        JLabel inputFileLabel = (JLabel) panel.getComponent(2);
        JButton outputButton = (JButton) panel.getComponent(3);
        JLabel outputFileLabel = (JLabel) panel.getComponent(4);
        JButton trans_all = (JButton) panel.getComponent(5);
        JLabel progress = (JLabel) panel.getComponent(6);
        JLabel targetLanguageLabel = (JLabel) panel.getComponent(7);
        JComboBox<String> languageComboBox = (JComboBox<String>) panel.getComponent(8);
        JButton jb = (JButton) panel.getComponent(9);
        JCheckBox useGlossaryCheckBox = (JCheckBox) panel.getComponent(11);
        JComboBox<String> glossaryComboBox = (JComboBox<String>) panel.getComponent(12);
        JLabel sourceLanguageLabel = (JLabel) panel.getComponent(13);
        JComboBox<String> sourceLanguageComboBox = (JComboBox<String>) panel.getComponent(14);
        JCheckBox htmlCheckBox = (JCheckBox) panel.getComponent(15);
        inputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JSystemFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                int returnValue = fileChooser.showOpenDialog(TranslatorGui.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    inputFileLabel.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        outputButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JSystemFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fileChooser.setAcceptAllFileFilterUsed(false);
                int returnValue = fileChooser.showSaveDialog(TranslatorGui.this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    outputFileLabel.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        htmlCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                html = htmlCheckBox.isSelected();
            }
        });

        useGlossaryCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean selected = useGlossaryCheckBox.isSelected();
                sourceLanguageComboBox.setVisible(selected);
                sourceLanguageLabel.setVisible(selected);
                glossaryComboBox.setVisible(selected);
            }
        });

        jb.addActionListener(in -> {
            throbberLabel.setVisible(true);
            handleTranslation(languageComboBox);
            throbberLabel.setVisible(false);
        });

        trans_all.addActionListener(in -> {
            if (useGlossaryCheckBox.isSelected()) {
                JOptionPane.showMessageDialog(TranslatorGui.this, "Translate to all languages is disabled when using a glossary.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            throbberLabel.setVisible(true);
            handleTranslationForAllLanguages(languageComboBox);
            throbberLabel.setVisible(false);
        });
    }

    private void loadGlossaries(MainMenuManager mainMenu) {
        List<GlossaryInfo> glossaries = glossaryManager.getGlossaries();
        try {
            for (GlossaryInfo glossary : glossaries) {
                String displayText = String.format("%s (%s -> %s) - Created on: %s",
                        glossary.getName(),
                        glossary.getSourceLang(),
                        glossary.getTargetLang(),
                        glossary.getCreationTime().toString());
                glossaryComboBox.addItem(displayText);
                glossaryMap.put(displayText, glossary);
            }
        } catch (NullPointerException e) {
            JOptionPane.showMessageDialog(TranslatorGui.this, "APIkey invalid", "Error", JOptionPane.ERROR_MESSAGE);
            mainMenu.setVisible(true);
        }
    }

    private void handleTranslation(JComboBox<String> languageComboBox) {
        try {
            System.out.println("Translate button clicked");
            if (!inputFileLabel.getText().equals("No file selected")) {
                String apiKey = Dotenv.load().get("DEEPL_API_KEY");
                File toTranslate = new File(inputFileLabel.getText());
                if (toTranslate.isDirectory()) {
                    File[] fileList = toTranslate.listFiles();
                    for (File file : fileList) {
                        if (!html) {
                            if (!file.isDirectory()) {
                                t.translate(file, (String) languageComboBox.getSelectedItem(), apiKey);
                            }
                        } else {
                            if (!file.isDirectory()) {
                                if (useGlossaryCheckBox.isSelected()) {
                                    String selectedGlossary = (String) glossaryComboBox.getSelectedItem();
                                    String sourceLang = (String) sourceLanguageComboBox.getSelectedItem();
                                    GlossaryInfo glossaryInfo = glossaryMap.get(selectedGlossary);
                                    if (glossaryInfo != null) {
                                        String glossaryId = glossaryInfo.getGlossaryId();
                                        t.translateHtmlWithGlossary(file, sourceLang, (String) languageComboBox.getSelectedItem(), apiKey, new File(outputFileLabel.getText()), progress, glossaryId);
                                    } else {
                                        t.translateHtmlWithGlossary(file, sourceLang, (String) languageComboBox.getSelectedItem(), apiKey, new File(outputFileLabel.getText()), progress, (String) glossaryComboBox.getSelectedItem());
                                    }
                                } else {
                                    t.translateHtml(file, (String) languageComboBox.getSelectedItem(), apiKey, new File(outputFileLabel.getText()), progress);
                                }
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(TranslatorGui.this, SUCCESSFULLY);
                }
            } else {
                JOptionPane.showMessageDialog(TranslatorGui.this, "Please enter File Path(s)");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(progress, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void handleTranslationForAllLanguages(JComboBox<String> languageComboBox) {
        try {
            if (!inputFileLabel.getText().equals("No file selected")) {
                String apiKey = Dotenv.load().get("DEEPL_API_KEY");
                File toTranslate = new File(inputFileLabel.getText());
                if (toTranslate.isDirectory()) {
                    File[] fileList = toTranslate.listFiles();
                    for (File file : fileList) {
                        for (String language : languages) {
                            if (useGlossaryCheckBox.isSelected()) {
                                String sourceLang = (String) sourceLanguageComboBox.getSelectedItem();
                                String selectedGlossary = (String) glossaryComboBox.getSelectedItem();
                                GlossaryInfo glossaryInfo = glossaryMap.get(selectedGlossary);
                                if (glossaryInfo != null) {
                                    String glossaryId = glossaryInfo.getGlossaryId();
                                    t.translateHtmlWithGlossary(file, sourceLang, language, apiKey, new File(outputFileLabel.getText()), progress, glossaryId);
                                }
                            } else {
                                t.translateHtml(file, language, apiKey, new File(outputFileLabel.getText()), progress);
                            }
                        }
                    }
                    progress.setText("Finished");
                    JOptionPane.showMessageDialog(TranslatorGui.this, SUCCESSFULLY);
                }
                if (!html) {
                    t.translate(toTranslate, (String) languageComboBox.getSelectedItem(), apiKey);
                    JOptionPane.showMessageDialog(TranslatorGui.this, SUCCESSFULLY);
                } else {
                    if (useGlossaryCheckBox.isSelected()) {
                        String sourceLang = (String) sourceLanguageComboBox.getSelectedItem();
                        if (sourceLang == null) {
                            JOptionPane.showMessageDialog(TranslatorGui.this, "Please select a source language for glossary mode.");
                            return;
                        }
                        t.translateHtmlWithGlossary(toTranslate, sourceLang, (String) languageComboBox.getSelectedItem(), apiKey, new File(outputFileLabel.getText()), progress, (String) glossaryComboBox.getSelectedItem());
                    } else {
                        t.translateHtml(toTranslate, (String) languageComboBox.getSelectedItem(), apiKey, new File(outputFileLabel.getText()), progress);
                    }
                    JOptionPane.showMessageDialog(TranslatorGui.this, SUCCESSFULLY);
                }
            } else {
                JOptionPane.showMessageDialog(TranslatorGui.this, "Please enter File Path(s)");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(progress, e.getMessage());
            throw new RuntimeException();
        }
    }
}