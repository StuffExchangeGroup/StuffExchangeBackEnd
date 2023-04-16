package pbm.com.exchange.framework.http;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessage {

    private String message;

    private List<FieldErr> fieldErrs;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<FieldErr> getFieldErrs() {
        return fieldErrs;
    }

    public void setFieldErrs(List<FieldErr> fieldErrs) {
        this.fieldErrs = fieldErrs;
    }
}
