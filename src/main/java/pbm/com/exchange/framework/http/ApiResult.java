package pbm.com.exchange.framework.http;

import pbm.com.exchange.message.Message;
import pbm.com.exchange.message.MessageHelper;
import java.util.List;
import java.util.Objects;
import liquibase.repackaged.org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiResult {

    public static ResponseEntity<ResponseData> success() {
        return response(HttpStatus.OK, MessageHelper.getMessage(Message.Keys.I0001), new ResponseData(), null);
    }

    public static ResponseEntity<ResponseData> success(ResponseData data) {
        return response(HttpStatus.OK, MessageHelper.getMessage(Message.Keys.I0001), data, null);
    }

    public static ResponseEntity<ResponseData> failed() {
        return response(HttpStatus.BAD_REQUEST, MessageHelper.getMessage(Message.Keys.I0002), new ResponseData(), null);
    }

    public static ResponseEntity<ResponseData> failed(Message message) {
        return response(HttpStatus.BAD_REQUEST, message, new ResponseData(), null);
    }

    public static ResponseEntity<ResponseData> failed(Message message, List<FieldErr> fieldErrs) {
        return response(HttpStatus.BAD_REQUEST, message, new ResponseData(), fieldErrs);
    }

    public static ResponseEntity<ResponseData> response(
        HttpStatus httpStatus,
        Message message,
        ResponseData data,
        List<FieldErr> fieldErrs
    ) {
        if (data == null) {
            data = new ResponseData();
        }
        if (httpStatus.equals(HttpStatus.OK)) {
            data.setStatusCode(HttpStatus.OK);
            data.setIsSuccess(true);
        } else {
            data.setIsSuccess(false);
            ErrorMessage errorMessage = new ErrorMessage();
            if (Objects.nonNull(message)) {
                errorMessage.setMessage(message.getContent());
            }
            if (CollectionUtils.isNotEmpty(fieldErrs)) {
                errorMessage.setFieldErrs(fieldErrs);
            }
            data.setStatusCode(httpStatus);
            data.setErrorMessage(errorMessage);
            data.setData(null);
        }
        return ResponseEntity.status(httpStatus).body(data);
    }
}
