package com.quinbay.inventory.exception;

public class CustomExceptions {

    public static class ProductNotFoundException extends RuntimeException {
        public ProductNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidProductDataException extends RuntimeException {
        public InvalidProductDataException(String message) {
            super(message);
        }
    }

    public static class SellerNotFoundException extends RuntimeException {
        public SellerNotFoundException(String message) {
            super(message);
        }
    }

    public static class NoProductsAvailableException extends RuntimeException {
        public NoProductsAvailableException(String message) {
            super(message);
        }
    }

    public static class CategoryNotFoundException extends RuntimeException {
        public CategoryNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedSellerException extends RuntimeException {
        public UnauthorizedSellerException(String message) {
            super(message);
        }
    }
}