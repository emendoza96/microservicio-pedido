package com.microservice.order.controller;

import org.springframework.web.bind.annotation.RestController;

import com.microservice.order.domain.Order;
import com.microservice.order.domain.OrderDetail;
import com.microservice.order.service.OrderService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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
@ApiOperation(value = "OrderRest")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    @ApiOperation(value = "Get all orders")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Orders successfully retrieved"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order not found")
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
    @ApiOperation(value = "Get order by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order successfully retrieved"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order not found")
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
    @ApiOperation(value = "Get orders by construction id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Orders successfully retrieved"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order not found")
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
    @ApiOperation(value = "Get order by order detail id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order successfully retrieved"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order not found")
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
    @ApiOperation(value = "Create a new order")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order successfully created"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
    })
    public ResponseEntity<Order> saveOrder(@RequestBody Order order) {

       try {
            Order newOrder = orderService.createOrder(order);
            return ResponseEntity.status(201).body(newOrder);
       } catch (Exception e) {
            System.err.println(e.getMessage());
            return ResponseEntity.badRequest().build();
       }
    }

    @PostMapping("/{idOrder}/detail")
    @ApiOperation(value = "Create a new order detail")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order detail successfully created"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
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
    @ApiOperation(value = "Edit order")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order successfully edited"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order not found")
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
    @ApiOperation(value = "Delete order by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order successfully deleted"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order not found")
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
    @ApiOperation(value = "Delete order detail by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order detail successfully deleted"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order detail not found")
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
}
