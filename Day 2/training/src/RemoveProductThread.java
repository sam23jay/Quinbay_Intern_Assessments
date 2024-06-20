public class RemoveProductThread extends Thread {
    private ProductHandler productHandler;
    private int productId;

    public RemoveProductThread(ProductHandler productHandler, int productId) {
        this.productHandler = productHandler;
        this.productId = productId;
    }

    @Override
    public void run() {
        System.out.println("Remove Thread Running");
        productHandler.removeProduct(productId);
        System.out.println("Remove Thread Stopped");

    }
}
