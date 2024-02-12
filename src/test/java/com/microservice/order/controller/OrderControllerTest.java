package com.microservice.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.order.domain.Construction;
import com.microservice.order.domain.Order;
import com.microservice.order.domain.OrderDetail;
import com.microservice.order.service.impl.OrderServiceImpl;

@WebMvcTest
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderServiceImpl orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private Order order;

    @BeforeEach
    void setUp() {
        order = new Order();

        OrderDetail detail1 = new OrderDetail();
        detail1.setId(1);
        detail1.setOrder(order);
        OrderDetail detail2 = new OrderDetail();
        detail2.setId(2);
        detail2.setOrder(order);

        order.setDetail(List.of(detail1, detail2));
    }

    @Test
    void testDeleteOrderById() throws Exception {
        //given
        int idOrder = 1;
        order.setId(idOrder);
        when(orderService.getOrderById(idOrder)).thenReturn(Optional.of(order));
        doNothing().when(orderService).deleteOrderById(idOrder);

        //when
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/order/{id}", idOrder)
            .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).deleteOrderById(idOrder);
    }

    @Test
    void testDeleteOrderDetailById() throws Exception {
        //given
        int idOrder = 1;
        order.setId(idOrder);

        int idDetail = order.getDetail().get(0).getId();

        when(orderService.getOrderById(idOrder)).thenReturn(Optional.of(order));
        doNothing().when(orderService).deleteOrderDetailById(idDetail);

        //when
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/order/{orderID}/detail/{detailID}", idOrder, idDetail)
            .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());

        verify(orderService, times(1)).deleteOrderDetailById(idDetail);
    }

    @Test
    void testEditOrder() throws Exception {
        //given
        int idOrder = 1;
        order.setId(idOrder);
        order.setDetail(null);

        Order order1 = new Order();
        order1.setId(idOrder);
        order1.setDetail(List.of(new OrderDetail()));
        order1.setOrderDate(Instant.now());

        when(orderService.getOrderById(any())).thenReturn(Optional.of(order));
        when(orderService.createOrder(any(Order.class))).thenReturn(order1);

        String jsonResult = objectMapper.writeValueAsString(order);

        //when
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/order/edit/{id}", idOrder)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonResult)
        );

        //then
        response.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idOrder))
        .andExpect(MockMvcResultMatchers.jsonPath("$.detail").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$.orderDate").isNotEmpty());

    }

    @Test
    void testGetAllOrders() throws Exception {
        //given
        Order order1 = new Order();
        order1.setId(2);
        order1.setDetail(List.of(new OrderDetail()));
        order1.setOrderDate(Instant.now());

        when(orderService.getAllOrders()).thenReturn(List.of(order, order1));

        //when
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/order")
            .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[1].orderDate").isNotEmpty());
    }

    @Test
    void testGetOrderByConstruction() throws Exception {
        //given
        int constructionId = 1;

        Order order1 = new Order();

        Construction construction = new Construction();
        construction.setId(constructionId);
        construction.setDescription("Test description");

        order.setConstruction(construction);
        order1.setConstruction(construction);

        when(orderService.getOrderByConstructionId(constructionId)).thenReturn(List.of(order, order1));

        //when
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/order/construction/{id}", constructionId)
            .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].construction").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].construction.description").value(construction.getDescription()));
    }

    @Test
    void testGetOrderByDetailId() throws Exception {
        //given
        int idOrder = 1;
        int idDetail = order.getDetail().get(0).getId();

        order.setId(idOrder);

        when(orderService.getOrderDetailById(idDetail)).thenReturn(Optional.of(order.getDetail().get(0)));

        //when
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/order/{idOrder}/detail/{idDetail}", idOrder, idDetail)
            .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idDetail));
    }

    @Test
    void testGetOrderById() throws Exception {
        //given
        int idOrder = 1;
        order.setId(idOrder);

        when(orderService.getOrderById(idOrder)).thenReturn(Optional.of(order));

        //when
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.get("/api/order/{id}", idOrder)
            .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        response.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idOrder));
    }

    @Test
    void testSaveOrder() throws Exception {
        Order order1 = new Order();
        order1.setId(3);
        order1.setDetail(List.of(new OrderDetail()));
        order1.setOrderDate(Instant.now());

        //given
        when(orderService.createOrder(any(Order.class))).thenReturn(order1);

        String jsonResult = objectMapper.writeValueAsString(order1);

        //when
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/order")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonResult)
        );

        //then
        response.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        .andExpect(MockMvcResultMatchers.jsonPath("$.detail").isNotEmpty())
        .andExpect(MockMvcResultMatchers.jsonPath("$.orderDate").isNotEmpty());

    }

    @Test
    void testSaveDetailOrder() throws Exception {
        int idOrder = 2;

        Order order1 = new Order();
        order1.setId(idOrder);

        OrderDetail detail = new OrderDetail();
        detail.setPrice(33d);
        detail.setQuantity(22);

        OrderDetail detailResult = new OrderDetail();
        detailResult.setOrder(order1);

        //given
        when(orderService.createOrderDetail(any(), any(OrderDetail.class))).thenReturn(detailResult);

        String jsonResult = objectMapper.writeValueAsString(detail);

        //when
        ResultActions response = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/order/{idOrder}/detail", idOrder)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonResult)
        );

        //then
        response.andDo(MockMvcResultHandlers.print())
        .andExpect(MockMvcResultMatchers.status().isOk());
        // .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNumber())
        // .andExpect(MockMvcResultMatchers.jsonPath("$.detail").isNotEmpty())
        // .andExpect(MockMvcResultMatchers.jsonPath("$.orderDate").isNotEmpty());
    }

}