package gr3.workhub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PaymentGatewayConfig {
    @Value("${momo.api.url}")
    private String momoApiUrl;

    @Value("${vnpay.api.url}")
    private String vnpayApiUrl;

    @Value("${momo.api.key}")
    private String momoApiKey;

    @Value("${vnpay.api.key}")
    private String vnpayApiKey;

    public String getMomoApiUrl() {
        return momoApiUrl;
    }

    public String getVnpayApiUrl() {
        return vnpayApiUrl;
    }

    public String getMomoApiKey() {
        return momoApiKey;
    }

    public String getVnpayApiKey() {
        return vnpayApiKey;
    }
}