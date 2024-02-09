package com.microservice.order.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.microservice.order.domain.Order;
import com.microservice.order.domain.OrderDetail;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setDetail(List.of(new OrderDetail(), new OrderDetail()));;
    }

    @Test
    void testSaveOrder() {
        //given
        Order order1 = new Order();
        order.setDetail(List.of(new OrderDetail()));

        //when
        Order newOrder = orderRepository.save(order1);

        //then
        assertNotNull(newOrder);
        assert(newOrder.getId() > 0);
    }

    @Test
    void testFindAllOrders() {
        //given
        Order order1 = new Order();
        order.setDetail(List.of(new OrderDetail()));
        orderRepository.save(order);
        orderRepository.save(order1);

        //when
        List<Order> orders = orderRepository.findAll();

        //then
        assert(!orders.isEmpty());
        assertThat(orders.size()).isEqualTo(2);
    }
}
