package com.microservice.order.service;

import java.util.List;
import java.util.Optional;

import com.microservice.order.domain.Order;
import com.microservice.order.domain.OrderDetail;
import com.microservice.order.error.ErrorDetails;

public interface OrderService {

    public List<Order> getAllOrders();
    public Optional<Order> getOrderById(Integer id);
    public List<Order> getOrderByConstructionId(Integer id);
    public Optional<OrderDetail> getOrderDetailById(Integer id);
    public Order createOrder(Order order);
    public OrderDetail createOrderDetail(Integer idOrder, OrderDetail detail);
    public void deleteOrderById(Integer id);
    public void deleteOrderDetailById(Integer id);
    public Order confirmOrder(Order order);
    public Order setOrderStatus(Order order);
    public void sendMessageToOrdersQueue(Order order);
    public ErrorDetails getErrors(Order order);

}
