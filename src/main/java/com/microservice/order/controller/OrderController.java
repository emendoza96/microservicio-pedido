package com.microservice.order.controller;

import org.springframework.web.bind.annotation.RestController;

import com.microservice.order.client.InventoryClient;
import com.microservice.order.domain.Order;
import com.microservice.order.domain.OrderDetail;
import com.microservice.order.error.ErrorDetails;
import com.microservice.order.error.ErrorResponse;
import com.microservice.order.helpers.StockAvailability;
import com.microservice.order.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private InventoryClient inventoryClient;

    @GetMapping
    @Operation(summary = "Get all orders")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders successfully retrieved"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<?> getAllOrders() {
        try {
            List<Order> orders = orderService.getAllOrders();
            return ResponseEntity.ok().body(orders);
        } catch (Exception e) {
            ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
            errorDetails.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
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
    public ResponseEntity<?> getOrderById(@PathVariable Integer id) {
        try {
            Order order = orderService.getOrderById(id).orElseThrow();
            return ResponseEntity.ok().body(order);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
            errorDetails.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
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
    public ResponseEntity<?> getOrderByConstruction(@PathVariable Integer id) {
        try {
            List<Order> orders = orderService.getOrderByConstructionId(id);
            return ResponseEntity.ok().body(orders);
        } catch (Exception e) {
            ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
            errorDetails.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
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
    public ResponseEntity<?> getOrderByDetailId(@PathVariable Integer idOrder, @PathVariable Integer id) {
        try {
            Order order = orderService.getOrderDetailById(id).orElseThrow().getOrder();

            if(order.getId() != idOrder) new NoSuchElementException();

            return ResponseEntity.ok().body(order);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
            errorDetails.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
        }
    }


    @PostMapping()
    @Operation(summary = "Create a new order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order successfully created"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    public ResponseEntity<?> saveOrder(@RequestBody Order order) {

       try {

            ErrorDetails errorDetails = orderService.getErrors(order);

            if (!errorDetails.getDetails().isEmpty()) {
                errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
                errorDetails.setMessage("Required data is missing");
                return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
            }

            StockAvailability stockAvailability = inventoryClient.checkStockAvailability(order.getDetail());

            if (!stockAvailability.getAvailability()) {
                return ResponseEntity.ok().body(stockAvailability);
            }

            Order newOrder = orderService.createOrder(order);
            return ResponseEntity.status(201).body(newOrder);
       } catch (Exception e) {
            ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
            errorDetails.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
       }
    }

    @PostMapping("/{idOrder}/detail")
    @Operation(summary = "Create a new order detail")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order detail successfully created"),
        @ApiResponse(responseCode = "401", description = "Not authorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
    })
    public ResponseEntity<?> saveDetailOrder(@PathVariable Integer idOrder, @RequestBody OrderDetail detail) {

        try {
            Order order = orderService.getOrderById(idOrder).orElseThrow();
            order.getDetail().add(detail);
            detail.setOrder(order);

            ErrorDetails errorDetails = orderService.getErrors(order);

            if (!errorDetails.getDetails().isEmpty()) {
                errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
                errorDetails.setMessage("Required data is missing");
                return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
            }

            return ResponseEntity.ok().body(orderService.createOrder(order));
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
            errorDetails.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
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
    public ResponseEntity<?> editOrder(@PathVariable Integer id, @RequestBody Order order) {

        try {

            ErrorDetails errorDetails = orderService.getErrors(order);

            if (!errorDetails.getDetails().isEmpty()) {
                errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
                errorDetails.setMessage("Required data is missing");
                return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
            }

            orderService.getOrderById(id).orElseThrow();
            Order orderResult = orderService.createOrder(order);
            return ResponseEntity.ok().body(orderResult);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
            errorDetails.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
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
    public ResponseEntity<?> deleteOrderById(@PathVariable Integer id){

        try {
            orderService.getOrderById(id).orElseThrow();
            orderService.deleteOrderById(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
            errorDetails.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
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
    public ResponseEntity<?> deleteOrderDetailById(@PathVariable Integer idOrder, @PathVariable Integer id){

        try {
            orderService.getOrderById(idOrder).orElseThrow();
            orderService.deleteOrderDetailById(id);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
            errorDetails.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
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
    public ResponseEntity<?> confirmOrder(@PathVariable Integer id){

        try {
            Order order = orderService.getOrderById(id).orElseThrow();
            order = orderService.confirmOrder(order);
            order = orderService.setOrderStatus(order);

            if (order.getState().getState().equals("ACCEPTED")) {
                orderService.sendMessageToOrdersQueue(order);
            }

            return ResponseEntity.ok().body("Order " + order.getState().getState());
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
            errorDetails.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
        }
    }

    @GetMapping("/check/{id}")
    public ResponseEntity<?> checkStock(@PathVariable Integer id, @RequestParam Integer quantity) {

        try {
            Boolean hasStock = inventoryClient.checkStock(id, quantity);

            return ResponseEntity.ok().body(hasStock);
        } catch (Exception e) {
            ErrorDetails errorDetails = new ErrorDetails();
            errorDetails.setCode(HttpStatus.BAD_REQUEST.value());
            errorDetails.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorResponse(errorDetails));
        }

    }
}
