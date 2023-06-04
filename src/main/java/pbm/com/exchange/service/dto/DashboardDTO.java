package pbm.com.exchange.service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;	

@AllArgsConstructor
@Getter
@Setter
public class DashboardDTO {

	public Long numberOfExchange;
	
	public Long numberOfProduct;
	
	public Long numberOfUser;
	
	public Double successPercentOfExchange;
}
