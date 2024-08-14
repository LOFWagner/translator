package org.example;

import com.deepl.api.GlossaryInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;
import java.util.List;

public class GlossaryGui extends JFrame {
    private JLabel inputFileLabel;
    private JTextField glossaryNameField;
    private JTextField selectedFileField;
    private JComboBox<String> inputLanguageComboBox;
    private JComboBox<String> outputLanguageComboBox;
    private JLabel throbberLabel;
    private JList<GlossaryInfo> glossaryList;
    private DefaultListModel<GlossaryInfo> glossaryListModel;
    private GlossaryManager glossaryManager;
    private JButton deleteGlossaryButton;

    public GlossaryGui() {
        glossaryManager = new GlossaryManager();
        setTitle("Create New Glossary");
        setSize(600, 400); // Adjusted size to accommodate larger display
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout()); // Changed to BorderLayout

        glossaryNameField = new JTextField();
        selectedFileField = new JTextField();
        selectedFileField.setEditable(false);
        JButton fileSelectorButton = new JButton("Select Input File");
        inputFileLabel = new JLabel("No file selected");
        inputLanguageComboBox = new JComboBox<>(new String[]{"en", "de", "pl", "nl", "es", "da", "sv", "tr", "it", "fr"});
        outputLanguageComboBox = new JComboBox<>(new String[]{"en", "de", "pl", "nl", "es", "da", "sv", "tr", "it", "fr"});
        JButton createGlossaryButton = new JButton("Create Glossary");

        // Load throbber GIF
        URL throbberUrl = getClass().getResource("/cropped.gif");
        if (throbberUrl != null) {
            throbberLabel = new JLabel(new ImageIcon(throbberUrl));
        } else {
            throbberLabel = new JLabel("Loading...");
            System.err.println("Throbber GIF not found at /translator/main/resources/throbber_13.gif");
        }
        throbberLabel.setVisible(false);

        glossaryListModel = new DefaultListModel<>();
        glossaryList = new JList<>(glossaryListModel);
        glossaryList.setCellRenderer(new GlossaryListCellRenderer());
        deleteGlossaryButton = new JButton("Delete Selected Glossary");

        fileSelectorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(GlossaryGui.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    inputFileLabel.setText(selectedFile.getAbsolutePath());
                    selectedFileField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        createGlossaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String glossaryName = glossaryNameField.getText();
                String inputFilePath = inputFileLabel.getText();
                String sourceLang = (String) inputLanguageComboBox.getSelectedItem();
                String targetLang = (String) outputLanguageComboBox.getSelectedItem();

                if (inputFilePath.equals("No file selected") || sourceLang.isEmpty() || targetLang.isEmpty()) {
                    JOptionPane.showMessageDialog(GlossaryGui.this, "Please fill in all fields.");
                    return;
                }

                File inputFile = new File(inputFilePath);
                throbberLabel.setVisible(true);
                new SwingWorker<GlossaryInfo, Void>() {
                    @Override
                    protected GlossaryInfo doInBackground() throws Exception {
                        return glossaryManager.createNewGlossary(inputFile, sourceLang, targetLang, glossaryName);
                    }

                    @Override
                    protected void done() {
                        throbberLabel.setVisible(false);
                        try {
                            GlossaryInfo glossary = get();
                            glossaryListModel.addElement(glossary);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(GlossaryGui.this, "Error creating glossary: " + ex.getMessage());
                        }
                    }
                }.execute();
            }
        });

        deleteGlossaryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GlossaryInfo selectedGlossary = glossaryList.getSelectedValue();
                if (selectedGlossary != null) {
                    int confirm = JOptionPane.showConfirmDialog(GlossaryGui.this, "Are you sure you want to delete the selected glossary?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (glossaryManager.deleteGlossary(selectedGlossary.getGlossaryId())) {
                            glossaryListModel.removeElement(selectedGlossary);
                        } else {
                            JOptionPane.showMessageDialog(GlossaryGui.this, "Error deleting glossary.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(GlossaryGui.this, "Please select a glossary to delete.");
                }
            }
        });

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Glossary Name:"));
        inputPanel.add(glossaryNameField);
        inputPanel.add(fileSelectorButton);
        inputPanel.add(selectedFileField);
        inputPanel.add(new JLabel("Input Language:"));
        inputPanel.add(inputLanguageComboBox);
        inputPanel.add(new JLabel("Output Language:"));
        inputPanel.add(outputLanguageComboBox);
        inputPanel.add(createGlossaryButton);
        inputPanel.add(throbberLabel);

        JPanel glossaryPanel = new JPanel(new BorderLayout());
        glossaryPanel.add(new JLabel("Existing Glossaries:"), BorderLayout.NORTH);
        glossaryPanel.add(new JScrollPane(glossaryList), BorderLayout.CENTER);
        glossaryPanel.add(deleteGlossaryButton, BorderLayout.SOUTH);

        add(inputPanel, BorderLayout.NORTH);
        add(glossaryPanel, BorderLayout.CENTER);

        updateGlossaryList();

    }

    private void updateGlossaryList() {
        glossaryListModel.clear();
        List<GlossaryInfo> glossaries = glossaryManager.getGlossaries();
        if (glossaries != null) {
            for (GlossaryInfo glossary : glossaries) {
                glossaryListModel.addElement(glossary);
            }
        }
    }

    private class GlossaryListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (c instanceof JLabel && value instanceof GlossaryInfo) {
                GlossaryInfo glossary = (GlossaryInfo) value;
                ((JLabel) c).setText(glossary.getName() + " (Created on: " + glossary.getCreationTime() + ")");
                int textWidth = c.getFontMetrics(c.getFont()).stringWidth(((JLabel) c).getText());
                if (textWidth > list.getFixedCellWidth()) {
                    list.setFixedCellWidth(textWidth + 20); // Add some padding
                }
            }
            return c;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainMenuManager().setVisible(true);
            }
        });
    }
}