package pbm.com.exchange.app.rest;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pbm.com.exchange.app.rest.request.SendChatNotificationReq;
import pbm.com.exchange.app.rest.respone.NotificationRes;
import pbm.com.exchange.app.rest.vm.NotificationDTO;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.service.NotificationService;

@RestController
@RequestMapping("api/app")
public class AppNotificationResource {

    private final Logger log = LoggerFactory.getLogger(AppNotificationTokenResource.class);

    @Autowired
    public NotificationService notificationService;

    @GetMapping("/list-notifications")
    public ResponseEntity<ResponseData> getAllNotificationsByCurrentUser(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("api get all notification by current user");
        ResponseData responseData = new ResponseData();
        NotificationRes notification = notificationService.getNotificationsByCurrentUser(pageable);
        responseData.addData("notification", notification);
        return ApiResult.success(responseData);
    }

    @GetMapping("/notification")
    public ResponseEntity<ResponseData> getDetailNotification(@RequestParam(name = "id", required = true) Long id) {
        log.debug("api get detail notification");
        ResponseData responseData = new ResponseData();
        NotificationDTO notification = notificationService.getNotificationById(id);
        responseData.addData("notification", notification);
        return ApiResult.success(responseData);
    }

    @PostMapping("/push-notification/chat-swap")
    public ResponseEntity<ResponseData> pushChatNotification(@Valid @RequestBody SendChatNotificationReq req) {
        log.debug("Push notification for chat swap: ", req);
        notificationService.sendChatNotification(req);
        return ApiResult.success();
    }
    
    @GetMapping("/notification/count")
    public ResponseEntity<ResponseData> getUnseenCountNotification() {
        log.debug("api get unseen count notification");
        ResponseData responseData = new ResponseData();
        Integer count = this.notificationService.countNotificationHaveNotSeen();
        responseData.addData("notification", count);
        return ApiResult.success(responseData);
    }
    
    @PutMapping("/notification/mark-all")
    public ResponseEntity<ResponseData> markAllAsRead(){
        log.debug("Api mark all notifications as read");
        ResponseData responseData = new ResponseData();
        boolean isSuccess = notificationService.markAllAsRead();
        responseData.addData("data", isSuccess);
        return ApiResult.success(responseData);
    }
}
