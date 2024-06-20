public class UpdateProductThread extends Thread {
    private ProductUpdater productUpdater;
    private int productId;
    private int updatedStock;
    private double updatedPrice;
    private boolean updateStock;

    public UpdateProductThread(ProductUpdater productUpdater, int productId, int updatedStock) {
        this.productUpdater = productUpdater;
        this.productId = productId;
        this.updatedStock = updatedStock;
        this.updateStock = true;
    }

    public UpdateProductThread(ProductUpdater productUpdater, int productId, double updatedPrice) {
        this.productUpdater = productUpdater;
        this.productId = productId;
        this.updatedPrice = updatedPrice;
        this.updateStock = false;
    }

    @Override
    public void run() {
        System.out.println("Update Thread Running");
        if (updateStock) {
            productUpdater.updateAttributes(productId,"stock", updatedStock);
        } else {
            productUpdater.updateAttributes(productId,"price" ,updatedPrice);
        }
        System.out.println("Update Thread Stopped");

    }
}
