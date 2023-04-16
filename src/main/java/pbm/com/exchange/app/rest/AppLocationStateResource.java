package pbm.com.exchange.app.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.service.NationalityService;
import pbm.com.exchange.service.ProductService;
import pbm.com.exchange.service.ProvinceService;
import pbm.com.exchange.service.dto.NationalityDTO;
import pbm.com.exchange.service.dto.ProvinceDTO;

@RestController
@RequestMapping("/api/app")
public class AppLocationStateResource {

    private final Logger log = LoggerFactory.getLogger(AppLocationStateResource.class);

    @Autowired
    private ProvinceService provinceService;

    @Autowired
    private NationalityService nationalityService;

    @Autowired
    private ProductService productService;

    @GetMapping("/states")
    public ResponseEntity<ResponseData> getProvinces(@RequestParam(value = "locationId", required = false) Long locationId) {
        log.debug("Request to get list of states");
        ResponseData responseData = new ResponseData();
        List<ProvinceDTO> provinceDTOs = null;

        if (locationId != null) {
            provinceDTOs = provinceService.findByLocationId(locationId);
        } else {
            provinceDTOs = provinceService.findAll();
        }
        responseData.addData("states", provinceDTOs);
        return ApiResult.success(responseData);
    }

    @GetMapping("/states/{stateId}")
    public ResponseEntity<ResponseData> getProvinceById(@PathVariable Long stateId) {
        log.debug("Request to get state by id: ", stateId);
        ResponseData responseData = new ResponseData();
        ProvinceDTO provinceDTO = provinceService.findOne(stateId).get();
        responseData.addData("data", provinceDTO);
        return ApiResult.success(responseData);
    }

    @GetMapping("/locations")
    public ResponseEntity<ResponseData> getNationalities() {
        log.debug("Request to get all locations");
        ResponseData responseData = new ResponseData();
        List<NationalityDTO> nationalitiesDTOs = nationalityService.findAll();
        responseData.addData("nationalities", nationalitiesDTOs);
        return ApiResult.success(responseData);
    }

    @GetMapping("/products/locations")
    public ResponseEntity<ResponseData> getAllProductLocations() {
        log.debug("Request to get all product's locations");
        ResponseData responseData = new ResponseData();
        List<String> locations = productService.getAllProductLocations();
        responseData.addData("locations", locations);
        return ApiResult.success(responseData);
    }
}
