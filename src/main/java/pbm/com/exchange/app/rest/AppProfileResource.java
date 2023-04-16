package pbm.com.exchange.app.rest;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import liquibase.repackaged.org.apache.commons.collections4.map.HashedMap;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pbm.com.exchange.app.rest.request.ReSendOTPReq;
import pbm.com.exchange.app.rest.request.ResendOTPForUpdateEmail;
import pbm.com.exchange.app.rest.respone.GetProfileUidRes;
import pbm.com.exchange.app.rest.vm.PutProfileDTO;
import pbm.com.exchange.domain.enumeration.ReferralType;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.service.FileService;
import pbm.com.exchange.service.ProfileService;
import pbm.com.exchange.service.UserService;
import pbm.com.exchange.service.dto.FileDTO;

@RestController
@RequestMapping("/api/app")
public class AppProfileResource {

    private final Logger log = LoggerFactory.getLogger(AppProfileResource.class);

    @Autowired
    private ProfileService profileService;

    @Autowired
    private FileService fileService;
    
    @Autowired
    private UserService userService;

    @PutMapping("/profiles")
    public ResponseEntity<ResponseData> updateProfile(@Valid @RequestBody PutProfileDTO profileDTO) throws Exception {
        log.debug("REST request to partial update Profile partially : {}, {}", profileDTO);
        PutProfileDTO profile = profileService.partialUpdate(profileDTO);

        ResponseData responseData = new ResponseData();
        responseData.addData("data", profile);

        return ApiResult.success(responseData);
    }

    @GetMapping("/profiles/uid")
    public ResponseEntity<ResponseData> getProfileByUid(@RequestParam(name = "uid", required = true) String uid) {
        log.debug("Get profile by uid : ", uid);
        ResponseData responseData = new ResponseData();
        GetProfileUidRes getProfileUidRes = profileService.getProfileByUid(uid);
        responseData.addData("profile", getProfileUidRes);

        return ApiResult.success(responseData);
    }

    @PostMapping("/profiles/uploadAvatar")
    public ResponseEntity<ResponseData> uploadImage(@RequestPart("avatar") MultipartFile file, HttpServletRequest request)
        throws Exception {
        log.debug("REST request to upload avatar profile : ", file);
        FileDTO fileDTO = fileService.uploadFile(file);
        if (fileDTO == null) {
            return null;
        }
        String newAvatar = profileService.updateAvatar(fileDTO, request);

        Map<String, Object> avatarFile = new HashedMap<>();
        avatarFile.put("id", fileDTO.getId());
        avatarFile.put("linkImage", newAvatar);

        ResponseData responseData = new ResponseData();
        responseData.addData("avatar", avatarFile);
        return ApiResult.success(responseData);
    }

    @GetMapping("/profiles/code")
    public ResponseEntity<ResponseData> getMyCodeReferal(
        @RequestParam(name = "type", required = false) ReferralType referralType,
        HttpServletRequest request
    ) {
        ResponseData responseData = new ResponseData();
        responseData.addData("referral", profileService.generateReferralCode(referralType, request));

        return ApiResult.success(responseData);
    }
    
    @PostMapping("/check-exist-email")
    public ResponseEntity<ResponseData> checkExistEmail(@RequestParam(value = "email") String email){
        log.debug("REST request to check if email exists");
        userService.checkExistEmail(email);
        return ApiResult.success();
    }
    
    @PostMapping("/resend-otp-update-email")
    public ResponseEntity<ResponseData> reSendOTPForUpdateEmail(@Valid @RequestBody ReSendOTPReq reSendOTPReq) {
        log.debug("REST request to resend OTP: {}", reSendOTPReq);
        userService.resendOTP(reSendOTPReq);
        return ApiResult.success();
    }
}
