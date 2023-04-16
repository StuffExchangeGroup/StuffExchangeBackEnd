package pbm.com.exchange.app.rest;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pbm.com.exchange.framework.http.ApiResult;
import pbm.com.exchange.framework.http.ResponseData;
import pbm.com.exchange.service.CityService;
import pbm.com.exchange.service.dto.CityDTO;

@RestController
@RequestMapping("/api/app")
public class AppCityResource {

    @Autowired
    private CityService cityService;

    public AppCityResource(CityService cityService) {
        this.cityService = cityService;
    }

    @GetMapping("/cities")
    public ResponseEntity<ResponseData> getAllCities(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "stateId", required = false) Long provinceId
    ) {
        ResponseData responseData = new ResponseData();
        List<CityDTO> cityDTOs = null;

        if (provinceId == null) {
            cityDTOs = cityService.findAll(pageable).getContent();
        } else {
            cityDTOs = cityService.findByProvinceId(provinceId);
        }

        responseData.addData("cities", cityDTOs);
        return ApiResult.success(responseData);
    }
}
