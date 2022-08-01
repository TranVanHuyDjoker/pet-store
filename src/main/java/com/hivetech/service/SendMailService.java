package com.hivetech.service;

import com.hivetech.model.dto.DataMailDTO;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.layout.font.FontProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendMailService {

    @Autowired
    @Qualifier("ThreadPoolTaskExecutorBean")
    private  Executor executor;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("${base-url}")
    private String baseUrl;
    @Value("${pdf.directory}")
    private String baseDir;
    @Value("${mailServer.email}")
    private String fromAddress;


    public void sendMail(DataMailDTO mailDTO, String template) throws IOException {
        String parameter = (String) mailDTO.getProps().get("parameter");
        mailDTO.getProps().put("verifyUrl", baseUrl + parameter);

        Context context = new Context();
        context.setVariables(mailDTO.getProps());
        String html = templateEngine.process(template, context);

        MimeMessage message = mailSender.createMimeMessage();
        executor.execute(() -> {
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
                helper.setFrom(fromAddress);
                helper.setTo(mailDTO.getTo());
                helper.setSubject("HiveTech company");
                helper.setText(html, true);
                mailSender.send(message);
            } catch (MessagingException e) {
                log.error("Send mail error", e);
            }
        });
    }

    public void sendMailOrder(DataMailDTO mailDTO, String template, String pdfFilename) throws IOException {
        String parameter = (String) mailDTO.getProps().get("parameter");
        mailDTO.getProps().put("verifyUrl", baseUrl + parameter);
        Context context = new Context();
        context.setVariables(mailDTO.getProps());
        String html = templateEngine.process(template, context);
        MimeMessage message = mailSender.createMimeMessage();
        executor.execute(() -> {
                    try {
                        OutputStream outputStream = new FileOutputStream(baseDir + pdfFilename);
                        FontProvider fontProvider = new FontProvider();
                        fontProvider.addFont("/home/svip/font/GothicA1-ExtraLight.ttf");
                        ConverterProperties converterProperties = new ConverterProperties();
                        converterProperties.setFontProvider(fontProvider);
                        HtmlConverter.convertToPdf(html, outputStream, converterProperties);
                        File file = new File(baseDir + pdfFilename);
                        MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
                        helper.setFrom(fromAddress);
                        helper.setTo(mailDTO.getTo());
                        helper.setSubject("HiveTech company");
                        helper.setText("Cửa hàng PetStore kính gửi quý khách hóa đơn:", false);
                        helper.addAttachment(pdfFilename, file);
                        mailSender.send(message);

                    } catch (MessagingException e) {
                        log.error("Send mail error", e);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
        );
    }
}
