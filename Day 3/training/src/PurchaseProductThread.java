public class PurchaseProductThread extends Thread {
    private ProductPurchase productPurchase;
    private int quantity;
    private String productId;

    public PurchaseProductThread(ProductPurchase productPurchase, String productId) {
        this.productPurchase = productPurchase;
        this.productId = productId;
    }

    @Override
    public void run() {
        productPurchase.purchaseProduct(productId);
    }
}
