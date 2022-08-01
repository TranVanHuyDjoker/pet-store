package com.hivetech.service;


import com.hivetech.config.exception.BadRequestException;
import com.hivetech.utils.StringUtils;
import com.telnyx.sdk.ApiClient;
import com.telnyx.sdk.ApiException;
import com.telnyx.sdk.Configuration;
import com.telnyx.sdk.api.MessagesApi;
import com.telnyx.sdk.auth.HttpBearerAuth;
import com.telnyx.sdk.model.CreateMessageRequest;
import com.telnyx.sdk.model.MessageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SMSService {
    @Value("${telnyx.key}")
    private String telnyxKey;
    public String sendSMS(String phonenumber, String message) throws IOException, ApiException {
        if(!checkFormat(phonenumber)){
            throw new BadRequestException("Số điện thoại không đúng định dạng. VD: 0987654321");
        }
        String phonenumberE164Format = StringUtils.convertPhonenumberToE164(phonenumber);
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("https://api.telnyx.com/v2");

        HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
        bearerAuth.setBearerToken(telnyxKey);

        MessagesApi apiInstance = new MessagesApi(defaultClient);
        CreateMessageRequest createMessageRequest = new CreateMessageRequest()
                .from("+18554892142")
                .to(phonenumberE164Format)
                .text(message);
        try {
            MessageResponse result = apiInstance.createMessage(createMessageRequest);
            log.info("Send SMS to {} success {}", phonenumber, result);
            return "Gửi SMS thành công";
        } catch (ApiException e) {
            log.error("Send SMS to {} fail {}", phonenumber, e);
        }
        return "Gửi SMS thất bại";
    }

    private boolean checkFormat(String phonenumber){
        return phonenumber.matches("^0(3\\d|5\\d|7[0|9|7|6|8]|8[1|2|3|4|5|6|8|9]|9\\d)\\d{7}");
    }
}
