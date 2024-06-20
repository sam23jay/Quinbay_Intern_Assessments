import java.util.List;
import java.util.Scanner;

public class Main {
    private static final ProductHandler productHandler = new ProductHandler();
    private static final ProductPurchase productPurchase = new ProductPurchase(productHandler);
    private static final ProductUpdater productUpdater = new ProductUpdater(productHandler);

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("1. Add Product\n2. View Product\n3. View All Products\n" +
                    "4. Update Stock\n5. Update Price\n6. Purchase Product\n" +
                    "7. Remove Product\n8. View Purchase History\n9. Exit");
            int option = sc.nextInt();
            sc.nextLine();

            switch (option) {
                case 1:
                    addProduct(sc);
                    break;
                case 2:
                    viewProduct(sc);
                    break;
                case 3:
                    viewAllProducts();
                    break;
                case 4:
                    updateStock(sc);
                    break;
                case 5:
                    updatePrice(sc);
                    break;
                case 6:
                    purchaseProduct(sc);
                    break;
                case 7:
                    removeProduct(sc);
                    break;
                case 8:
                    viewPurchaseHistory();
                    break;
                case 9:
                    System.out.println("Exiting...");
                    sc.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid input");
            }
        }
    }

    private static void addProduct(Scanner sc) {
        System.out.println("Enter name:");
        String name = sc.nextLine();
        System.out.println("Enter price:");
        double price = sc.nextDouble();
        System.out.println("Enter stock:");
        int stock = sc.nextInt();

        Product product = new Product(productHandler.getMaxProductId() + 1, name, price, stock);
        Thread addThread = new AddProductThread(productHandler, product);
        addThread.start();

    }

    private static void viewProduct(Scanner sc) {
        System.out.println("Enter product id:");
        int id = sc.nextInt();
        if(!productHandler.verifyProductExists(id))
        {
            System.out.println("Product doesn't exist");
            return;
        }
        Product product = productHandler.getProduct(id);
        if (product != null) {
            System.out.println(product);
        } else {
            System.out.println("Product not found");
        }
    }

    private static void viewAllProducts() {
        List<Product> products = productHandler.getProducts();
        products.forEach(System.out::println);
    }

    private static void updateStock(Scanner sc) {
        System.out.println("Enter product id:");
        int id = sc.nextInt();
        if(!productHandler.verifyProductExists(id))
        {
            System.out.println("Product doesn't exist");
            return;
        }
        System.out.println("Enter updated stock value:");
        int updatedStock = sc.nextInt();

        Thread updateThread = new UpdateProductThread(productUpdater, id, updatedStock);
        updateThread.start();

        try {
            updateThread.join();
        } catch (InterruptedException e) {
            e.getMessage();
        }
    }

    private static void updatePrice(Scanner sc) {
        System.out.println("Enter product id:");
        int id = sc.nextInt();
        if(!productHandler.verifyProductExists(id))
        {
            System.out.println("Product doesnt exist");
            return;
        }
        System.out.println("Enter updated price value:");
        double updatedPrice = sc.nextDouble();

        Thread updateThread = new UpdateProductThread(productUpdater, id, updatedPrice);
        updateThread.start();

        try {
            updateThread.join();
        } catch (InterruptedException e) {
            e.getMessage();
        }
    }

    private static void purchaseProduct(Scanner sc) {
        System.out.println("Enter product id:");
        int id = sc.nextInt();
        if(!productHandler.verifyProductExists(id))
        {
            System.out.println("Product doesnt exist");
            return;
        }
        System.out.println("Enter quantity:");
        int quantity = sc.nextInt();

        Thread purchaseThread = new PurchaseProductThread(productPurchase, id, quantity);
        purchaseThread.start();

        try {
            purchaseThread.join();
        } catch (InterruptedException e) {
            e.getMessage();
        }
    }

    private static void removeProduct(Scanner sc) {
        System.out.println("Enter product id:");
        int id = sc.nextInt();
        if(!productHandler.verifyProductExists(id))
        {
            System.out.println("Product doesnt exist");
            return;
        }
        Thread removeThread = new RemoveProductThread(productHandler, id);
        removeThread.start();

        try {
            removeThread.join();
        } catch (InterruptedException e) {
            e.getMessage();
        }
    }

    private static void viewPurchaseHistory() {
        List<Purchase> purchases = productPurchase.getPurchaseHistory();
        purchases.forEach(System.out::println);
    }
}
