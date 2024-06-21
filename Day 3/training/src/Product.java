public class Product {
    private String productId;
    private String name;
    private double price;
    private int stock;
    private Category category;
    private boolean deleted;


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        return "Product ID: " + productId +
                "\nName: " + name +
                "\nPrice: " + price +
                "\nStock: " + stock +
                "\nCategory: " + (category != null ? category.getName() : "N/A") +
                "\nDeleted: " + deleted;
    }
}
