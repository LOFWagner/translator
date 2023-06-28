package org.example;

import com.deepl.api.DeepLException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Translate {

    public File translate(File input, String outPath, String lang, String apiKey) throws IOException {
        Api api = new Api();
        FileReader fr = null;
        BufferedReader br = null;
        String r = null;
        File out = new File(outPath);
        System.out.println(out.getAbsolutePath());
        System.out.println(outPath);
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

    public File translateHtml(File input, String outPath, String lang, String apiKey) throws IOException {
        Api api = new Api();
        FileReader fr = null;
        BufferedReader br = null;
        String r = null;
        File out = new File(outPath);
        System.out.println(out.getAbsolutePath());
        System.out.println(outPath);
        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        if (!out.exists()) {
            out.createNewFile();
        }
        try {
            fr = new FileReader(input);
            br = new BufferedReader(fr);
            String toTranslate = "";
            String translated = "";
            String temp = "";
            while ((temp = br.readLine()) != null){
                toTranslate += temp + "\n";
            }
            translated = api.translateHtml(toTranslate,lang,apiKey);
            bw.write(translated);
            return out;
        } catch (DeepLException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            br.close();
            bw.close();
        }
    }
}
