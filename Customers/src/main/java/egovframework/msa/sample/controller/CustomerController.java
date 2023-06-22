package egovframework.msa.sample.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/customers")
public class CustomerController {
    
    @GetMapping("/{customerId}")
    public String getCustomerDetail(@PathVariable String customerId) throws Exception {
        log.info("request customerId : {}", customerId);
        if(customerId.equalsIgnoreCase("error")) {
            log.error("I/O Exception");
            throw new Exception("customerId 값은 error일 수 없습니다.");
        }
        return "[Customer id = " + customerId + " at " + System.currentTimeMillis() + "]";
    }
}
