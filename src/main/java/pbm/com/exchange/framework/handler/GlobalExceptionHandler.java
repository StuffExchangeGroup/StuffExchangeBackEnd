package pbm.com.exchange.framework.handler;

import pbm.com.exchange.exception.*;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.FieldErr;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.message.Message;
import pbm.com.exchange.message.MessageHelper;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * Created by 30Hero on 12/17/2020
 **/

@RestControllerAdvice
@Component
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ResponseData> handleAuthenticationException(AuthenticationException exception) {
        return ApiResult.response(HttpStatus.UNAUTHORIZED, exception.getMsg(), null, null);
    }

    @ResponseBody
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ResponseData> handleInsufficientAuthenticationException(InsufficientAuthenticationException exception) {
        return ApiResult.response(HttpStatus.UNAUTHORIZED, MessageHelper.getMessage(Message.Keys.E0005), null, null);
    }

    @ResponseBody
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseData> handleRuntimeException(RuntimeException exception) {
        return ApiResult.response(HttpStatus.INTERNAL_SERVER_ERROR, MessageHelper.getMessage(Message.Keys.I0002), null, null);
    }

    @ResponseBody
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ResponseData> handleIOException(IOException exception) {
        return ApiResult.response(HttpStatus.INTERNAL_SERVER_ERROR, MessageHelper.getMessage(Message.Keys.I0002), null, null);
    }

    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseData> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        Message message = new Message();
        message.setId(Message.Keys.I0002);
        message.setContent(exception.getMessage());
        return ApiResult.response(HttpStatus.METHOD_NOT_ALLOWED, message, null, null);
    }

    @ResponseBody
    @ExceptionHandler(ServletRequestBindingException.class)
    public ResponseEntity<ResponseData> handleServletRequestBindingException(ServletRequestBindingException exception) {
        Message message = new Message();
        message.setId(Message.Keys.I0002);
        message.setContent(exception.getMessage());
        return ApiResult.response(HttpStatus.BAD_REQUEST, message, null, null);
    }

    @ResponseBody
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseData> handleAccessDeniedException(AccessDeniedException exception) {
        return ApiResult.response(HttpStatus.UNAUTHORIZED, MessageHelper.getMessage(Message.Keys.I0002), null, null);
    }

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseData> handleHttpMessageNotReadableException(HttpMessageNotReadableException exception) {
        Message message = new Message();
        message.setContent(exception.getMessage());
        return ApiResult.response(HttpStatus.BAD_REQUEST, message, null, null);
    }

    @ResponseBody
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ResponseData> handleHttpMediaTypeNotSupportedException(HttpMediaTypeNotSupportedException exception) {
        Message message = new Message();
        message.setContent(exception.getMessage());
        return ApiResult.response(HttpStatus.BAD_REQUEST, message, null, null);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseData> handleException(Exception exception) {
        return ApiResult.response(HttpStatus.INTERNAL_SERVER_ERROR, MessageHelper.getMessage(Message.Keys.I0002), null, null);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseData> handleBindException(BindException ex) {
        BindingResult result = ex.getBindingResult();
        List<FieldErr> fiedErrs = result
            .getFieldErrors()
            .stream()
            .map(f -> new FieldErr(f.getField(), StringUtils.isNotBlank(f.getDefaultMessage()) ? f.getDefaultMessage() : f.getCode()))
            .collect(Collectors.toList());
        return ApiResult.response(HttpStatus.BAD_REQUEST, MessageHelper.getMessage(Message.Keys.E0011), null, fiedErrs);
    }

    @ResponseBody
    @ExceptionHandler(AlreadyUsedException.class)
    public ResponseEntity<ResponseData> handleAlreadyUsedException(AlreadyUsedException exception) {
        return ApiResult.response(HttpStatus.BAD_REQUEST, exception.getMsg(), null, null);
    }

    @ResponseBody
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseData> handleBadRequestException(BadRequestException exception) {
        return ApiResult.response(HttpStatus.BAD_REQUEST, exception.getMsg(), null, null);
    }

    @ExceptionHandler(TwilioException.class)
    public ResponseEntity<ResponseData> handleTwilioException(TwilioException exception) {
        return ApiResult.response(HttpStatus.BAD_REQUEST, exception.getMsg(), null, null);
    }

    @ResponseBody
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseData> handleNotFoundException(NotFoundException exception) {
        return ApiResult.response(HttpStatus.NOT_FOUND, exception.getMsg(), null, null);
    }

    @ResponseBody
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseData> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        return ApiResult.response(HttpStatus.BAD_REQUEST, MessageHelper.getMessage(Message.Keys.E0099), null, null);
    }
}
