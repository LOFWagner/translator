package org.example;

import com.deepl.api.DeepLException;
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
        FileReader fr = null;
        BufferedReader br = null;
        String r = null;

        File out = new File(input.getAbsolutePath() + "_translated");

        System.out.println(out.getAbsolutePath());

        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        if (!out.exists()) {
            out.createNewFile();
        }
        try {
            fr = new FileReader(input);
            br = new BufferedReader(fr);

            String temp;
            String translate;
            while ((temp = br.readLine()) != null) {
                bw.write(temp);
                bw.newLine();
                bw.write(br.readLine());
                bw.newLine();
                translate = " ";
                while ((temp = br.readLine()).trim().length() != 0) {
                    translate += temp;
                }
                translate = api.translate(translate, lang, apiKey);
                bw.write(translate);
                bw.newLine();
                bw.newLine();
                translate = " ";
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        } catch (DeepLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            br.close();
            bw.close();
        }
        return out;
    }

    ;

    public void translateHtml(File input, String lang, String apiKey, File output, JLabel progress) throws IOException {
        Api api = new Api();
        FileReader fr = null;
        BufferedReader br = null;
        String r = null;
        Boolean newDir = new File(output.getPath() + File.separator + "translated_" + lang).mkdir();
        File out = new File(output.getPath() + File.separator + "translated_" + lang + File.separator + input.getAbsoluteFile().getName());
        if (!out.exists() && !out.isDirectory()) {
            out.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(out));
            try {
                progress.setText("Working on " + input.getName());
                fr = new FileReader(input);
                br = new BufferedReader(fr);
                String toTranslate = "";
                String translated = "";
                String temp = "";
                while ((temp = br.readLine()) != null) {
                    toTranslate += temp + "\n";
                }
                translated = api.translateHtml(toTranslate, lang, apiKey);
                bw.write(translated);
            } catch (DeepLException e) {
                progress.setText(e.getMessage());
                JOptionPane.showMessageDialog(progress,  e.getMessage());
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                JOptionPane.showMessageDialog(progress,  e.getMessage());
                progress.setText(e.getMessage());
                throw new RuntimeException(e);
            } finally {
                br.close();
                bw.close();
            }
        } else {
            if (!already_trans) {
                JOptionPane.showMessageDialog(progress, "Already translated this to " + lang + ", this message will only be shown once!");

                already_trans = true;
            }
        }

    }
}

