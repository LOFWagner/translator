package org.example;

import java.io.File;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileSelectorExample extends JFrame {

    private JLabel inputFileLabel;
    private JLabel outputFileLabel;

    public final boolean html = true;

    public FileSelectorExample() throws IOException {
        setTitle("File Selector Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 200);
        setLocationRelativeTo(null);

        // Create file selector buttons
        JButton inputButton = new JButton("Select Input File");
        JButton outputButton = new JButton("Select Output File");

        // Create labels to display selected file paths
        inputFileLabel = new JLabel("No file selected");
        outputFileLabel = new JLabel("No file selected");

        // Create language selection combo box
        String[] languages = {"en-US", "de", "pl", "nl", "es", "da", "sv", "tr", "it", "fr"};
        JComboBox<String> languageComboBox = new JComboBox<>(languages);

        // Create action listeners for file selectors
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

        Translate t = new Translate();
        JButton jb = new JButton("Translate");
        JTextField jtf = new JTextField("replace with api-key");
        jb.addActionListener(in -> {
            try {
                if (inputFileLabel.getText() != "No file selected") {
                    File toTranslate = new File(inputFileLabel.getText());
                    if (toTranslate.isDirectory()) {
                        File[] fileList = toTranslate.listFiles();
                        for (int i = 0; i < fileList.length; i++) {
                            if (!html) {
                                t.translate(fileList[i],
                                    (String) languageComboBox.getSelectedItem(),
                                    jtf.getText());

                            } else {
                                t.translateHtml(fileList[i],
                                    (String) languageComboBox.getSelectedItem(),
                                    jtf.getText(), new File(outputFileLabel.getText()));


                            }


                        }
                        JOptionPane.showMessageDialog(FileSelectorExample.this, "Translated successfully");
                    }
                    if (!html) {
                        t.translate(toTranslate,
                            (String) languageComboBox.getSelectedItem(),
                            jtf.getText());
                        JOptionPane.showMessageDialog(FileSelectorExample.this, "Translated successfully");
                    } else {
                        t.translateHtml(toTranslate,
                            (String) languageComboBox.getSelectedItem(),
                            jtf.getText(), new File(outputFileLabel.getText()));
                        JOptionPane.showMessageDialog(FileSelectorExample.this, "Translated successfully");

                    }
                } else {
                    JOptionPane.showMessageDialog(FileSelectorExample.this, "Please enter File Path(s) / Api key");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        JButton trans_all = new JButton("Translate to all Languages");
        trans_all.addActionListener(in -> {
            try {

                if (inputFileLabel.getText() != "No file selected") {
                    File toTranslate = new File(inputFileLabel.getText());
                    if (toTranslate.isDirectory()) {
                        File[] fileList = toTranslate.listFiles();
                        for (int i = 0; i < fileList.length; i++) {
                            for (int h = 0; h < languages.length; h++) {
                                t.translateHtml(fileList[i],
                                    languages[h],
                                    jtf.getText(), new File(outputFileLabel.getText()));

                            }
                        }
                        JOptionPane.showMessageDialog(FileSelectorExample.this, "Translated successfully");
                    }
                    if (!html) {
                        t.translate(toTranslate,
                            (String) languageComboBox.getSelectedItem(),
                            jtf.getText());
                        JOptionPane.showMessageDialog(FileSelectorExample.this, "Translated successfully");
                    } else {
                        t.translateHtml(toTranslate,
                            (String) languageComboBox.getSelectedItem(),
                            jtf.getText(), new File(outputFileLabel.getText()));
                        JOptionPane.showMessageDialog(FileSelectorExample.this, "Translated successfully");

                    }
                } else {
                    JOptionPane.showMessageDialog(FileSelectorExample.this, "Please enter File Path(s) / Api key");
                }
            } catch (IOException e) {
                throw new RuntimeException();
            }
        });
        // Create layout
        JPanel panel = new JPanel();
        panel.add(jtf);
        panel.setLayout(new GridLayout(5, 1));
        panel.add(inputButton);
        panel.add(inputFileLabel);
        panel.add(outputButton);
        panel.add(outputFileLabel);

        panel.add(jb);
        panel.add(trans_all);
        getContentPane().add(panel, BorderLayout.CENTER);
        getContentPane().add(languageComboBox, BorderLayout.NORTH);


    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new FileSelectorExample().setVisible(true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
