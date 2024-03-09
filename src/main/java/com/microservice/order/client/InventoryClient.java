package com.microservice.order.client;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.microservice.order.DTO.OrderDetailDTO;
import com.microservice.order.domain.OrderDetail;
import com.microservice.order.helpers.StockAvailability;
import com.microservice.order.security.jwt.JwtUtils;

@Component
public class InventoryClient {

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Autowired
    private JwtUtils jwtUtils;

    private final RestTemplate restTemplate = new RestTemplate();

    public Boolean checkStock(Integer id, Integer quantity) {

        HttpEntity<String> entity = new HttpEntity<>(jwtUtils.getHeadersWithBearerToken());
        ResponseEntity<Boolean> response = restTemplate.exchange(
            inventoryServiceUrl + "/api/stock/check/{id}?quantity={quantity}",
            HttpMethod.GET,
            entity,
            Boolean.class,
            id,
            quantity
        );

        return response.getBody();
    }

    public StockAvailability checkStockAvailability(List<OrderDetail> orderDetails){
        List<OrderDetailDTO> orderDetailsDTOs = new ArrayList<>();

        for (OrderDetail orderDetail : orderDetails) {
            orderDetailsDTOs.add(
                new OrderDetailDTO(
                    orderDetail.getOrder().getId(),
                    orderDetail.getId(),
                    orderDetail.getQuantity(),
                    orderDetail.getPrice(),
                    orderDetail.getMaterial().getId()
                )
            );
        }


        HttpEntity<List<OrderDetailDTO>> entity = new HttpEntity<>(orderDetailsDTOs, jwtUtils.getHeadersWithBearerToken());
        ResponseEntity<StockAvailability> response = restTemplate.exchange(
            inventoryServiceUrl + "/api/stock/availability",
            HttpMethod.POST,
            entity,
            StockAvailability.class
        );


        return response.getBody();
    }
}
