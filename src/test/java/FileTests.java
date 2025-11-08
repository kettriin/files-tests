import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.lang.Double.parseDouble;
import static org.junit.jupiter.api.Assertions.*;

public class FileTests {
    private final ClassLoader cl = FileTests.class.getClassLoader();

    @Test
    @DisplayName("pdf файл из архива содержит весёлый текст")
    void pdfFileInZipHasFunnyText() throws Exception {
        boolean pdfFoundInZip = false;

        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("bizinfo.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("pdf.pdf")) {
                    pdfFoundInZip = true;
                    assertNotNull(entry);
                    byte[] pdfRead = zis.readAllBytes();
                    assertTrue(pdfRead.length > 0);
                    PDF pdf = new PDF(pdfRead);
                    String pdfText = pdf.text;
                    Assertions.assertTrue(pdfText.contains("Fun fun fun"),
                            "В 'pdf.pdf' не найден текст 'Fun fun fun'");
                }
            }
            assertTrue(pdfFoundInZip, "'pdf.pdf' не найден в архиве");
        }
    }

    @Test
    @DisplayName("xlsx файл из архива содержит номер телефона Мадонны")
    void xlsxFileInZipHasMadonnaPhoneNumber() throws Exception {
        boolean xlsxFoundInZip = false;

        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("bizinfo.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("xlsx.xlsx")) {
                    xlsxFoundInZip = true;
                    assertNotNull(entry);
                    byte[] xlsxRead = zis.readAllBytes();
                    assertTrue(xlsxRead.length > 0);
                    XLS xlsx = new XLS(xlsxRead);
                    String personName = xlsx.excel.getSheetAt(0).getRow(12).getCell(0).getStringCellValue();
                    String personPhone = xlsx.excel.getSheetAt(0).getRow(12).getCell(2).getStringCellValue();
                    Assertions.assertTrue(personName.contains("Madonna"),
                            "В файле нет пользователя Madonna");
                    Assertions.assertTrue(personPhone.contains("247-8172"),
                            "Номер для Madonna указан неверно");
                }
            }

            assertTrue(xlsxFoundInZip, "'xlsx.xlsx' не найден в архиве");
        }
    }

    @Test
    @DisplayName("Конечная стоимость товаров считается из суммы себестоимости и ндс")
    void totalCostEqualsTaxAndCostSumm() throws Exception {
        boolean csvFoundInZip = false;

        try (ZipInputStream zis = new ZipInputStream(
                cl.getResourceAsStream("bizinfo.zip")
        )) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().equals("taxables.csv")) {
                    csvFoundInZip = true;
                    assertNotNull(entry);

                    byte[] csvBuffer = zis.readAllBytes();
                    assertTrue(csvBuffer.length > 0);

                    String csvContent = new String(csvBuffer);
                    assertFalse(csvContent.trim().isEmpty());

                    try (CSVReader reader = new CSVReader(new InputStreamReader(
                            new java.io.ByteArrayInputStream(csvBuffer)))) {

                        List<String[]> data = reader.readAll();
                        Assertions.assertTrue(data.size() > 1,
                                "В CSV нет данных о стоимостях");
                        String[] firstDataRow = data.get(1);

                        double cost = parseDouble(firstDataRow[2].trim());
                        double tax = parseDouble(firstDataRow[3].trim());
                        double total = parseDouble(firstDataRow[4].trim());
                        double expectedTotal = cost + tax;

                        Assertions.assertEquals(expectedTotal, total,
                                "Налог расчитывается неверно");
                    }
                }
            }

            assertTrue(csvFoundInZip, "CSV файл не найден в архиве");
        }
    }
}