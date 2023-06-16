package egovframework.msa.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    
    @GetMapping("/{customerId}")
    public String getCustomerDetail(@PathVariable String customerId) throws Exception {
        System.out.println("request customerId : " + customerId);
        if(customerId.equalsIgnoreCase("error")) {
            throw new Exception("customerId 값은 error일 수 없습니다.");
        }
        return "[Customer id = " + customerId + " at " + System.currentTimeMillis() + "]";
    }
}
