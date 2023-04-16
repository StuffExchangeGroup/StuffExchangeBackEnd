package pbm.com.exchange.app.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.service.CategoryService;
import pbm.com.exchange.service.dto.CategoryDTO;

@RestController
@RequestMapping("/api/app")
public class AppCategoryResource {

    private final Logger log = LoggerFactory.getLogger(AppCategoryResource.class);

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<ResponseData> getAllCategory() {
        log.debug("Rest request to get all categories");

        ResponseData responseData = new ResponseData();

        List<CategoryDTO> categoryDTOs = categoryService.findAllByActive();

        responseData.addData("categories", categoryDTOs);
        return ApiResult.success(responseData);
    }
}
