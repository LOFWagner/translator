package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.File;
import java.io.IOException;

import javax.swing.JLabel;

public class ManualTest {
    public static void main(String[] args) {
        Translate translator = new Translate();
        Dotenv dotenv = Dotenv.load();
        String apiKey = dotenv.get("DEEPL_API_KEY");
        File inputFile = new File("test_input.txt");
        File outputDir = new File("test_output");

        try {
            System.out.println("Translating to ES");
            translator.translateHtml(inputFile, "ES", apiKey, outputDir, new JLabel());
            System.out.println("Finished translating to ES");
        } catch (IOException e) {
            System.err.println("Error translating to ES: " + e.getMessage());
        }
    }
}
