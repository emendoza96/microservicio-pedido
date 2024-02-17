package com.microservice.order.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import com.microservice.order.dao.OrderDetailRepository;
import com.microservice.order.dao.OrderRepository;
import com.microservice.order.dao.OrderStateRepository;
import com.microservice.order.domain.Construction;
import com.microservice.order.domain.Material;
import com.microservice.order.domain.Order;
import com.microservice.order.domain.OrderDetail;
import com.microservice.order.domain.OrderState;



@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderDetailRepository detailRepository;

    @Mock
    private OrderStateRepository stateRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        //order.setDetail(List.of(new OrderDetail(), new OrderDetail()));

        Material material1 = new Material();
        material1.setCurrentStock(50);
        material1.setPrice(5.8d);
        Material material2 = new Material();
        material2.setCurrentStock(40);
        material2.setPrice(10d);

        OrderDetail detail1 = new OrderDetail();
        detail1.setMaterial(material1);
        detail1.setQuantity(30);
        OrderDetail detail2 = new OrderDetail();
        detail2.setMaterial(material2);
        detail2.setQuantity(20);
        order.setDetail(List.of(detail1, detail2));

    }

    @Test
    void testCreateOrder() {
        //given
        int idOrder = 1;

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(idOrder);
            return savedOrder;
        });
        when(stateRepository.findByState(anyString())).thenReturn(new OrderState());

        //when
        Order newOrder = orderService.createOrder(order);

        //then
        assertThat(newOrder.getId()).isEqualTo(idOrder);
        assertThat(newOrder.getDetail().size()).isEqualTo(2);
    }

    @Test
    void testCreateOrderDetail() {
        //given
        OrderDetail detail = new OrderDetail();
        detail.setPrice(44d);
        detail.setQuantity(20);
        order.setId(1);

        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(detailRepository.save(any(OrderDetail.class))).thenReturn(detail);

        //when
        OrderDetail newDetail = orderService.createOrderDetail(order.getId(), detail);

        //then
        assertThat(newDetail.getOrder().getId()).isEqualTo(order.getId());
        assertThat(newDetail.getPrice()).isEqualTo(44d);
        assertThat(newDetail.getQuantity()).isEqualTo(20);
    }

    @Test
    void testDeleteOrderById() {
        //given
        order.setId(1);
        doNothing().when(orderRepository).deleteById(any());

        //when
        orderService.deleteOrderById(order.getId());

        //then
        verify(orderRepository, times(1)).deleteById(order.getId());
    }

    @Test
    void testDeleteOrderDetailById() {
        //given
        int detailId = 1;
        doNothing().when(detailRepository).deleteById(detailId);

        //when
        orderService.deleteOrderDetailById(detailId);

        //then
        verify(detailRepository, times(1)).deleteById(detailId);
    }

    @Test
    void testGetAllOrders() {
        //given
        Order order1 = new Order();
        when(orderRepository.findAll()).thenReturn(List.of(order, order1));

        //when
        List<Order> orders = orderService.getAllOrders();

        //then
        assertThat(orders.size()).isEqualTo(2);
    }

    @Test
    void testGetOrderByConstructionId() {
        //given
        int constructionId = 1;

        Order order1 = new Order();

        Construction construction = new Construction();
        construction.setId(constructionId);

        order.setConstruction(construction);
        order1.setConstruction(construction);

        when(orderRepository.findOrderByConstructionId(any())).thenReturn(List.of(order, order1));

        //when
        List<Order> orders = orderService.getOrderByConstructionId(constructionId);

        //then
        assertThat(orders.size()).isEqualTo(2);
        assertTrue(orders.stream().allMatch(o -> o.getConstruction().getId() == constructionId));
    }

    @Test
    void testGetOrderById() {
        //given
        int orderId = 1;
        order.setId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        //when
        Optional<Order> result = orderService.getOrderById(orderId);

        //then
        assertThat(result).isNotEmpty();
        assertThat(result.get().getId()).isEqualTo(orderId);
    }

    @Test
    void testGetOrderDetailById() {
        //given
        int detailId = 1;

        OrderDetail detail = new OrderDetail();
        detail.setId(detailId);
        detail.setOrder(order);

        when(detailRepository.findById(detailId)).thenReturn(Optional.of(detail));

        //when
        Optional<OrderDetail> result = orderService.getOrderDetailById(detailId);

        //then
        assertThat(result).isNotEmpty();
        assertThat(result.get().getId()).isEqualTo(detailId);
    }

    @Test
    void testSetStatusAcceptedToOrder() {
        //given
        OrderState state = new OrderState();
        state.setState("CONFIRMED");
        order.setState(state);

        OrderState stateAccepted = new OrderState();
        stateAccepted.setState("ACCEPTED");

        when(stateRepository.findByState("ACCEPTED")).thenReturn(stateAccepted);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            return savedOrder;
        });

        //when
        Order orderResult = orderService.setOrderStatus(order);

        //then
        assertThat(orderResult.getState().getState()).isEqualTo("ACCEPTED");
    }

    @Test
    void testSetStatusPendingToOrder() {
        //given
        OrderState state = new OrderState();
        state.setState("CONFIRMED");
        order.setState(state);

        OrderDetail detail = order.getDetail().get(0);
        detail.setQuantity(3000);

        OrderState statePending = new OrderState();
        statePending.setState("PENDING");

        when(stateRepository.findByState("PENDING")).thenReturn(statePending);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            return savedOrder;
        });

        //when
        Order orderResult = orderService.setOrderStatus(order);

        //then
        assertThat(orderResult.getState().getState()).isEqualTo("PENDING");
    }

    @Test
    void testSendMessageToOrdersQueue() {
        //given
        when(kafkaTemplate.send(anyString(),  anyString())).thenReturn(null);

        //when
        orderService.sendMessageToOrdersQueue(order);

        //then
        verify(kafkaTemplate, times(1)).send(anyString(), anyString());
    }
}
