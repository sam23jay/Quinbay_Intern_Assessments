package com.quinbay.inventory.service.impl;

import com.quinbay.inventory.dto.SellerProductsDTO;
import com.quinbay.inventory.exception.CustomExceptions.SellerNotFoundException;
import com.quinbay.inventory.dao.Seller;
import com.quinbay.inventory.repository.SellerRepository;
import com.quinbay.inventory.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerServiceImpl implements SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    @Override
    public List<SellerProductsDTO> getAllSellers() {
        return sellerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SellerProductsDTO getSellerById(long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found with id: " + id));
        return convertToDTO(seller);
    }

    @Override
    public Seller addSeller(Seller seller) {
        return sellerRepository.save(seller);
    }

    @Override
    public void deleteSeller(long id) {
        Seller seller = sellerRepository.findById(id)
                .orElseThrow(() -> new SellerNotFoundException("Seller not found with id: " + id));
        sellerRepository.delete(seller);
    }

    private SellerProductsDTO convertToDTO(Seller seller) {
        return new SellerProductsDTO(seller.getId(), seller.getName(), seller.getLocation(), seller.getProducts());
    }
}
