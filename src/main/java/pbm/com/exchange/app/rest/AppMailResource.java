package pbm.com.exchange.app.rest;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pbm.com.exchange.domain.User;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.repository.UserRepository;
import pbm.com.exchange.service.MailService;

@RestController
@RequestMapping("/api/app")
public class AppMailResource {

    @Autowired
    private MailService mailService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/send-mail")
    public ResponseEntity<ResponseData> sendMessage(@RequestParam(name = "email") String email) {
        Optional<User> userOptional = userRepository.findOneByEmailIgnoreCase(email);
        //        mailService.sendRemindUserEmail(userOptional.get());
        mailService.sendInviteFriendEmail(userOptional.get());
        //        mailService.sendActivatedEmail(userOptional.get());
        return ApiResult.success();
    }
}
