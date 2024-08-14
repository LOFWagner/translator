package org.example;

public class Test {
    public static void main(String[] args) throws Exception {
        Api api = new Api();
        System.out.println(api.translate_with_glossary("TEST_TEXT is quite important in todays time", "es", "71ffef99-ab23-9460-532d-0b0103fc249b:fx", "4fc45e43-b863-48e2-b927-a61c09ded0a4", "en"));

    }
}
