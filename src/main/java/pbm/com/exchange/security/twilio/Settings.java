package pbm.com.exchange.security.twilio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Settings {

    @Value("${twilio.ACCOUNT_SID}")
    private String ACCOUNT_SID;

    @Value("${twilio.AUTH_TOKEN}")
    private String AUTH_TOKEN;

    @Value("${twilio.SERVICE_ID}")
    private String SERVICE_ID;

    @Value("${twilio.USE_OTP}")
    private boolean USE_OTP;

    @Value("${twilio.CHANNEL}")
    private String CHANNEL;

    public String getAccountSid() {
        return this.ACCOUNT_SID;
    }

    public String getAuthToken() {
        return this.AUTH_TOKEN;
    }

    public String getServiceId() {
        return this.SERVICE_ID;
    }

    public boolean getUseOTP() {
        return this.USE_OTP;
    }

    public String getChannel() {
        return this.CHANNEL;
    }
}
