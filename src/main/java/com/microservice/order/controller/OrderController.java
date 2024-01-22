package com.microservice.order.controller;

import org.springframework.web.bind.annotation.RestController;

import com.microservice.order.domain.Order;
import com.microservice.order.domain.OrderDetail;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;






@RestController
@RequestMapping("/api/order")
@ApiOperation(value = "OrderRest")
public class OrderController {

    @GetMapping
    @ApiOperation(value = "Get all orders")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Orders successfully retrieved"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order not found")
    })
    public List<Order> getAllOrders() {
        return null;
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get order by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order successfully retrieved"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order not found")
    })
    public Order getOrderById(@PathVariable Integer id) {
        return null;
    }

    @GetMapping("/construction/{id}")
    @ApiOperation(value = "Get orders by construction id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Orders successfully retrieved"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order not found")
    })
    public List<Order> getOrderByConstruction(@PathVariable Integer id) {
        return null;
    }

    @GetMapping("/{idOrder}/detail/{id}")
    @ApiOperation(value = "Get order by order detail id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order successfully retrieved"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order not found")
    })
    public Order getOrderByDetailId(@PathVariable Integer idOrder, @PathVariable Integer id) {
        return null;
    }


    @PostMapping()
    @ApiOperation(value = "Create a new order")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order successfully created"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
    })
    public Order saveOrder(@RequestBody Order order) {

        System.out.println(order);

        return order;
    }

    @PostMapping("/{idOrder}/detail")
    @ApiOperation(value = "Create a new order detail")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order detail successfully created"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
    })
    public Order saveDetailOrder(@PathVariable Integer idOrder, @RequestBody OrderDetail detail) {

        Order order = new Order();
        order.getDetail().add(detail);
        System.out.println(order);

        return order;
    }

    @PutMapping("/edit/{id}")
    @ApiOperation(value = "Edit order")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order successfully edited"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order not found")
    })
    public Order editOrder(@PathVariable String id, @RequestBody Order order) {

        return order;
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete order by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order successfully deleted"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order not found")
    })
    public Boolean deleteOrderById(@PathVariable String id){

        return true;
    }

    @DeleteMapping("/{idOrder}/detail/{id}")
    @ApiOperation(value = "Delete order detail by id")
    @ApiResponses(value = {
        @ApiResponse(code = 200, message = "Order detail successfully deleted"),
        @ApiResponse(code = 401, message = "Not authorized"),
        @ApiResponse(code = 403, message = "Forbidden"),
        @ApiResponse(code = 404, message = "Order detail not found")
    })
    public Boolean deleteOrderDetailById(@PathVariable Integer idOrder, @PathVariable Integer id){

        return true;
    }
}
