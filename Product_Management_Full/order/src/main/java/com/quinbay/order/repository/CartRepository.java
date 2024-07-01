package com.quinbay.order.repository;

import com.quinbay.order.dao.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, Long> {
    Cart findByCartId(long cartId);
}