package com.microservice.order.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.microservice.order.dao.OrderDetailRepository;
import com.microservice.order.dao.OrderRepository;
import com.microservice.order.dao.OrderStateRepository;
import com.microservice.order.domain.Order;
import com.microservice.order.domain.OrderDetail;
import com.microservice.order.helpers.OrderEventHelper;
import com.microservice.order.service.OrderService;
import com.microservice.order.util.JsonUtils;

@Service
public class OrderServiceImpl implements OrderService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository detailRepository;

    @Autowired
    private OrderStateRepository stateRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Optional<Order> getOrderById(Integer id) {
        return orderRepository.findById(id);
    }

    @Override
    public List<Order> getOrderByConstructionId(Integer id) {
        return orderRepository.findOrderByConstructionId(id);
    }

    @Override
    public Optional<OrderDetail> getOrderDetailById(Integer id) {
        return detailRepository.findById(id);
    }

    @Override
    public Order createOrder(Order order) {
        order.setState(stateRepository.findByState("NEW"));
        return orderRepository.save(order);
    }

    @Override
    public OrderDetail createOrderDetail(Integer idOrder, OrderDetail detail) {
        Order order = orderRepository.findById(idOrder).get();
        detail.setOrder(order);
        return detailRepository.save(detail);
    }

    @Override
    public void deleteOrderById(Integer id) {
        orderRepository.deleteById(id);
    }

    @Override
    public void deleteOrderDetailById(Integer id) {
        detailRepository.deleteById(id);
    }

    @Override
    public Order confirmOrder(Order order) {
        order.setState(stateRepository.findByState("CONFIRMED"));
        return orderRepository.save(order);
    }

    @Override
    public Boolean validateOrder(Order order) {
        Boolean hasConstruction = order.getConstruction() != null && order.getConstruction().getId() != null;
        Boolean hasDetails = order.getDetail().stream().allMatch(d -> d.getMaterial() != null && d.getQuantity() != null);
        Boolean hasOrderDate = order.getOrderDate() != null;

        return hasConstruction && hasDetails && hasOrderDate;
    }

    @Override
    public Order setOrderStatus(Order order) {
        Boolean hasStock = true;

        for(OrderDetail detail : order.getDetail()) {
            if (detail.getQuantity() > detail.getMaterial().getCurrentStock()) hasStock = false;
        }

        order.setState(hasStock ? stateRepository.findByState("ACCEPTED") : stateRepository.findByState("EARRING"));

        return orderRepository.save(order);
    }

    @Override
    public void sendMessageToOrdersQueue(Order order) {
        List<OrderEventHelper> ordersHelper = new ArrayList<>();

        for(OrderDetail detail : order.getDetail()) {
            ordersHelper.add(
                new OrderEventHelper(
                    order.getId(),
                    detail.getId(),
                    detail.getQuantity(),
                    detail.getPrice(),
                    detail.getMaterial().getId()
                )
            );
        }

        kafkaTemplate.send("builder-yard-orders", JsonUtils.toJson(ordersHelper));
    }


}
