public class RemoveProductThread extends Thread {
    private ProductHandler productHandler;
    private String productId;

    public RemoveProductThread(ProductHandler productHandler, String productId) {
        this.productHandler = productHandler;
        this.productId = productId;
    }

    @Override
    public void run() {
        productHandler.removeProduct(productId);
    }
}
