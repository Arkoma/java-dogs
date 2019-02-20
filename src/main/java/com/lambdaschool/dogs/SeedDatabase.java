package com.lambdaschool.dogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeedDatabase {
    private static final Logger log = LoggerFactory.getLogger(SeedDatabase.class);

    @Bean
    public CommandLineRunner initDB(DogRepository repository) {
        return args -> {
            log.info("Seeding " + repository.save(new Dog("Springer",50,false)));
            log.info("Seeding " + repository.save(new Dog("Bulldog",50,true)));
            log.info("Seeding " + repository.save(new Dog("Collie",50,false)));
            log.info("Seeding " + repository.save(new Dog("Boston Terrier",35,true)));
            log.info("Seeding " + repository.save(new Dog("Corgie",35,true)));
        };
    }
}
