package nl.artemy.abnamroassignment2022;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class RecipeApplication {

    public static void main(final String[] args) {
        SpringApplication.run(RecipeApplication.class, args);
    }

}
