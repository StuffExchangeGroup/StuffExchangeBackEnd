package pbm.com.exchange.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pbm.com.exchange.service.DashboardService;
import pbm.com.exchange.service.dto.DashboardDTO;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardResource {

	@Autowired
	DashboardService dashboardService;
	
	@GetMapping
    public ResponseEntity<DashboardDTO> getAll() {
        Long numberOfProduct = dashboardService.getNumberOfProduct();
        Long numberOfExchange = dashboardService.getNumberOfExchange();
        Double successPercentOfExchange = dashboardService.getSuccessPercentOfExchange();
        Long numberOfUser = dashboardService.getNumberOfUser();
        DashboardDTO dashboardDTO = new DashboardDTO(numberOfExchange, numberOfProduct, numberOfUser, successPercentOfExchange);
        return ResponseEntity.ok().body(dashboardDTO);
    }
}