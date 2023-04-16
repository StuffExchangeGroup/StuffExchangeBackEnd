package pbm.com.exchange.app.rest;

import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pbm.com.exchange.app.rest.request.ChangePasswordReq;
import pbm.com.exchange.app.rest.request.CheckOTPChangePasswordReq;
import pbm.com.exchange.app.rest.request.CheckOTPReq;
import pbm.com.exchange.app.rest.request.CheckOTPSignUpReq;
import pbm.com.exchange.app.rest.request.ReSendOTPReq;
import pbm.com.exchange.app.rest.request.ResendOTPForUpdateEmail;
import pbm.com.exchange.app.rest.request.ResetPasswordReq;
import pbm.com.exchange.app.rest.request.SignInReq;
import pbm.com.exchange.app.rest.request.SignUpReq;
import pbm.com.exchange.app.rest.request.UpdatePasswordReq;
import pbm.com.exchange.domain.User;
import pbm.com.exchange.exception.BadRequestException;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.message.Message;
import pbm.com.exchange.message.MessageHelper;
import pbm.com.exchange.service.UserService;

@RestController
@RequestMapping("/api/app/auth")
public class AppUserResource {

    private final Logger log = LoggerFactory.getLogger(AppUserResource.class);

    @Autowired
    private UserService userService;

    @PostMapping("/sign-up")
    public ResponseEntity<ResponseData> signUp(@Valid @RequestBody SignUpReq signUpReq) {
        log.debug("REST request to sign up: {}", signUpReq);
        ResponseData responseData = new ResponseData();
        responseData.addData("signUp", userService.signUp(signUpReq));
        return ApiResult.success(responseData);
    }

    @PostMapping("/check-otp-sign-up")
    public ResponseEntity<ResponseData> checkOTPSignUp(@Valid @RequestBody CheckOTPSignUpReq checkOTPSignUpReq) {
        log.debug("REST request to check otp sign up: {}", checkOTPSignUpReq);
        ResponseData responseData = new ResponseData();
        responseData.addData("signIn", userService.checkOTPSignUpAndActiveUser(checkOTPSignUpReq));
        return ApiResult.success(responseData);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ResponseData> signIn(@Valid @RequestBody SignInReq signInReq) {
        log.debug("REST request to sign in: {}", signInReq);
        ResponseData responseData = new ResponseData();
        responseData.addData("signIn", userService.signIn(signInReq));
        return ApiResult.success(responseData);
    }

    @PostMapping("/check-otp")
    public ResponseEntity<ResponseData> checkOTP(@Valid @RequestBody CheckOTPReq checkOTPReq) {
        log.debug("REST request to check otp: {}", checkOTPReq);
        userService.checkOTP(checkOTPReq);
        return ApiResult.success();
    }

    @PostMapping("/resend-otp")
    public ResponseEntity<?> reSendOTPForRegister(@Valid @RequestBody ReSendOTPReq reSendOTPReq) {
        log.debug("REST request to resend OTP: {}", reSendOTPReq);
        userService.resendOTP(reSendOTPReq.getEmail());
        return ApiResult.success();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResponseData> resetPassword(@Valid @RequestBody ResetPasswordReq resetPasswordReq) {
        log.debug("REST request to reset password: {}", resetPasswordReq);
        ResponseData responseData = new ResponseData();
        responseData.addData("resetPassword", userService.forgotPassword(resetPasswordReq));
        return ApiResult.success(responseData);
    }

    @PostMapping("/update-password")
    public ResponseEntity<ResponseData> updatePassword(@Valid @RequestBody UpdatePasswordReq updatePasswordReq) {
        log.debug("REST request to update password: {}", updatePasswordReq);
        ResponseData responseData = new ResponseData();
        userService.updatePassword(updatePasswordReq);
//        responseData.addData("signIn", userService.updatePassword(updatePasswordReq));
        return ApiResult.success();
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseData> requestChangePassword(@Valid @RequestBody ChangePasswordReq changePasswordReq) {
        log.debug("REST request to change password: {}", changePasswordReq);
        ResponseData responseData = new ResponseData();
        responseData.addData("changePassword", userService.requestChangePassword(changePasswordReq));
        return ApiResult.success(responseData);
    }

    @PostMapping("/check-otp-change-password")
    public ResponseEntity<ResponseData> checkOTPChangePassword(@Valid @RequestBody CheckOTPChangePasswordReq checkOTPChangePasswordReq) {
        log.debug("REST request to check otp change password: {}", checkOTPChangePasswordReq);
        userService.checkOTPAndUpdatePassword(checkOTPChangePasswordReq);
        return ApiResult.success();
    }
    
    @GetMapping("/activate")
    public ResponseEntity<ResponseData> activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (!user.isPresent()) {
            throw new BadRequestException(MessageHelper.getMessage(Message.Keys.E0093), new Throwable());
        }
        return ApiResult.success();
    }
}
