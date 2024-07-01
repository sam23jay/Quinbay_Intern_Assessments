package com.quinbay.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequestDTO {
    private long cartId;
    private String customerName;
    private String customerEmail;
    private List<ProductQuantityDTO> products;
}