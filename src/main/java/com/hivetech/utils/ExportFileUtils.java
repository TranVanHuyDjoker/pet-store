package com.hivetech.utils;

import com.hivetech.model.dto.PetDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
public class ExportFileUtils {

    private final List<PetDTO> pets;

    public void exportPFDFile(HttpServletResponse response) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();
        Font titleFont = FontFactory.getFont(FontFactory.defaultEncoding);
        titleFont.setSize(18);

        document.add(new Paragraph("Danh sách thú cưng", titleFont));

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[]{1.0f, 4.0f, 2.5f, 2.5f, 3.0f, 3.0f});
        table.setSpacingBefore(10);

        //HEADER

        PdfPCell cell = new PdfPCell();
        cell.setPhrase(new Phrase("ID"));
        table.addCell(cell);
        cell.setPhrase(new Phrase("TÊN"));
        table.addCell(cell);
        cell.setPhrase(new Phrase("DANH MỤC"));
        table.addCell(cell);
        cell.setPhrase(new Phrase("TÌNH TRẠNG"));
        table.addCell(cell);
        cell.setPhrase(new Phrase("NGÀY THÊM"));
        table.addCell(cell);
        cell.setPhrase(new Phrase("NGÀY SỬA"));
        table.addCell(cell);

        //DATA
        for (PetDTO pet : pets) {
            table.addCell(String.valueOf(pet.getId()));
            table.addCell(pet.getName());
            table.addCell(pet.getCategory().getName());
            table.addCell(pet.getCreateAt().toString());
            table.addCell(pet.getUpdateAt().toString());
        }

        document.add(table);
        document.close();
    }

    public void exportExcelFile(HttpServletResponse response) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pets");
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight(16);
        style.setFont(font);
        Row row = sheet.createRow(0);

        createCell(row, 0, "ID", style, sheet);
        createCell(row, 1, "TÊN", style, sheet);
        createCell(row, 2, "DANH MỤC", style, sheet);
        createCell(row, 3, "TÌNH TRẠNG", style, sheet);
        createCell(row, 4, "NGÀY THÊM", style, sheet);
        createCell(row, 5, "NGÀY SỬA", style, sheet);
        writeDataLines(workbook, sheet);
        workbook.write(response.getOutputStream());

        workbook.close();
    }

    private void createCell(Row row, int columnCount, Object value, CellStyle style, Sheet sheet) {
        sheet.autoSizeColumn(columnCount);
        org.apache.poi.ss.usermodel.Cell cell = row.createCell(columnCount);
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        } else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        } else {
            cell.setCellValue((String) value);
        }
        cell.setCellStyle(style);
    }

    private void writeDataLines(XSSFWorkbook workbook, Sheet sheet) {
        int rowCount = 1;
        CellStyle style = workbook.createCellStyle();
        XSSFFont font = workbook.createFont();
        font.setFontHeight(14);
        style.setFont(font);

        for (PetDTO pet : pets) {
            Row row = sheet.createRow(rowCount++);
            int columnCount = 0;

            createCell(row, columnCount++, pet.getId(), style, sheet);
            createCell(row, columnCount++, pet.getName(), style, sheet);
            createCell(row, columnCount++, pet.getCategory().getName(), style, sheet);
            createCell(row, columnCount++, pet.getCreateAt().toString(), style, sheet);
            createCell(row, columnCount++, pet.getUpdateAt().toString(), style, sheet);
        }
    }
}
