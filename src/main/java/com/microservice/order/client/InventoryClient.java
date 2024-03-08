package com.microservice.order.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.microservice.order.security.jwt.JwtUtils;

@Component
public class InventoryClient {

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    @Value("${spring.security.user.name}")
    private String username;

    @Autowired
    private JwtUtils jwtUtils;

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders createHeadersWithBearerToken() {
        String token = "Bearer " + jwtUtils.generateAccessToken(username);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    public Boolean checkStock(Integer id, Integer quantity) {

        HttpEntity<String> entity = new HttpEntity<>(createHeadersWithBearerToken());
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

}
