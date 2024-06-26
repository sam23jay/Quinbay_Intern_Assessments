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

    public static class NoProductsAvailableException extends RuntimeException {
        public NoProductsAvailableException(String message) {
            super(message);
        }
    }

}
