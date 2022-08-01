package com.hivetech.controller;

import com.hivetech.model.dto.ReportOrderForMonth;
import com.hivetech.model.dto.ReportOrderForQuarter;
import com.hivetech.model.dto.ReportOrderForWeek;
import com.hivetech.model.dto.ReportOrderForYear;
import com.hivetech.service.OrderReport;
import com.hivetech.service.OrderService;
import com.lowagie.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReportController {
    private final OrderService orderService;

    @GetMapping(value = "/api/v1/order/report/month")
    public ResponseEntity<List<ReportOrderForMonth>> getReportForMonth() {
        try {
            return ResponseEntity.ok(orderService.getOrderForMonth());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(value = "/api/v1/order/report/year")
    public ResponseEntity<List<ReportOrderForYear>> getReportForYear() {
        try {
            return ResponseEntity.ok(orderService.getOrderForYear());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(value = "/api/v1/order/report/quarter")
    public ResponseEntity<List<ReportOrderForQuarter>> getReportForQuarter() {
        try {
            return ResponseEntity.ok(orderService.getOrderForQuarter());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(value = "/api/v1/order/report/week")
    public ResponseEntity<List<ReportOrderForWeek>> getReportForWeek() {
        try {
            return ResponseEntity.ok(orderService.getOrderForWeek());
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping(value = "/admin/report")
    public String getPieChart(Model model) {
        model.addAttribute("pieChartData", orderService.getOrderForMonth());
        return "chart/google-charts";
    }

    @GetMapping("/admin/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws DocumentException, IOException {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=report_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        List<ReportOrderForWeek> listReport = orderService.getOrderForWeek();

        OrderReport exporter = new OrderReport(listReport);
        exporter.export(response);

    }


}
