package egovframework.msa.sample.controller;

import egovframework.msa.sample.service.CustomerApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/catalogs/customerinfo")
public class CatalogsController {
    
    private final CustomerApiService customerApiService;
    
    @GetMapping(path = "/{customerId}")
    public String getCustomerInfo(@PathVariable String customerId) {
        String customerInfo = customerApiService.getCustomerDetail(customerId);
        
        String retVal = String.format("[Customer id = %s at %s %s ]", customerId,
                System.currentTimeMillis(), customerInfo);
        
        log.info(retVal);
        
        return retVal;
    }
}
