import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductPurchase {
    private ProductHandler productHandler;
    private List<Purchase> purchaseHistory;
    private static final String PURCHASE_FILE_NAME = "purchase_history.txt";

    public ProductPurchase(ProductHandler productHandler) {
        this.productHandler = productHandler;
        this.purchaseHistory = new ArrayList<>();
        loadPurchaseHistoryFromFile();
    }

    public void purchaseProduct(int quantity, int id) {
        if (quantity < 0 || id < 0) {
            System.out.println("Invalid values");
            return;
        }
        if (productHandler.getProduct(id) != null) {
            Product product = productHandler.getProduct(id);
            if (product.getStock() >= quantity) {
                product.setStock(product.getStock() - quantity);
                double price = product.getPrice() * quantity;
                System.out.println("Purchased Successfully, total price is " + price);
                purchaseHistory.add(new Purchase(id, quantity, price));
                savePurchaseHistoryToFile();
            } else {
                System.out.println("Only " + product.getStock() + " available");
            }
        } else {
            System.out.println("Product doesn't exist");
        }
    }

    public List<Purchase> getPurchaseHistory() {
        if (purchaseHistory.isEmpty()) {
            System.out.println("No purchases made");
        }
        return purchaseHistory;
    }

    private void loadPurchaseHistoryFromFile() {
        File file = new File(PURCHASE_FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(PURCHASE_FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                purchaseHistory.add(Purchase.fromFileString(line));
            }
        } catch (FileNotFoundException error) {
            System.out.println("Purchase history file not found");
        } catch (IOException error) {
            error.getMessage();
        }
    }

    private void savePurchaseHistoryToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PURCHASE_FILE_NAME))) {
            for (Purchase purchase : purchaseHistory) {
                bw.write(purchase.toFileString());
                bw.newLine();
            }
        } catch (IOException error) {
            error.getMessage();
        }
    }
}
