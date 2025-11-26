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
    private String[] targetLanguages = Constants.TRANSLATION_TARGET_LANGUAGES;
    private JComboBox<String> glossaryComboBox;
    private JCheckBox useGlossaryCheckBox;
    private JComboBox<String> sourceLanguageComboBox;
    private GlossaryManager glossaryManager;
    private JLabel sourceLanguageLabel;
    private JLabel throbberLabel;
    private JLabel buffer;
    private Map<String, GlossaryInfo> glossaryMap;
    private JButton jb;
    private JButton inputButton;
    private JButton outputButton;
    private JComboBox<String> languageComboBox;
    private JButton trans_all;
    private JCheckBox htmlCheckBox;
    private JLabel targetLanguageLabel;
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

        inputButton = new JButton("Select Input Folder");
        outputButton = new JButton("Select Output Folder");
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

        languageComboBox = new JComboBox<>(targetLanguages);
        glossaryComboBox = new JComboBox<>();
        glossaryComboBox.setVisible(false);
        useGlossaryCheckBox = new JCheckBox("Use Glossary");
        sourceLanguageComboBox = new JComboBox<>(Constants.TRANSLATION_SOURCE_LANGUAGES);
        sourceLanguageComboBox.setVisible(false);
        sourceLanguageLabel = new JLabel("Source Language:");
        sourceLanguageLabel.setVisible(false);
        jb = new JButton("Translate to selected Language");
        trans_all = new JButton("Translate to all Languages");
        htmlCheckBox = new JCheckBox("HTML translation mode");
        htmlCheckBox.setSelected(true);
        progress = new JLabel("Progress will show here");
        targetLanguageLabel = new JLabel("Target Language:");

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Throbber
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(throbberLabel, gbc);

        // Input Folder
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(inputButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(inputFileLabel, gbc);

        // Output Folder
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(outputButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(outputFileLabel, gbc);

        // Target Language
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(targetLanguageLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(languageComboBox, gbc);

        // Source Language
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(sourceLanguageLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        panel.add(sourceLanguageComboBox, gbc);

        // Use Glossary
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(useGlossaryCheckBox, gbc);

        gbc.gridx = 1;
        gbc.gridy = 5;
        panel.add(glossaryComboBox, gbc);

        // HTML Checkbox
        gbc.gridx = 0;
        gbc.gridy = 6;
        panel.add(htmlCheckBox, gbc);

        // Translate Buttons
        gbc.gridx = 0;
        gbc.gridy = 7;
        panel.add(jb, gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        panel.add(trans_all, gbc);

        // Progress
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        panel.add(progress, gbc);

        getContentPane().add(panel, BorderLayout.CENTER);
    }

    private void setupActionListeners() {
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

        useGlossaryCheckBox.addActionListener(e -> {
            boolean selected = useGlossaryCheckBox.isSelected();
            sourceLanguageComboBox.setVisible(selected);
            sourceLanguageLabel.setVisible(selected);
            glossaryComboBox.setVisible(selected);
            languageComboBox.setEnabled(!selected);
            sourceLanguageComboBox.setEnabled(!selected);
            if (selected) {
                updateLanguagesFromGlossary();
            }
        });

        glossaryComboBox.addActionListener(e -> {
            if (useGlossaryCheckBox.isSelected()) {
                updateLanguagesFromGlossary();
            }
        });

        jb.addActionListener(in -> {
            throbberLabel.setVisible(true);
            handleTranslation();
            throbberLabel.setVisible(false);
        });

        trans_all.addActionListener(in -> {
            if (useGlossaryCheckBox.isSelected()) {
                JOptionPane.showMessageDialog(TranslatorGui.this, "Translate to all languages is disabled when using a glossary.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            throbberLabel.setVisible(true);
            handleTranslationForAllLanguages();
            throbberLabel.setVisible(false);
        });
    }

    private void updateLanguagesFromGlossary() {
        String selectedGlossary = (String) glossaryComboBox.getSelectedItem();
        GlossaryInfo glossaryInfo = glossaryMap.get(selectedGlossary);
        if (glossaryInfo != null) {
            sourceLanguageComboBox.setSelectedItem(glossaryInfo.getSourceLang());
            languageComboBox.setSelectedItem(glossaryInfo.getTargetLang());
        }
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

    private void handleTranslation() {
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

    private void handleTranslationForAllLanguages() {
        try {
            if (!inputFileLabel.getText().equals("No file selected")) {
                String apiKey = Dotenv.load().get("DEEPL_API_KEY");
                File toTranslate = new File(inputFileLabel.getText());
                if (toTranslate.isDirectory()) {
                    File[] fileList = toTranslate.listFiles();
                    for (File file : fileList) {
                        for (String language : targetLanguages) {
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