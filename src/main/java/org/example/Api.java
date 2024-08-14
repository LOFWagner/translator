package org.example;


import com.deepl.api.TextTranslationOptions;
import com.deepl.api.Translator;

public class Api {
    Translator translator;
    TextTranslationOptions normal_config_with_glossary;
    TextTranslationOptions html_config_with_glossary;
    public Api(){
    }
    public String translate(String s, String lang, String authKey) throws Exception {
        translator = new Translator(authKey);
        String result = translator.translateText(s, null, lang).getText();
        return result;
    }

    public String translateHtml(String s, String lang, String authKey) throws Exception {
        TextTranslationOptions doc_html = new TextTranslationOptions();
        doc_html.setTagHandling("html");
        translator = new Translator(authKey);
        String result = translator.translateText(s, null, lang, doc_html).getText();

        return result;
    }

    public String translate_with_glossary(String s, String lang, String authKey, String glossaryId, String source_lang) throws Exception {
        translator = new Translator(authKey);
        normal_config_with_glossary = new TextTranslationOptions();
        normal_config_with_glossary.setGlossaryId(glossaryId);
        String result = translator.translateText(s, source_lang, lang, normal_config_with_glossary).getText();
        return result;
    }

    public String translateHtml_with_glossary(String s, String lang, String authKey, String glossaryId, String source_lang) throws Exception {
        translator = new Translator(authKey);
        html_config_with_glossary = new TextTranslationOptions();
        html_config_with_glossary.setGlossaryId(glossaryId);
        String result = translator.translateText(s, null, lang, html_config_with_glossary).getText();
        return result;
    }

}
