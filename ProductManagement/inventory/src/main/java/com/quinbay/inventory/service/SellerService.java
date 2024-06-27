package com.quinbay.inventory.service;

import com.quinbay.inventory.dto.SellerProductsDTO;
import com.quinbay.inventory.exception.CustomExceptions.SellerNotFoundException;
import com.quinbay.inventory.model.Seller;
import com.quinbay.inventory.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    public List<SellerProductsDTO> getAllSellers() {
        return sellerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public SellerProductsDTO getSellerById(long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found with id: " + id));
        return convertToDTO(seller);
    }

    public Seller addSeller(Seller seller) {
        return sellerRepository.save(seller);
    }

    public void deleteSeller(long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found with id: " + id));
        sellerRepository.delete(seller);
    }

    private SellerProductsDTO convertToDTO(Seller seller) {
        return new SellerProductsDTO(seller.getId(), seller.getName(), seller.getLocation(), seller.getProducts());
    }
}