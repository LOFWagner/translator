package org.example;
import com.deepl.api.DeepLException;
import com.deepl.api.GlossaryEntries;
import com.deepl.api.GlossaryInfo;
import com.deepl.api.Translator;
import exceptions.EnvFileException;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlossaryManager {
    private Translator translator;

    public GlossaryManager() throws EnvFileException {
        File envFile = new File(".env");
        if (!envFile.exists()) {
            JOptionPane.showMessageDialog(null, ".env file not found. Please ensure the .env file is present in the application directory.", "Error", JOptionPane.ERROR_MESSAGE);
            throw new EnvFileException(".env file not found");

        }
        Dotenv dotenv = Dotenv.load();
        translator = new Translator(dotenv.get("DEEPL_API_KEY"));
    }


    private GlossaryInfo addGlossary(GlossaryEntries entryPairs, String name, String sourceLang, String targetLang) throws DeepLException, InterruptedException {
            GlossaryEntries entries = new GlossaryEntries(entryPairs);
            return translator.createGlossary(name, sourceLang, targetLang, entries);

    }

    public boolean deleteGlossary(String glossaryId) {
        try {
            translator.deleteGlossary(glossaryId);
        } catch (DeepLException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    public List<GlossaryInfo> getGlossaries() {
        try {
            return translator.listGlossaries();
        } catch (DeepLException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
    }

    public GlossaryInfo createNewGlossary(File file, String sourceLang, String targetLang, String name) {
        if (name == null || name.isEmpty()) {
            name = "Glossary";
        }
        try {
            if (file.getName().endsWith(".xlsx")) {
                File csvFile = convertXlsxToCSV(file);
                Map<String, String> entries = convertCsvToMap(csvFile);
                GlossaryEntries glossaryEntries = new GlossaryEntries(entries);
                return addGlossary(glossaryEntries, name, sourceLang, targetLang);
            } else if (file.getName().endsWith(".csv")) {
                Map<String, String> entries = convertCsvToMap(file);
                GlossaryEntries glossaryEntries = new GlossaryEntries(entries);
                return addGlossary(glossaryEntries, name, sourceLang, targetLang);
            } else {
                throw new IllegalArgumentException("File must be in .xlsx or .csv format");
            }
        } catch (DeepLException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to create glossary: " + e.getMessage(), e);
        }
    }

    private File convertXlsxToCSV(File xlsxFile) {
        try {
            FileInputStream file = new FileInputStream(xlsxFile);
            Workbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            File csvFile = new File(xlsxFile.getAbsolutePath() + ".csv");
            BufferedWriter bw = new BufferedWriter(new FileWriter(csvFile));

            for (Row row : sheet) {
                StringBuilder line = new StringBuilder();
                boolean hasContent = false;  // Flag to track if any cell has non-empty content
                String[] cellValues = new String[2];

                for (int i = 0; i < Math.min(2, row.getLastCellNum()); i++) {
                    if (row.getCell(i) != null) {
                        String cellValue = "";
                        if (row.getCell(i).getCellType() == CellType.STRING) {
                            cellValue = row.getCell(i).getStringCellValue().trim();
                        } else if (row.getCell(i).getCellType() == CellType.NUMERIC) {
                            cellValue = String.valueOf(row.getCell(i).getNumericCellValue()).trim();
                        }

                        cellValue = cellValue.replace("\"", "");
                        cellValues[i] = removeTrailingWhitespaces(cellValue);
                    }
                }

                // Check if both cells are non-empty
                if (cellValues[0] != null && !cellValues[0].isEmpty() && cellValues[1] != null && !cellValues[1].isEmpty()) {
                    line.append(cellValues[0]).append(",").append(cellValues[1]);
                    hasContent = true;
                }

                // Write the line if it contains non-empty content, to avoid empty lines or lines with only commas
                if (hasContent) {
                    bw.write(line.toString());
                    bw.newLine();
                    System.out.println(line.toString() + " \n");
                }
            }

            bw.close();
            return csvFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




    private Map<String, String> convertCsvToMap(File csvFile) {
        Map<String, String> map = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(csvFile));
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    map.put(parts[0], parts[1]);
                }
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    private String removeTrailingWhitespaces(String input) {
        if (input == null) {
            return null;
        }
        int end = input.length() - 1;
        while (end >= 0 && (Character.isWhitespace(input.charAt(end)) || input.charAt(end) == '\u00A0')) {
            end--;
        }
        return input.substring(0, end + 1);
    }

    public boolean deleteAllGlossaries() {
        List<GlossaryInfo> glossaries = getGlossaries();
        if (glossaries == null) {
            return false;
        }
        for (GlossaryInfo glossary : glossaries) {
            if (!deleteGlossary(glossary.getGlossaryId())) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) throws DeepLException, InterruptedException {
      //  GlossaryManager glossaryManager = new GlossaryManager();
      //  File input = new File("C:\\Users\\Louis\\Documents\\20240223_Vaillant multi_ENG_ESP.xlsx");
      //  GlossaryInfo myGlossary = glossaryManager.createNewGlossary(input, "EN", "ES");
      //  System.out.printf("Created '%s' (%s) %s->%s containing %d entries\n",
       //         myGlossary.getName(),
       //         myGlossary.getGlossaryId(),
       //         myGlossary.getSourceLang(),
       //         myGlossary.getTargetLang(),
       //         myGlossary.getEntryCount());
      //  System.out.println("Glossaries:" + glossaryManager.getGlossaries());

    }
}
