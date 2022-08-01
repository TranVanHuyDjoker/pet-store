package com.hivetech.controller;

import com.hivetech.service.SMSService;
import com.telnyx.sdk.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SMSControler {
    @Autowired
    private SMSService smsService;
    @GetMapping("/sms")
    public String sendSMS(@RequestParam String phonenumber,
                          @RequestParam String message) throws IOException, ApiException {
        return smsService.sendSMS(phonenumber,message);
    }
}
