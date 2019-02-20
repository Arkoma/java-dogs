package com.lambdaschool.dogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.core.DummyInvocationUtils.methodOn;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

@RestController
public class DogController {
    private static final Logger log = LoggerFactory.getLogger(DogController.class);
    private final DogRepository repository;
    private final DogResourceAssembler assembler;

    public DogController(DogRepository repository, DogResourceAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("dogs/breeds")
    public Resources<Resource<Dog>> allByBreed() {
        List<Resource<Dog>> dogs = repository.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());
        dogs.sort((d1, d2) -> d1.getContent().getBreed().compareToIgnoreCase(d2.getContent().getBreed()));
        return new Resources<>(dogs,
                linkTo(methodOn(DogController.class).allByBreed()).withSelfRel());
    }

    @GetMapping("dogs/weight")
    public Resources<Resource<Dog>> allByWeight() {
        List<Resource<Dog>> dogs = repository.findAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());
        dogs.sort((d1, d2) -> d1.getContent().getAvgWeight() - d2.getContent().getAvgWeight());
        return new Resources<>(dogs,
                linkTo(methodOn(DogController.class).allByWeight()).withSelfRel());
    }

    @GetMapping("dogs/breeds/{breed}")
    public Resource<Dog> showBreed(@PathVariable String breed) {
        try {
            Dog foundDog = repository.findByBreed(breed);
            return assembler.toResource(foundDog);
        } catch(Exception e) {
           throw new DogNotFoundException(breed);
        }
    }

    @GetMapping("dogs/apartment")
    public Resources<Resource<Dog>> allGoodForApartment(boolean goodForApartment) {
        goodForApartment = true;
        List<Resource<Dog>> dogs = repository.findByGoodForApartment(true).stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(dogs,
                linkTo(methodOn(DogController.class).allGoodForApartment(true)).withSelfRel());
    }

    @PutMapping("dogs/{id}")
    public ResponseEntity<?> putDog(@RequestBody Dog d, @PathVariable Long id) throws URISyntaxException {
        Dog updatedDog = repository.findById(id)
                .map(dog -> {
                    dog.setBreed(d.getBreed());
                    dog.setAvgWeight(d.getAvgWeight());
                    dog.setGoodForApartment(d.isGoodForApartment());
                    return repository.save(dog);
                })
                .orElseGet(() -> {
                    d.setId(id);
                    return repository.save(d);
                });
        Resource<Dog> resource = assembler.toResource(updatedDog);
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }
}
