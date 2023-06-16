package egovframework.msa.sample.serviceImpl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import egovframework.msa.sample.service.CustomerApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CustomerApiServiceImpl implements CustomerApiService {
    
    private final RestTemplate restTemplate;
    
    @Override
    @HystrixCommand(fallbackMethod = "getCustomerDetailFallback")
    public String getCustomerDetail(String customerId) {
        return restTemplate.getForObject("http://localhost:8082/customers/" + customerId, String.class);
    }
    
    public String getCustomerDetailFallback(String customerId, Throwable ex) {
        System.out.println("Error: {}" + ex.getMessage());
        return "고객정보 조회가 지연되고 있습니다.";
    }
}
