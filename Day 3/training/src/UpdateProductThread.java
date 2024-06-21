public class UpdateProductThread extends Thread {
    private ProductUpdater productUpdater;
    private String productId;
    private double updatedValue;
    private String attribute;

    public UpdateProductThread(ProductUpdater productUpdater, String productId, int updatedStock) {
        this.productUpdater = productUpdater;
        this.productId = productId;
        this.updatedValue = updatedStock;
        this.attribute = "stock";
    }

    public UpdateProductThread(ProductUpdater productUpdater, String productId, double updatedPrice) {
        this.productUpdater = productUpdater;
        this.productId = productId;
        this.updatedValue = updatedPrice;
        this.attribute = "price";
    }

    @Override
    public void run() {
        productUpdater.updateAttributes(productId, attribute, updatedValue);
    }
}
