package com.hivetech.config;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class PaypalConfig {
    @Value("${paypal.client-id}")
    private String clientId;
    @Value("${paypal.serect-key}")
    private String secrectKey;
    @Value("${paypal.mode}")
    private String mode;

    @Bean
    public Map paypalSdkConfig() {
        Map configMap = new HashMap();
        configMap.put("mode", mode);
        return configMap;
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential() {
        return new OAuthTokenCredential(clientId, secrectKey, paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() throws PayPalRESTException {
        APIContext context = new APIContext(oAuthTokenCredential().getAccessToken());
        context.setConfigurationMap(paypalSdkConfig());
        log.info("Connect to Paypal Success", context.getAccessToken());
        return context;
    }
}
