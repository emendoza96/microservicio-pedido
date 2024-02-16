package com.microservice.order.helpers;

public class OrderEventHelper{

    Integer idOrder;
    Integer idDetail;
    Integer quantity;
    Double price;
    Integer idMaterial;

    public OrderEventHelper(Integer idOrder, Integer idDetail, Integer quantity, Double price, Integer idMaterial) {
        this.idOrder = idOrder;
        this.idDetail = idDetail;
        this.quantity = quantity;
        this.price = price;
        this.idMaterial = idMaterial;
    }

    public Integer getIdOrder() {
        return idOrder;
    }

    public Integer getIdDetail() {
        return idDetail;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getIdMaterial() {
        return idMaterial;
    }

    @Override
    public String toString() {
        return "OrderEventHelper [idOrder=" + idOrder + ", idDetail=" + idDetail + ", quantity=" + quantity + ", price="
                + price + ", idMaterial=" + idMaterial + "]";
    }

}