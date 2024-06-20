import java.io.Serializable;

public class Product implements Serializable {
    private int id;
    private String name;
    private double price;
    private int stock;
    private boolean deleted;

    public Product(int id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.deleted = false;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "ID: " + id + "\nName: " + name + "\nPrice: " + price + "\nStock: " + stock + "\nDeleted: " + deleted;
    }

    public String toFileString() {
        return "ID: " + id + ", Name: " + name + ", Price: " + price + ", Stock: " + stock + ", Deleted: " + deleted;
    }

    public static Product fromFileString(String fileString) {
        String[] parts = fileString.split(",");
        int id = Integer.parseInt(parts[0].substring(4).trim());
        String name = parts[1].substring(7).trim();
        double price = Double.parseDouble(parts[2].substring(7).trim());
        int stock = Integer.parseInt(parts[3].substring(7).trim());
        boolean deleted = Boolean.parseBoolean(parts[4].substring(10).trim());
        Product product = new Product(id, name, price, stock);
        product.setDeleted(deleted);
        return product;
    }
}
