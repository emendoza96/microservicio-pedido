package com.microservice.order.helpers;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockAvailability {

    private Boolean availability;
    private Map<String, String> details = new LinkedHashMap<>();

}
