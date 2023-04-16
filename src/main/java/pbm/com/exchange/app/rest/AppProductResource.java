package pbm.com.exchange.app.rest;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pbm.com.exchange.app.rest.request.PostProductReq;
import pbm.com.exchange.app.rest.request.UpdateProductReq;
import pbm.com.exchange.app.rest.respone.FilterProductRes;
import pbm.com.exchange.app.rest.respone.GetProductRes;
import pbm.com.exchange.app.rest.respone.MyProductRes;
import pbm.com.exchange.app.rest.respone.SimilarProductRes;
import pbm.com.exchange.app.rest.vm.CriteriaDTO;
import pbm.com.exchange.app.rest.vm.FilterDTO;
import pbm.com.exchange.domain.enumeration.Condition;
import pbm.com.exchange.domain.enumeration.ProductStatus;
import pbm.com.exchange.domain.enumeration.ProductType;
import pbm.com.exchange.domain.enumeration.PurposeType;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.service.FileService;
import pbm.com.exchange.service.ProductService;
import pbm.com.exchange.service.dto.FileDTO;
import pbm.com.exchange.service.dto.ProductDTO;

@RestController
@RequestMapping("/api/app")
public class AppProductResource {

    private final Logger log = LoggerFactory.getLogger(AppProductResource.class);

    @Autowired
    ProductService productService;

    @Autowired
    FileService fileService;

    @GetMapping("/filter-products")
    public ResponseEntity<ResponseData> getProducts(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) Long categoryId,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) ProductType type,
        @RequestParam(required = false) Condition condition,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) PurposeType purposeType
    ) {
        CriteriaDTO criteriaDTO = new CriteriaDTO(categoryId, search, type, condition, location, purposeType);
        log.debug("Request to filter products : ", criteriaDTO);
        ResponseData responseData = new ResponseData();
        List<ProductDTO> productDTOs = productService.filterProducts(pageable, criteriaDTO);
        responseData.addData("products", productDTOs);
        return ApiResult.success(responseData);
    }

    @GetMapping("web-filter-products")
    public ResponseEntity<ResponseData> getWebProducts(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) List<Long> categoryIds,
        @RequestParam(required = false) List<Long> cityIds,
        @RequestParam(required = false) String search,
        @RequestParam(required = false) ProductType type,
        @RequestParam(required = false) List<Condition> conditions,
        @RequestParam(required = false) List<PurposeType> purposeTypes
    ) {
        FilterDTO filterDTO = new FilterDTO(search, categoryIds, conditions, cityIds, purposeTypes, type);
        log.debug("Request to web filter products : ", filterDTO);
        ResponseData responseData = new ResponseData();
        FilterProductRes filterProductRes = productService.webFilterProducts(pageable, filterDTO);
        responseData.addData("filterProductRes", filterProductRes);
        return ApiResult.success(responseData);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ResponseData> getDetailProduct(@PathVariable(value = "id", required = true) Long productId) {
        log.debug("Request to get product detail id: ", productId);
        ResponseData responseData = new ResponseData();
        GetProductRes productDTOs = productService.getProductDetail(productId);
        responseData.addData("product", productDTOs);
        return ApiResult.success(responseData);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ResponseData> deleteProduct(@PathVariable(value = "id", required = true) Long productId) {
        log.debug("Request to delete product detail id: ", productId);
        productService.deleteProduct(productId);
        return ApiResult.success();
    }

    @PostMapping("/products")
    public ResponseEntity<ResponseData> createProduct(@Valid @RequestBody PostProductReq productDTO, HttpServletRequest request)
        throws BindException {
        log.debug("Request to post new product ", productDTO);
        ResponseData responseData = new ResponseData();
        productDTO = productService.save(productDTO, request);
        responseData.addData("product", productDTO);
        return ApiResult.success(responseData);
    }

    @GetMapping("/products/my-items")
    public ResponseEntity<ResponseData> getMyItems(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(required = false) ProductStatus status
    ) {
        log.debug("Request to get my product");
        ResponseData responseData = new ResponseData();
        List<MyProductRes> productDTOs = productService.findMyProducts(pageable, status);
        responseData.addData("myItems", productDTOs);
        return ApiResult.success(responseData);
    }

    @GetMapping("/products/similar/{id}")
    public ResponseEntity<ResponseData> getSimilarProducts(@PathVariable(value = "id", required = true) Long productId) {
        log.debug("Request to get similar product");
        ResponseData responseData = new ResponseData();
        List<SimilarProductRes> similarProductDTOs = productService.getSimilarProducts(productId);
        responseData.addData("products", similarProductDTOs);
        return ApiResult.success(responseData);
    }

    @PutMapping("/products")
    public ResponseEntity<ResponseData> createProduct(@Valid @RequestBody UpdateProductReq productReq, HttpServletRequest request)
        throws BindException {
        log.debug("Request to update product {}", productReq);
        ResponseData responseData = new ResponseData();
        productReq = productService.updateProduct(productReq, request);
        responseData.addData("product", productReq);
        return ApiResult.success(responseData);
    }

    @PostMapping("/products/uploadImg")
    public ResponseEntity<List<FileDTO>> uploadImage(@RequestPart("file") MultipartFile[] file) throws Exception {
        log.debug("Request to upload new images");
        List<FileDTO> fileDTOs = new ArrayList<>();

        for (MultipartFile f : file) {
            FileDTO fileDTO = fileService.uploadFile(f);
            fileDTOs.add(fileDTO);
        }

        return ResponseEntity.ok().body(fileDTOs);
    }

    @GetMapping("/my-products/{id}")
    public ResponseEntity<ResponseData> getMyDetailProduct(@PathVariable(value = "id", required = true) Long productId) {
        log.debug("Request to get product detail id: ", productId);
        ResponseData responseData = new ResponseData();
        GetProductRes productDTOs = productService.getMyProductDetail(productId);
        responseData.addData("product", productDTOs);
        return ApiResult.success(responseData);
    }
}
