package com.microservice.order.domain;

public class Material {

    private Integer id;
    private String description;
    private Double price;

    public Material() {}

    public Integer getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Material [id=" + id + ", description=" + description + ", price=" + price + "]";
    }

}
