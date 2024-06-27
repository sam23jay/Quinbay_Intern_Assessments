package com.quinbay.inventory.controller;

import com.quinbay.inventory.dto.SellerProductsDTO;
import com.quinbay.inventory.model.Seller;
import com.quinbay.inventory.service.SellerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.util.List;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private SellerService sellerService;

    @GetMapping("/getAll")
    public List<SellerProductsDTO> getAllSellers() {
        return sellerService.getAllSellers();
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<SellerProductsDTO> getSellerById(@PathVariable long id) {
        SellerProductsDTO seller = sellerService.getSellerById(id);
        return new ResponseEntity<>(seller, HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<Seller> addSeller(@RequestBody Seller seller) {
        Seller newSeller = sellerService.addSeller(seller);
        return new ResponseEntity<>(newSeller, HttpStatus.CREATED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteSeller(@PathVariable long id) {
        sellerService.deleteSeller(id);
        return ResponseEntity.noContent().build();
    }
}