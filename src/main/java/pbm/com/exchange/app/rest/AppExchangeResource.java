package pbm.com.exchange.app.rest;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pbm.com.exchange.app.rest.request.ConfirmSwapReq;
import pbm.com.exchange.app.rest.request.RequestSwapReq;
import pbm.com.exchange.app.rest.vm.ItemSwapDTO;
import pbm.com.exchange.app.rest.vm.MySwapDTO;
import pbm.com.exchange.domain.enumeration.ExchangeStatus;
import pbm.com.exchange.domain.enumeration.ExchangeType;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.service.ExchangeService;

@RestController
@RequestMapping("/api/app/exchanges")
public class AppExchangeResource {

    private final Logger log = LoggerFactory.getLogger(AppExchangeResource.class);

    @Autowired
    ExchangeService exchangeService;

    @PostMapping("/swap")
    ResponseEntity<ResponseData> requestSwap(@Valid @RequestBody RequestSwapReq requestSwapReq) {
        exchangeService.requestSwap(requestSwapReq);
        return ApiResult.success();
    }

    @PostMapping("/confirm-swap")
    ResponseEntity<ResponseData> confirmSwap(@Valid @RequestBody ConfirmSwapReq confirmSwapReq) {
        log.debug("Request to confirm swap {}", confirmSwapReq);
        exchangeService.confirmSwap(confirmSwapReq);
        return ApiResult.success();
    }

    @PutMapping("/start-chatting/{exchangeId}")
    public ResponseEntity<ResponseData> startChatting(@PathVariable(required = true) Long exchangeId) {
        exchangeService.startChatting(exchangeId);
        return ApiResult.success();
    }

    @GetMapping("/my-exchanges")
    public ResponseEntity<ResponseData> getMyExchanges(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) ExchangeStatus exchangeStatus,
        @RequestParam(required = false) ExchangeType type
    ) {
        log.debug("Request to get my exchanges");
        ResponseData responseData = new ResponseData();
        List<MySwapDTO> myExchangeDTOs = exchangeService.getMyExchanges(exchangeStatus, type, pageable);
        responseData.addData("myExchanges", myExchangeDTOs);
        return ApiResult.success(responseData);
    }

    @GetMapping("/item-swaps")
    public ResponseEntity<ResponseData> getExchangeByProductId(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = true) Long productId,
        @RequestParam(required = false) ExchangeType type
    ) {
        log.debug("Request to get my exchanges from product id: ", productId);
        ResponseData responseData = new ResponseData();
        List<ItemSwapDTO> myExchangeDTOs = exchangeService.getExchangesByProductId(productId, type, pageable);
        Long totalExchange = exchangeService.getTotalExchangeByProduct(productId);
        responseData.addData("total", totalExchange);
        responseData.addData("myExchanges", myExchangeDTOs);
        return ApiResult.success(responseData);
    }
}
