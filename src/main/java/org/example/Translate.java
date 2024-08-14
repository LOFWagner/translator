package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

public class Translate {

    boolean already_trans = false;

    public File translate(File input, String lang, String apiKey) throws IOException {
        Api api = new Api();
        File out = new File(input.getAbsolutePath() + "_translated.txt");
        if (!out.exists()) {
            BufferedWriter bw = null;
            FileReader fr = null;
            BufferedReader br = null;
            try {
                bw = new BufferedWriter(new FileWriter(out));
                fr = new FileReader(input);
                br = new BufferedReader(fr);

                String temp;
                StringBuilder toTranslate = new StringBuilder();

                while ((temp = br.readLine()) != null) {
                    toTranslate.append(temp).append("\n");
                }

                String translated = api.translate(toTranslate.toString(), lang, apiKey);
                bw.write(translated);
            } catch (Exception e) {
                if (out.exists()) {
                    out.delete();
                }
                throw new RuntimeException(e);
            } finally {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
                if (bw != null) {
                    bw.close();
                }
            }
        } else {
            System.out.println("File already translated.");
        }

        return out;
    }

    ;

    public void translateHtml(File input, String lang, String apiKey, File output, JLabel progress) throws IOException {
        System.out.println("File to be translated: " + input.getAbsolutePath());
        if (!input.isDirectory()) {
            Api api = new Api();
            FileReader fr = null;
            BufferedReader br = null;
            BufferedWriter bw = null;
            File out = new File(output.getPath() + File.separator + "translated_" + lang + File.separator + input.getName());

            try {
                if (!out.exists()) {
                    out.getParentFile().mkdirs();
                    bw = new BufferedWriter(new FileWriter(out));
                    fr = new FileReader(input);
                    br = new BufferedReader(fr);

                    progress.setText("Working on " + input.getName());
                    StringBuilder toTranslate = new StringBuilder();
                    String temp;

                    while ((temp = br.readLine()) != null) {
                        toTranslate.append(temp).append("\n");
                    }

                    String translated = api.translateHtml(toTranslate.toString(), lang, apiKey);
                    bw.write(translated);
                } else {
                    if (!already_trans) {
                        JOptionPane.showMessageDialog(progress, "Already translated this to " + lang + ", this message will only be shown once!");
                        already_trans = true;
                    }
                }
            } catch (Exception e) {
                if (out.exists()) {
                    out.delete();
                }
                progress.setText(e.getMessage());
                JOptionPane.showMessageDialog(progress, e.getMessage());
                throw new RuntimeException(e);
            } finally {
                if (br != null) {
                    br.close();
                }
                if (fr != null) {
                    fr.close();
                }
                if (bw != null) {
                    bw.close();
                }
            }
        }
    }
}

