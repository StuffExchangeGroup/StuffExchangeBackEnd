package pbm.com.exchange.app.rest;

import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pbm.com.exchange.app.rest.request.SendNotificationToDeviceReq;
import pbm.com.exchange.app.rest.request.SendNotificationToMultiDevicesReq;
import pbm.com.exchange.app.rest.request.SendNotificationToTopicReq;
import pbm.com.exchange.exception.BadRequestException;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.service.NotificationTokenService;
import pbm.com.exchange.service.dto.NotificationTokenDTO;

@RestController
@RequestMapping("/api/app")
public class AppNotificationTokenResource {

    private final Logger log = LoggerFactory.getLogger(AppNotificationTokenResource.class);

    @Autowired
    private NotificationTokenService notificationService;

    @PostMapping("/add-token")
    public ResponseEntity<ResponseData> addNotificationToken(@Valid @RequestBody NotificationTokenDTO notificationTokenDTO) {
        log.debug("Rest request to add token {}" + notificationTokenDTO);
        boolean isSuccess = notificationService.addNotificationToken(notificationTokenDTO);
        if (isSuccess) return ApiResult.success();
        return ApiResult.failed();
    }

    @DeleteMapping("/delete-token")
    public ResponseEntity<ResponseData> deleteNotificationToken(@RequestParam(required = true) String token) {
        log.debug("Request to add token {}" + token);
        boolean isSuccess = notificationService.deleteNotificationToken(token);
        if (isSuccess) return ApiResult.success();
        return ApiResult.failed();
    }

    @PostMapping("/push-notification/device")
    public ResponseEntity<ResponseData> pushNotificationToDevice(@RequestBody SendNotificationToDeviceReq req)
        throws BadRequestException, InterruptedException {
        log.debug("Request to push notification " + req);
        notificationService.sendNotificationToDevice(req);
        return ApiResult.success();
    }

    @PostMapping("/push-notification/multi-devices")
    public ResponseEntity<ResponseData> pushNotificationToMutiDevices(@RequestBody SendNotificationToMultiDevicesReq req)
        throws BadRequestException, InterruptedException {
        log.debug("Request to add token " + req);
        notificationService.sendNotificationToMultipleDevices(req);
        return ApiResult.success();
    }

    @PostMapping("/push-notification/topic")
    public ResponseEntity<ResponseData> sendPnsToTopic(@RequestBody SendNotificationToTopicReq req) {
        notificationService.sendNotificationToTopic(req);
        return ApiResult.success();
    }
}
