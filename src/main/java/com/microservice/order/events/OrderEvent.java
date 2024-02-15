package com.microservice.order.events;

public record OrderEvent(String orderNumber, String status) {

}
