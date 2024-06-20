import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ProductPurchase {
    private ProductHandler productHandler;
    private List<Purchase> purchaseHistory;
    private static final String PURCHASE_FILE_NAME = "purchase_history.txt";
    private final Lock lock = new ReentrantLock();

    public ProductPurchase(ProductHandler productHandler) {
        this.productHandler = productHandler;
        this.purchaseHistory = new ArrayList<>();
        loadPurchaseHistoryFromFile();
    }

    public void purchaseProduct(int quantity, int id) {
        lock.lock();
        try {
            if (quantity < 0 || id < 0) {
                System.out.println("Invalid values");
                return;
            }
            Product product = productHandler.getProduct(id);
            if (product != null) {
                if (product.getStock() >= quantity) {
                    product.setStock(product.getStock() - quantity);
                    double price = product.getPrice() * quantity;
                    System.out.println("Purchased Successfully, total price is " + price);
                    purchaseHistory.add(new Purchase(id, quantity, price));
                    savePurchaseHistoryToFileAsync();
                } else {
                    if(product.getStock()==0)
                    {
                        System.out.println("Out of Stock");
                    }
                    System.out.println("Only " + product.getStock() + " available");
                }
            } else {
                System.out.println("Product doesn't exist");
            }
        } finally {
            lock.unlock();
        }
    }

    public List<Purchase> getPurchaseHistory() {
        lock.lock();
        try {
            if (purchaseHistory.isEmpty()) {
                System.out.println("No purchases made");
            }
            return purchaseHistory;
        } finally {
            lock.unlock();
        }
    }

    private void loadPurchaseHistoryFromFile() {
        File file = new File(PURCHASE_FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            return;
        }
        lock.lock();
        try (BufferedReader br = new BufferedReader(new FileReader(PURCHASE_FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                purchaseHistory.add(Purchase.fromFileString(line));
            }
        } catch (FileNotFoundException error) {
            System.out.println("Purchase history file not found");
        } catch (IOException error) {
            error.getMessage();
        } finally {
            lock.unlock();
        }
    }

    public synchronized void savePurchaseHistoryToFileAsync() {
        CompletableFuture.runAsync(() -> {
            System.out.println("Save to purchase thread running");
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(PURCHASE_FILE_NAME))) {
                purchaseHistory.forEach(purchase -> {
                    try {
                        bw.write(purchase.toFileString());
                        bw.newLine();
                    } catch (IOException error) {
                        error.getMessage();
                    }
                });
            } catch (IOException error) {
                error.getMessage();
            }
        });
    }
}
