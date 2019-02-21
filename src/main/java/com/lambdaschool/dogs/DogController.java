package com.lambdaschool.dogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
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
            log.info("found dog " + foundDog.toString());
            return assembler.toResource(foundDog);
        } catch(Exception e) {
           throw new DogNotFoundException(breed);
        }
    }

    @GetMapping("dogs/apartment")
    public Resources<Resource<Dog>> allGoodForApartment() {
        List<Resource<Dog>> dogs = repository.findByGoodForApartment(true).stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());
        log.info("There are " + dogs.size() + " breeds that are good for apartments");
        return new Resources<>(dogs,
                linkTo(methodOn(DogController.class).allGoodForApartment()).withSelfRel());
    }

    @PutMapping("dogs/{id}")
    public ResponseEntity<?> putDog(@RequestBody Dog d, @PathVariable Long id) throws URISyntaxException {
        if (!isValid(d)) {
            log.debug("the Dog object from the request body is invalid");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
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
        log.info("saving " + updatedDog.toString());
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    @PostMapping("dogs/")
    public ResponseEntity<?> postDog(@RequestBody Dog d) throws URISyntaxException{
        if (!isValid(d)) {
            log.debug("the Dog object from the request body is invalid");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Dog updatedDog = repository.save(d);
        Resource<Dog> resource = assembler.toResource((updatedDog));
        log.info("saving " + updatedDog.toString());
        return ResponseEntity.created(new URI(resource.getId().expand().getHref())).body(resource);
    }

    @DeleteMapping("dogs/{id}")
    public ResponseEntity<?> deleteDog(@RequestBody Dog d, @PathVariable Long id) {
        try {
            Dog deletedDog = repository.findById(id).orElse(null);
            repository.deleteById(id);
            log.info("removed " + deletedDog.toString());
            Resource<Dog> resource = assembler.toResource((deletedDog));
            return new ResponseEntity<>(resource, HttpStatus.OK);
        } catch (DogNotFoundException dnfe) {
            throw new DogNotFoundException(id);
        }

    }

    @DeleteMapping("dogs/breeds/{breed}")
    public ResponseEntity<?> deleteDogByBreed(@PathVariable String breed) {
        try {
            Dog deletedDog = repository.findByBreed(breed);
            repository.deleteById(deletedDog.getId());
            log.info("removed " + deletedDog.toString());
            Resource<Dog> resource = assembler.toResource((deletedDog));
            return new ResponseEntity<>(resource, HttpStatus.OK);
        } catch(Exception e) {
            throw new DogNotFoundException(breed);
        }
    }

    private boolean isValid(Dog d) {
        Boolean apartment = d.isGoodForApartment();
        return d.getBreed() != null && d.getAvgWeight() >= 1 && apartment != null;
    }
}
