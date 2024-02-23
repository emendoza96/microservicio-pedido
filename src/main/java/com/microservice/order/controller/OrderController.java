package com.microservice.order.controller;

import org.springframework.web.bind.annotation.RestController;

import com.microservice.order.domain.Order;
import com.microservice.order.domain.OrderDetail;
import com.microservice.order.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;






@RestController
@RequestMapping("/api/order")
@Tag(name = "OrderRest")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders successfully retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<List<Order>> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok().body(orders);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order successfully retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        try {
            Order order = orderService.getOrderById(id).orElseThrow();
            return ResponseEntity.ok().body(order);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/construction/{id}")
    @Operation(summary = "Get orders by construction id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders successfully retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<List<Order>> getOrderByConstruction(@PathVariable Integer id) {
        try {
            List<Order> orders = orderService.getOrderByConstructionId(id);
            return ResponseEntity.ok().body(orders);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{idOrder}/detail/{id}")
    @Operation(summary = "Get order by order detail id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order successfully retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Order> getOrderByDetailId(@PathVariable Integer idOrder, @PathVariable Integer id) {
        try {
            Order order = orderService.getOrderDetailById(id).orElseThrow().getOrder();

            if(order.getId() != idOrder) new NoSuchElementException();

            return ResponseEntity.ok().body(order);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping()
    @Operation(summary = "Create a new order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order successfully created"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    public ResponseEntity<Order> saveOrder(@RequestBody Order order) {

       try {
            if(!orderService.validateOrder(order)) throw new Exception("Bad data");

            Order newOrder = orderService.createOrder(order);
            return ResponseEntity.status(201).body(newOrder);
       } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
       }
    }

    @PostMapping("/{idOrder}/detail")
    @Operation(summary = "Create a new order detail")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order detail successfully created"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    public ResponseEntity<Order> saveDetailOrder(@PathVariable Integer idOrder, @RequestBody OrderDetail detail) {

        try {
            OrderDetail detailResult = orderService.createOrderDetail(idOrder, detail);
            return ResponseEntity.ok().body(detailResult.getOrder());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/edit/{id}")
    @Operation(summary = "Edit order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order successfully edited"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Order> editOrder(@PathVariable Integer id, @RequestBody Order order) {

        try {
            orderService.getOrderById(id).orElseThrow();
            Order orderResult = orderService.createOrder(order);
            return ResponseEntity.ok().body(orderResult);
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Object> deleteOrderById(@PathVariable Integer id){

        try {
            orderService.getOrderById(id).orElseThrow();
            orderService.deleteOrderById(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{idOrder}/detail/{id}")
    @Operation(summary = "Delete order detail by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order detail successfully deleted"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Order detail not found")
    })
    public ResponseEntity<Object> deleteOrderDetailById(@PathVariable Integer idOrder, @PathVariable Integer id){

        try {
            orderService.getOrderById(idOrder).orElseThrow();
            orderService.deleteOrderDetailById(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @PutMapping("/confirm/{id}")
    @Operation(summary = "Confirm an order and create an event")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order successfully confirmed"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<String> confirmOrder(@PathVariable Integer id){

        try {
            Order order = orderService.getOrderById(id).orElseThrow();
            order = orderService.confirmOrder(order);
            order = orderService.setOrderStatus(order);

            if (order.getState().getState().equals("ACCEPTED")) {
                orderService.sendMessageToOrdersQueue(order);
            }

            return ResponseEntity.ok().body("Order " + order.getState().getState());
        } catch (NoSuchElementException e) {
            System.err.println(e.getMessage());
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
