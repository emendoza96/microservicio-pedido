package com.microservice.order.dao;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.microservice.order.domain.Construction;
import com.microservice.order.domain.Order;
import com.microservice.order.domain.OrderDetail;

@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ConstructionRepository constructionRepository;

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
        order1.setDetail(List.of(new OrderDetail()));

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
        order1.setDetail(List.of(new OrderDetail()));
        orderRepository.save(order);
        orderRepository.save(order1);

        //when
        List<Order> orders = orderRepository.findAll();

        //then
        assert(!orders.isEmpty());
        assertThat(orders.size()).isGreaterThan(1);
    }

    @Test
    void testFindOrderById() {
        // given
        Order order1 = new Order();
        order1.setDetail(List.of(new OrderDetail()));
        orderRepository.save(order);
        Order orderResult = orderRepository.save(order1);

        //when
        Optional<Order> orderRequested = orderRepository.findById(orderResult.getId());

        //then
        assert(orderRequested.isPresent());
        assertThat(orderRequested.get().getId()).isEqualTo(orderResult.getId());

    }

    @Test
    void testFindByOrderId() {
        // given
        Order order1 = new Order();
        order1.setDetail(List.of(new OrderDetail()));
        Order order2 = new Order();

        List<Construction> constructions = constructionRepository
            .saveAll(
                List.of(
                    Construction.builder().id(1).build(),
                    Construction.builder().id(2).build()
                )
            );

        order.setConstruction(constructions.get(0));
        order1.setConstruction(constructions.get(1));
        order2.setConstruction(constructions.get(1));
        orderRepository.save(order);
        orderRepository.save(order1);
        Order orderResult = orderRepository.save(order2);

        // when
        Integer constructionId = orderResult.getConstruction().getId();
        List<Order> orders = orderRepository.findOrderByConstructionId(constructionId);

        assertTrue(orders.stream().allMatch(o -> o.getConstruction().getId() == constructionId));

    }
}
