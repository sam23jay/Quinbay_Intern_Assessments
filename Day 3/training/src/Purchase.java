import java.io.Serializable;

public class Purchase implements Serializable {
    private String productId;
    private int quantity;
    private double totalPrice;
    private String categoryName;

    public Purchase(String productId, int quantity, double totalPrice, String categoryName) {
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.categoryName = categoryName;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public String toString() {
        return "Product ID: " + productId + ", Quantity: " + quantity + ", Total Price: " + totalPrice + ", Category: " + categoryName;
    }
}
