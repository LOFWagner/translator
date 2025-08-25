# Translation Tool

Welcome to the **Translation Tool** repository! This tool is designed to help you translate both text files (`.txt`) and HTML files (`.html`) in bulk, all within a single folder. It also includes a powerful glossary manager to maintain consistent translations for specific terms.

## Features

-   **Batch Translation**: Translate all text and HTML files within a specified folder to multiple languages.
-   **Glossary Manager**: Create, manage, and use glossaries to ensure consistent translations of specific terms.
-   **File Type Support**: Translates `.txt` and `.html` files. Glossaries can be imported from `.xlsx` and `.csv` files.
-   **Easy Setup**: Simple configuration using a single `.env` file for your DeepL API key.
-   **Simple UI**: An intuitive graphical user interface for easy operation.

## Installation

### Option 1: Download the Latest Release

1.  Download the latest release from the [Releases](https://github.com/yourusername/translation-tool/releases) page on GitHub.
2.  Create an `.env` file in the same directory as the `.jar` file with your DeepL API key. This file is required for the tool to function properly.

    **Example `.env` file:**

    ```
    DEEPL_API_KEY=your_api_key_here
    ```

3.  Run the tool by double-clicking the `.jar` file or using the command:

    ```bash
    java -jar translation-tool.jar
    ```

### Option 2: Compile from Source

1.  Clone the repository:

    ```bash
    git clone https://github.com/yourusername/translation-tool.git
    cd translation-tool
    ```

2.  Compile the project using Maven:

    ```bash
    mvn clean package
    ```

3.  Create an `.env` file in the root directory with your DeepL API key.

    **Example `.env` file:**

    ```
    DEEPL_API_KEY=your_api_key_here
    ```

4.  Run the compiled tool from the `target` directory:

    ```bash
    java -jar target/translator-1.1.8-jar-with-dependencies.jar
    ```

## Usage

The application starts with a main menu where you can choose between the "Translator" and the "Glossary Manager".

### Glossary Manager

The Glossary Manager allows you to create and manage your glossaries for use in translations.

**1. Creating a New Glossary:**

*   **Glossary Name:** You can provide an optional name for your glossary. If you leave it blank, it will be named "Glossary".
*   **Input File:** Select an Excel (`.xlsx`) or CSV (`.csv`) file containing your glossary terms.
    *   **Format:** The file should have two columns. The first column contains the source term, and the second column contains the corresponding target term. The application will read from the first sheet of an Excel file.
*   **Languages:** Select the source and target languages for your glossary from the dropdown menus.
*   Click the "Create Glossary" button. The new glossary will appear in the "Existing Glossaries" list.

**2. Managing Existing Glossaries:**

*   **List:** The "Existing Glossaries" list shows all the glossaries you have created, along with their creation dates.
*   **Delete:** You can select a glossary from the list and click the "Delete Selected Glossary" button to remove it.
*   **Copy ID:** You can right-click on a glossary in the list to copy its unique ID to your clipboard.

### Translator

The Translator is where you perform the bulk translation of your files.

**1. Standard Translation:**

*   Select an input folder containing the `.txt` or `.html` files you want to translate.
*   Select an output folder where the translated files will be saved.
*   Choose a target language from the dropdown menu.
*   Click "Translate to selected Language" to translate all files in the input folder to the selected language.
*   Alternatively, click "Translate to all Languages" to translate the files into all available languages.

**2. Translation with a Glossary:**

*   Select your input and output folders as you would for a standard translation.
*   Check the "Use Glossary" checkbox.
*   Select the desired glossary from the dropdown list that appears.
*   **Important:** You must also select the correct source language for the glossary you are using.
*   Choose your target language and click "Translate to selected Language".
*   **Note:** The "Translate to all Languages" option is disabled when using a glossary.
