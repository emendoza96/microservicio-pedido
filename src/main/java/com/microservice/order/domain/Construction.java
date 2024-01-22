package com.microservice.order.domain;

public class Construction {

    private Integer id;
    private String description;

    public Construction() {}

    public Integer getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Construction [id=" + id + ", description=" + description + "]";
    }

}
