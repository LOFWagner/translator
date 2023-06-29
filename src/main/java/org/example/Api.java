package org.example;

import com.deepl.api.DeepLException;
import com.deepl.api.Formality;
import com.deepl.api.TextTranslationOptions;
import com.deepl.api.Translator;
import java.io.File;
import java.io.IOException;

public class Api {

    //Key: 71ffef99-ab23-9460-532d-0b0103fc249b:fx
    Translator translator;
    TextTranslationOptions doc;
    public Api(){
         doc = new TextTranslationOptions();
        doc.setTagHandling("html");
    }
    public String translate(String s, String lang, String authKey) throws DeepLException, InterruptedException {
        translator = new Translator(authKey);
        System.out.println("In: " + s);
        String result = translator.translateText(s, null, lang).getText();
        System.out.println("Out " + result);
        return result;
    }

    public String translateHtml(String s, String lang, String authKey) throws DeepLException, InterruptedException {
        translator = new Translator(authKey);
        String result = translator.translateText(s, null, lang,doc).getText();
        return result;
    }


}
