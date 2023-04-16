package pbm.com.exchange.exception;

import javax.print.attribute.standard.Severity;

import pbm.com.exchange.message.Message;

public class BadRequestException extends BaseException {

    private static final long serialVersionUID = 1L;

    public BadRequestException(Message message, Throwable rootCause) {
        super(message, Severity.WARNING, rootCause);
    }
}
