package com.lambdaschool.dogs;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Long> {
    Dog findByBreed(String breed);
    List<Dog> findByGoodForApartment(boolean goodForApartment);
}
