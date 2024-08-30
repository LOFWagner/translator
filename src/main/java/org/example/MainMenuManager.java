package org.example;

import exceptions.EnvFileException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class MainMenuManager extends JFrame {
    private JButton translatorButton;
    private JButton glossaryManagerButton;

    public MainMenuManager() {
        setTitle("Welcome to the Application");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(2, 1));
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
        translatorButton = new JButton("Translator");
        glossaryManagerButton = new JButton("Glossary Manager");

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        translatorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    openTranslator();
                } catch (IOException | EnvFileException ex) {
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

    private void openTranslator() throws IOException, EnvFileException {
        setVisible(false);
        TranslatorGui translator = new TranslatorGui(this);
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

        GlossaryGui glossaryGui = null;
        try {
            glossaryGui = new GlossaryGui();
            glossaryGui.setVisible(true);
            glossaryGui.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    setVisible(true);
                }
            });
        } catch (EnvFileException e) {
            setVisible(true);
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
