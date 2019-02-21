package com.lambdaschool.dogs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Dog {

    private @Id @GeneratedValue Long id;
    private @Column(unique = true) String breed;
    private int avgWeight;
    private boolean goodForApartment;

    public Dog(String breed, int avgWeight, boolean goodForApartment) {
        this.breed = breed;
        this.avgWeight = avgWeight;
        this.goodForApartment = goodForApartment;
    }

    public Dog() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return this.id;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public int getAvgWeight() {
        return avgWeight;
    }

    public void setAvgWeight(int avgWeight) {
        this.avgWeight = avgWeight;
    }

    public boolean isGoodForApartment() {
        return goodForApartment;
    }

    public void setGoodForApartment(boolean goodForApartment) {
        this.goodForApartment = goodForApartment;
    }

    @Override
    public String toString() {
        return "Dog{" +
                "id='" + id + '\'' +
                "breed='" + breed + '\'' +
                ", avgWeight=" + avgWeight +
                ", goodForApartment=" + goodForApartment +
                '}';
    }
}
