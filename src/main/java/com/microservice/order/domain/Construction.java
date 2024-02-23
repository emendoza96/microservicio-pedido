package com.microservice.order.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@AllArgsConstructor
@Builder
@Table(name = "construction")
public class Construction {

    @Id
    private Integer id;
    private String description;

    public Construction() {}

    public Integer getId() {
        return id;
    }
    public String getDescription() {
        return description;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Construction [id=" + id + ", description=" + description + "]";
    }

}
