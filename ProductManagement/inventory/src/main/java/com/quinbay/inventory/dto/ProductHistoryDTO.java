package com.quinbay.inventory.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProductHistoryDTO {
    private long id;
    private String oldValue;
    private String newValue;
    private String modifiedColumn;
    private long productId;
    private Date dateModified;
}