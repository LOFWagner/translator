package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainMenuManager extends JFrame {
    private JButton translatorButton;
    private JButton glossaryManagerButton;

    public MainMenuManager() {
        setTitle("Main Menu");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1));

        translatorButton = new JButton("Translator");
        glossaryManagerButton = new JButton("Glossary Manager");

        translatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openTranslator();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        glossaryManagerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openGlossaryManager();
            }
        });

        add(translatorButton);
        add(glossaryManagerButton);
    }

    private void openTranslator() throws IOException {
        setVisible(false);
        FileSelectorExample translator = new FileSelectorExample(this);
        translator.setVisible(true);
        translator.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                setVisible(true);
            }
        });
    }

    private void openGlossaryManager() {
        setVisible(false);
        GlossaryGui glossaryGui = new GlossaryGui();
        glossaryGui.setVisible(true);
        glossaryGui.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                setVisible(true);
            }
        });
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