package com.microservice.order.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.microservice.order.dao.ConstructionRepository;
import com.microservice.order.dao.MaterialRepository;
import com.microservice.order.dao.OrderRepository;
import com.microservice.order.domain.Construction;
import com.microservice.order.domain.Material;
import com.microservice.order.domain.Order;
import com.microservice.order.domain.OrderDetail;
import com.microservice.order.domain.OrderState;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerRestTemplateTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ConstructionRepository constructionRepository;

    @Autowired
    private MaterialRepository materialRepository;

    private Order order;
    private Order order2;
    private Construction construction1;
    private Construction construction2;
    private Material material1;
    private Material material2;

    @BeforeEach
    void setUp() {

        // First order:

        order = Order.builder()
            .orderDate(Instant.now())
            .build();

        material1 = Material.builder()
            .id(1)
            .description("Brick")
            .currentStock(100)
            .price(5.5)
            .build();

        material2 = Material.builder()
            .id(2)
            .description("Brick 2")
            .currentStock(200)
            .price(8.5)
            .build();

        OrderDetail detail1 = OrderDetail.builder()
            .order(order)
            .quantity(40)
            .price(5.5)
            .material(material1)
            .build();

        OrderDetail detail2 = OrderDetail.builder()
            .order(order)
            .quantity(60)
            .price(8.5)
            .material(material2)
            .build();

        construction1 = Construction.builder()
            .id(1)
            .description("Test desc")
            .build();

        order.setDetail(List.of(detail1, detail2));
        order.setConstruction(construction1);

        // Second order:

        order2 = Order.builder()
            .orderDate(Instant.now())
            .build();

        OrderDetail detail3 = OrderDetail.builder()
            .order(order2)
            .quantity(40)
            .price(5.5)
            .material(material1)
            .build();

        construction2 = Construction.builder()
            .id(2)
            .description("Test desc 2")
            .build();

        order2.setDetail(List.of(detail3));
        order2.setConstruction(construction2);
    }

    @Test
    void testGetAllOrders() {
        //given
        materialRepository.save(material1);
        materialRepository.save(material2);
        constructionRepository.save(construction1);
        constructionRepository.save(construction2);
        orderRepository.save(order);
        orderRepository.save(order2);

        //when
        ResponseEntity<Order[]> response = restTemplate.getForEntity("/api/order", Order[].class);
        List<Order> orders = Arrays.asList(response.getBody());

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(orders.size()).isGreaterThan(0);
    }

    @Test
    void testGetOrderByConstruction() {
        //given
        int idConstructionSuccess = 1;
        materialRepository.save(material1);
        materialRepository.save(material2);
        constructionRepository.save(construction1); // Construction with id = 1
        constructionRepository.save(construction2); // Construction with id = 2
        orderRepository.save(order); // Order for Construction with id = 1
        orderRepository.save(order2); // Order for Construction with id = 2

        //when
        ResponseEntity<Order[]> response = restTemplate.getForEntity("/api/order/construction/{id}", Order[].class, idConstructionSuccess);
        List<Order> orders = Arrays.asList(response.getBody());

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(orders.size()).isGreaterThan(0);
    }

    @Test
    void testGetOrderByConstructionEmpty() {
        //given
        int idConstructionEmpty = 434;
        materialRepository.save(material1);
        materialRepository.save(material2);
        constructionRepository.save(construction1); // Construction with id = 1
        constructionRepository.save(construction2); // Construction with id = 2
        orderRepository.save(order); // Order for Construction with id = 1
        orderRepository.save(order2); // Order for Construction with id = 2

        //when
        ResponseEntity<Order[]> response = restTemplate.getForEntity("/api/order/construction/{id}", Order[].class, idConstructionEmpty);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void testGetOrderByDetailId() {
        // given
        int idOrder = 1;
        int idDetail = 1;
        materialRepository.save(material1);
        materialRepository.save(material2);
        constructionRepository.save(construction1);
        constructionRepository.save(construction2);
        orderRepository.save(order);
        orderRepository.save(order2);

        //when
        ResponseEntity<Order> response = restTemplate.getForEntity(
            "/api/order/{idOrder}/detail/{idDetail}",
            Order.class,
            idOrder,
            idDetail
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(idOrder);
    }

    @Test
    void testGetOrderById() {
        // given
        int idOrder = 1;
        materialRepository.save(material1);
        materialRepository.save(material2);
        constructionRepository.save(construction1);
        constructionRepository.save(construction2);
        orderRepository.save(order);
        orderRepository.save(order2);

        //when
        ResponseEntity<Order> response = restTemplate.getForEntity(
            "/api/order/{id}",
            Order.class,
            idOrder
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(idOrder);
    }

    @Test
    void testSaveOrder() {
        //given
        materialRepository.save(material1);
        materialRepository.save(material2);
        constructionRepository.save(construction1);
        constructionRepository.save(construction2);

        //when
        ResponseEntity<Order> response = restTemplate.postForEntity("/api/order", order, Order.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(MediaType.APPLICATION_JSON).isEqualTo(response.getHeaders().getContentType());

        Order newOrder = response.getBody();
        assertThat(newOrder.getDetail().size()).isEqualTo(2);
        assertThat(newOrder.getDetail().get(0).getPrice()).isEqualTo(5.5d);
        assertThat(newOrder.getDetail().get(0).getQuantity()).isEqualTo(40);
        assertThat(newOrder.getDetail().get(0).getMaterial().getId()).isEqualTo(1);
        assertThat(newOrder.getConstruction().getId()).isEqualTo(1);
    }

    @Test
    void testSaveDetailOrder() {
        //given
        int idOrder = 1;
        Material material = new Material();
        material.setId(1);
        Construction construction = new Construction();
        construction.setId(1);
        OrderState state = new OrderState();
        state.setId(1);
        OrderDetail detail = new OrderDetail();
        detail.setMaterial(material);
        detail.setPrice(3.3);
        detail.setQuantity(60);

        HttpEntity<OrderDetail> requestEntity = new HttpEntity<>(detail);

        //when
        ResponseEntity<Order> response = restTemplate.exchange(
            "/api/order/{idOrder}/detail",
            HttpMethod.POST,
            requestEntity,
            Order.class,
            idOrder
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Order orderResult = response.getBody();
        assertThat(orderResult.getDetail().size()).isGreaterThan(1);
    }

    @Test
    void testDeleteOrderById() {
        //given
        Material material = new Material();
        material.setId(1);
        Construction construction = new Construction();
        construction.setId(1);
        OrderState state = new OrderState();
        state.setId(1);
        OrderDetail detail = new OrderDetail();
        detail.setMaterial(material);
        detail.setPrice(5.5);
        detail.setQuantity(60);


        Order order = new Order();
        order.setOrderDate(Instant.now());
        order.setConstruction(construction);
        order.setDetail(List.of(detail));

        ResponseEntity<Order> response0 = restTemplate.postForEntity("/api/order", order, Order.class);
        Order newOrder = response0.getBody();

        //when
        ResponseEntity<Object> response = restTemplate.exchange(
            "/api/order/{id}",
            HttpMethod.DELETE,
            null,
            Object.class,
            newOrder.getId()
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void testDeleteOrderDetailById() {

    }

    @Test
    void testEditOrder() {
        //given
        int idOrder = 1;
        Order order = new Order();
        order.setOrderDate(null);

        HttpEntity<Order> requestEntity = new HttpEntity<>(order);

        //when
        ResponseEntity<Order> response = restTemplate.exchange(
            "/api/order/edit/{id}",
            HttpMethod.PUT,
            requestEntity,
            Order.class,
            idOrder
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getOrderDate()).isNull();
    }

    @Test
    @Disabled
    void confirmOrder() {
        //given
        int idOrder = 1;

        //when
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/order/confirm/{id}",
            HttpMethod.PUT,
            null,
            String.class,
            idOrder
        );

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Order confirmed");
    }
}
