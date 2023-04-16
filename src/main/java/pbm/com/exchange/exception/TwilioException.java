package pbm.com.exchange.exception;

import javax.print.attribute.standard.Severity;
import pbm.com.exchange.message.Message;

public class TwilioException extends BaseException {

    private static final long serialVersionUID = 1L;

    public TwilioException(Message message, Throwable rootCause) {
        super(message, Severity.ERROR, rootCause);
    }
}
