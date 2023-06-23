package egovframework.msa.sample.configclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "egovframework")
@EnableDiscoveryClient
public class ConfigClientApplication {
    
    public static void main(String[] args) throws InterruptedException {
        String profile = System.getProperty("spring.profiles.active");
        if(profile == null) {
            System.setProperty("spring.profiles.active", "dev");
        }
        SpringApplication.run(ConfigClientApplication.class, args);
    }
    
}
