package egovframework.msa.sample.serviceImpl;

import egovframework.msa.sample.service.CustomerApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CustomerApiServiceImpl implements CustomerApiService {
    
    private final RestTemplate restTemplate;
    
    @Override
    public String getCustomerDetail(String customerId) {
        return restTemplate.getForObject("http://localhost:8082/customers/" + customerId, String.class);
    }
}
