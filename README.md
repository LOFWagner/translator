# Translation Tool

Welcome to the **Translation Tool** repository! This tool is designed to help you translate both text files (`.txt`) and HTML files (`.html`) in bulk, all within a single folder. It also includes a glossary manager to maintain consistent translations for specific terms.

## Features

- **Batch Translation**: Translate all text and HTML files within a specified folder.
- **Glossary Manager**: Maintain a glossary of terms to ensure consistent translations.
- **File Type Support**: Automatically detects and translates `.txt` and `.html` files.
- **Easy Setup**: Simple configuration using an `.env` file.

## Installation

### Option 1: Download the Latest Release

1. Download the latest release from the [Releases](https://github.com/yourusername/translation-tool/releases) page on GitHub.
2. Create an `.env` file in the same directory as the `.jar` file with the necessary environment variables. This file is required for the tool to function properly.

    **Example `.env` file:**

    ```
    API_KEY=your_api_key_here
    ```

3. Install Java 17, preferrably corretto from this link:

    ```
    https://corretto.aws/downloads/latest/amazon-corretto-17-x64-windows-jdk.msi
    ```

4. Run the tool using the command:

    ```bash
    java -jar translation-tool.jar
    ```


### Option 2: Compile from Source

1. Clone the repository:

    ```bash
    git clone https://github.com/yourusername/translation-tool.git
    cd translation-tool
    ```

2. Compile the project using Maven:

    ```bash
    mvn clean package
    ```

3. Create an `.env` file in the root directory with the necessary environment variables. This file is required for the tool to function properly.

    **Example `.env` file:**

    ```
    API_KEY=your_api_key_here
    ```

4. Run the compiled tool using the command, or by simply double clicking it:

    ```bash
    java -jar target/translation-tool.jar
    ```

## Usage

1. Place the files you want to translate into the selected `input` folder.

2. Add any specific terms you want to manage to the glossary, which currently supports the `.xlsx` file format.

3. Run the translation tool:

    ```bash
    java -jar translation-tool.jar

    or just double click
    ```

4. The translated files will be output to the selected `output` folder.
