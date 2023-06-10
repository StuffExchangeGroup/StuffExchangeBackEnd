package pbm.com.exchange.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pbm.com.exchange.domain.enumeration.ExchangeStatus;
import pbm.com.exchange.repository.ExchangeRepository;
import pbm.com.exchange.repository.ProductRepository;
import pbm.com.exchange.repository.UserRepository;
import pbm.com.exchange.service.DashboardService;

@Service
@Transactional
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	ProductRepository productRepository;
	
	@Autowired
	ExchangeRepository exchangeRepository;
	
	@Autowired 
	UserRepository userRepository;
	;
	@Override
	public Long getNumberOfExchange() {
		return exchangeRepository.count();
	}

	@Override
	public Long getNumberOfProduct() {
	 	return productRepository.count();
	}

	@Override
	public Double getSuccessPercentOfExchange() {
		Long numberOfSuccessExchange = exchangeRepository.countByStatus(ExchangeStatus.SWAPPING);
		Long numberOfExchange = getNumberOfExchange();
		
		return numberOfSuccessExchange / (numberOfExchange * 1.0);
	}

	@Override
	public Long getNumberOfUser() {
		return userRepository.count();
	}

}
