package pbm.com.exchange.exception;

import javax.print.attribute.standard.Severity;
import pbm.com.exchange.message.Message;

public class NotFoundException extends BaseException {

    private static final long serialVersionUID = 1L;

    public NotFoundException(Message message, Throwable rootCause) {
        super(message, Severity.WARNING, rootCause);
    }
}