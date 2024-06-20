public class AddProductThread extends Thread {
    private ProductHandler productHandler;
    private Product product;

    public AddProductThread(ProductHandler productHandler, Product product) {
        this.productHandler = productHandler;
        this.product = product;
    }

    @Override
    public void run() {
        System.out.println("Add Thread Running");
        productHandler.addProduct(product);
        System.out.println("Add Thread Stopped");

    }
}
