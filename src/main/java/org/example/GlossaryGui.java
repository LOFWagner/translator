package org.example;

import com.deepl.api.GlossaryInfo;
import exceptions.EnvFileException;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

    public GlossaryGui() throws EnvFileException {
        glossaryManager = new GlossaryManager();
        setTitle("Create New Glossary");
        setSize(600, 400); // Adjusted size to accommodate larger display
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout()); // Changed to BorderLayout
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        glossaryNameField = new JTextField();
        selectedFileField = new JTextField();
        selectedFileField.setEditable(false);
        JButton fileSelectorButton = new JButton("Select Input File");
        inputFileLabel = new JLabel("No file selected");
        inputLanguageComboBox = new JComboBox<>(Constants.LANGUAGES);
        outputLanguageComboBox = new JComboBox<>(Constants.LANGUAGES);
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
                JFileChooser fileChooser = new JSystemFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files", "xlsx"));
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



        glossaryList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e) && !glossaryList.isSelectionEmpty() && glossaryList.locationToIndex(e.getPoint()) == glossaryList.getSelectedIndex()) {
                    JPopupMenu contextMenu = new JPopupMenu();
                    JMenuItem copyIdItem = new JMenuItem("Copy Glossary ID");
                    copyIdItem.addActionListener(event -> {
                        GlossaryInfo selectedGlossary = glossaryList.getSelectedValue();
                        if (selectedGlossary != null) {
                            StringSelection stringSelection = new StringSelection(selectedGlossary.getGlossaryId());
                            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
                        }
                    });
                    contextMenu.add(copyIdItem);
                    contextMenu.show(glossaryList, e.getX(), e.getY());
                }
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
        inputPanel.add(new JLabel("Glossary Name (can be empty):"));
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
        else {
            JOptionPane.showMessageDialog(GlossaryGui.this, "APIkey invalid or no glossaries found.");
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