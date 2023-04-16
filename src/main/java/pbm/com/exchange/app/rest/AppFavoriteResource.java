package pbm.com.exchange.app.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pbm.com.exchange.app.rest.respone.WishProductRes;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.service.FavoriteService;

@RestController
@RequestMapping("/api/app")
public class AppFavoriteResource {

    private final Logger log = LoggerFactory.getLogger(AppFavoriteResource.class);

    private final FavoriteService favoriteService;

    public AppFavoriteResource(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/favorites")
    public ResponseEntity<ResponseData> toggleFavoriteProduct(@RequestParam(required = true) Long productId) throws Exception {
        log.debug("Rest request to update favorite list productId: {}", productId);
        Boolean checkFavorite = favoriteService.isFavouriteExists(productId);
        ResponseData responseData = new ResponseData();
        responseData.addData("result", checkFavorite);
        return ApiResult.success(responseData);
    }

    @GetMapping("/my-wishlist")
    public ResponseEntity<ResponseData> getMyWishList(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("Rest request to get wishlist");
        ResponseData responseData = new ResponseData();
        List<WishProductRes> productRes = favoriteService.getWishList(pageable);
        responseData.addData("wishlist", productRes);
        return ApiResult.success(responseData);
    }
}
