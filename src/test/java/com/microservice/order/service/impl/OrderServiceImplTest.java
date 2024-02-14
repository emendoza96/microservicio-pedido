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

import com.microservice.order.dao.OrderDetailRepository;
import com.microservice.order.dao.OrderRepository;
import com.microservice.order.dao.OrderStateRepository;
import com.microservice.order.domain.Construction;
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

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setDetail(List.of(new OrderDetail(), new OrderDetail()));
    }

    @Test
    void testCreateOrder() {
        //given
        order.setId(1);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(stateRepository.findByState(anyString())).thenReturn(new OrderState());

        //when
        Order newOrder = orderService.createOrder(order);

        //then
        assertThat(newOrder.getId()).isEqualTo(order.getId());
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
}
