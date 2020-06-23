package com.application;

import java.io.IOException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LabelimgApplication {
  public static void main(String[] args) throws IOException {
    SpringApplication.run(LabelimgApplication.class, args);
    org.apache.log4j.BasicConfigurator.configure();
    RuntimeProcess.setup();
    
   
  }
}
