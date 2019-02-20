package com.lambdaschool.dogs;

public class DogNotFoundException extends RuntimeException {
    public DogNotFoundException(Long id) {
        super("Could not find dog by id " + id);
    }
    public DogNotFoundException(String breed) {
        super("Could not find dog by breed " + breed);
    }
}
