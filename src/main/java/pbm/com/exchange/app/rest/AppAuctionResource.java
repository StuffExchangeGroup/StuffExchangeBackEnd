package pbm.com.exchange.app.rest;

import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pbm.com.exchange.app.rest.request.AuctionReq;
import pbm.com.exchange.app.rest.respone.GetAuctionRes;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.service.AuctionService;

@RestController
@RequestMapping("api/app")
public class AppAuctionResource {

    private final Logger log = LoggerFactory.getLogger(AppAuctionResource.class);

    @Autowired
    private AuctionService auctionService;

    @PostMapping("/auctions")
    public ResponseEntity<ResponseData> createAuction(@RequestBody @Valid AuctionReq auctionReq) {
        log.debug("REST request to make an product auction {}" + auctionReq);
        auctionService.auctionProduct(auctionReq);
        return ApiResult.success();
    }

    @GetMapping("/auctions")
    public ResponseEntity<ResponseData> getAuctionList(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = true) Long productId
    ) {
        log.debug("REST request to get list of auction by productId: {}" + productId);
        List<GetAuctionRes> auctionListRes = auctionService.findByProductId(pageable, productId);
        ResponseData responseData = new ResponseData();
        responseData.addData("auctionList", auctionListRes);
        return ApiResult.success(responseData);
    }
}
