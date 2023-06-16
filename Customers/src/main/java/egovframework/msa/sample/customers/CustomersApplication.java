package egovframework.msa.sample.customers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "egovframework")
public class CustomersApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CustomersApplication.class, args);
    }
    
}
