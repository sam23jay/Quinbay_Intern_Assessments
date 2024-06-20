import java.io.Serializable;

public class Purchase implements Serializable {
    private int productId;
    private int quantity;
    private double totalPrice;

    public Purchase(int productId, int quantity, double totalPrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return "Product ID: " + productId + ", Quantity: " + quantity + ", Total Price: " + totalPrice;
    }

    public String toFileString() {
        return "Product ID: " + productId + ", Quantity: " + quantity + ", Total Price: " + totalPrice;
    }

    public static Purchase fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        int productId = Integer.parseInt(parts[0].split(":")[1].trim());
        int quantity = Integer.parseInt(parts[1].split(":")[1].trim());
        double totalPrice = Double.parseDouble(parts[2].split(":")[1].trim());

        return new Purchase(productId, quantity, totalPrice);
    }
}
