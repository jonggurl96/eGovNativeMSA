package egovframework.msa.sample.customers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableEurekaClient
@ComponentScan(basePackages = "egovframework")
public class CustomersApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CustomersApplication.class, args);
    }
    
}
