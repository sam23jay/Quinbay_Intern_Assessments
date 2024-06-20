public class PurchaseProductThread extends Thread {
    private ProductPurchase productPurchase;
    private int productId;
    private int quantity;

    public PurchaseProductThread(ProductPurchase productPurchase, int productId, int quantity) {
        this.productPurchase = productPurchase;
        this.productId = productId;
        this.quantity = quantity;
    }

    @Override
    public void run() {
        System.out.println("Purchase Thread Running");
        productPurchase.purchaseProduct(quantity, productId);
    }
}
