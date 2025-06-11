package com.huydev.skipli_be;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication(scanBasePackages={"com"})
@EntityScan(basePackages = {"com"})
public class SkipliBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SkipliBeApplication.class, args);
    }
}
