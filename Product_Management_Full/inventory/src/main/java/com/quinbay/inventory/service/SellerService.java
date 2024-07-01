package com.quinbay.inventory.service;

import com.quinbay.inventory.dto.SellerProductsDTO;
import com.quinbay.inventory.dao.Seller;

import java.util.List;

public interface SellerService {
    List<SellerProductsDTO> getAllSellers();
    SellerProductsDTO getSellerById(long id);
    Seller addSeller(Seller seller);
    void deleteSeller(long id);
}
