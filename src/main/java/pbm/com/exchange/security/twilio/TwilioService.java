package pbm.com.exchange.security.twilio;

import com.twilio.Twilio;
import com.twilio.exception.ApiException;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.rest.verify.v2.service.VerificationCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pbm.com.exchange.exception.TwilioException;
import pbm.com.exchange.message.MessageHelper;

@Service
public class TwilioService {

    private Settings settings;
    private final String APPROVED = "approved";

    @Autowired
    public TwilioService(@Autowired Settings settings) {
        this.settings = settings;
        Twilio.init(settings.getAccountSid(), settings.getAuthToken());
    }

    public boolean sendOTP(String phone) {
        // check use OTP
        if (!settings.getUseOTP()) return false;

        try {
            Verification.creator(settings.getServiceId(), phone, settings.getChannel()).create();
        } catch (ApiException exception) {
            throw new TwilioException(MessageHelper.getMessage(exception.getMessage()), new Throwable());
        }
        return true;
    }

    public boolean checkOTP(String phone, String OTP) {
        // check use OTP
        if (!settings.getUseOTP()) return false;

        VerificationCheck verificationCheck = VerificationCheck.creator(settings.getServiceId(), OTP).setTo(phone).create();

        if (verificationCheck.getStatus().compareTo(APPROVED) != 0) {
            throw new TwilioException(MessageHelper.getMessage("Error verifying OTP."), new Throwable());
        }
        return true;
    }

    public boolean hasOTP() {
        return settings.getUseOTP();
    }
}
