package pbm.com.exchange.exception;

import com.fasterxml.jackson.annotation.JsonIgnore;
import pbm.com.exchange.message.Message;
import javax.print.attribute.standard.Severity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Message msg;
    private Severity severity;

    @JsonIgnore
    private Throwable rootCause;

    public BaseException(Message message, Severity severity, Throwable rootCause) {
        super(message.getContent(), rootCause);
        this.msg = message;
        this.severity = severity;
        this.rootCause = rootCause;
    }

    public BaseException(String string, String code, Object[] args, Object object) {}

    public Message getMsg() {
        return msg;
    }

    public void setMsg(Message msg) {
        this.msg = msg;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Throwable getRootCause() {
        return rootCause;
    }

    public void setRootCause(Throwable rootCause) {
        this.rootCause = rootCause;
    }
}
