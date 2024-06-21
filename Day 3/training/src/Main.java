import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) {
        try (Connection postgresConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ecommerce", "samueljayaseelanv", "1234")) {
            MongoDatabase mongoDatabase = MongoClients.create("mongodb://localhost:27017").getDatabase("ecommerce");

            ProductHandler productHandler = new ProductHandler(mongoDatabase);
            ProductUpdater productUpdater = new ProductUpdater(mongoDatabase, productHandler);
            ProductPurchase productPurchase = new ProductPurchase(mongoDatabase, postgresConnection);
            CategoryHandler categoryHandler = new CategoryHandler(mongoDatabase);

            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("1. Add Product\n2. View Product\n3. View All Products\n4. Update Stock\n5. Update Price\n6. Purchase Product\n7. Remove Product\n8. View Purchase History\n9. Add Category\n10. Remove Category\n11. View Categories\n12. Exit");
                int option = sc.nextInt();
                sc.nextLine();

                switch (option) {
                    case 1:
                        List<Category> categories = categoryHandler.getCategories();
                        if(categories.isEmpty())
                        {
                            System.out.println("Add Categories first");
                            break;
                        }
                        System.out.println("Enter name: ");
                        String name = sc.nextLine();
                        System.out.println("Enter price: ");
                        double price = sc.nextDouble();
                        System.out.println("Enter stock: ");
                        int stock = sc.nextInt();
                        sc.nextLine();
                        System.out.println("Available categories:");

                        categories.forEach(category -> System.out.println(category.getName()));
                        System.out.println("Enter category name: ");
                        String categoryName = sc.nextLine();

                        Category category = categories.stream()
                                .filter(cat -> cat.getName().equalsIgnoreCase(categoryName))
                                .findFirst()
                                .orElse(null);

                        if (category == null) {
                            System.out.println("Category doesn't exist");
                            break;
                        }

                        Product product = new Product();
                        product.setName(name);
                        product.setPrice(price);
                        product.setStock(stock);
                        product.setCategory(category);

                        CompletableFuture.runAsync(() -> {
                            System.out.println("Add Thread Running");
                            productHandler.addProduct(product);
                            System.out.println("Add Thread Stopped");
                        });
                        break;

                    case 2:
                        System.out.println("Enter product id: ");
                        String productId = sc.nextLine();
                        if (!productHandler.verifyProductExists(productId)) {
                            System.out.println("Product doesn't exist");
                            break;
                        }
                        Product viewProduct = productHandler.getProduct(productId);
                        System.out.println(viewProduct);
                        break;

                    case 3:
                        List<Product> products = productHandler.getProducts();
                        products.forEach(viewProducts -> System.out.println(viewProducts));
                        break;

                    case 4:
                        System.out.println("Enter product id: ");
                        productId = sc.nextLine();
                        if (!productHandler.verifyProductExists(productId)) {
                            System.out.println("Product doesn't exist");
                            break;
                        }
                        System.out.println("Enter updated stock value: ");
                        int updatedStock = sc.nextInt();
                        sc.nextLine();
                        if(updatedStock<=0)
                        {
                            System.out.println("Invalid value");
                            break;
                        }
                        UpdateProductThread updateStockThread = new UpdateProductThread(productUpdater, productId, updatedStock);
                        updateStockThread.start();
                        try {
                            updateStockThread.join();
                        } catch (InterruptedException e) {
                            e.getMessage();
                        }
                        break;

                    case 5:
                        System.out.println("Enter product id: ");
                        productId = sc.nextLine();
                        if (!productHandler.verifyProductExists(productId)) {
                            System.out.println("Product doesn't exist");
                            break;
                        }
                        System.out.println("Enter updated price value: ");
                        double updatedPrice = sc.nextDouble();
                        if(updatedPrice<=0)
                        {
                            System.out.println("Invalid value");
                            break;
                        }
                        sc.nextLine();
                        UpdateProductThread updatePriceThread = new UpdateProductThread(productUpdater, productId, updatedPrice);
                        updatePriceThread.start();
                        try {
                            updatePriceThread.join();
                        } catch (InterruptedException e) {
                            e.getMessage();
                        }
                        break;

                    case 6:
                        System.out.println("Enter product id: ");
                        productId = sc.nextLine();

                        PurchaseProductThread purchaseProductThread = new PurchaseProductThread(productPurchase, productId);
                        purchaseProductThread.start();
                        try {
                            purchaseProductThread.join();
                        } catch (InterruptedException e) {
                            e.getMessage();
                        }
                        break;

                    case 7:
                        System.out.println("Enter product id: ");
                        productId = sc.nextLine();
                        if (!productHandler.verifyProductExists(productId)) {
                            System.out.println("Product doesn't exist");
                            break;
                        }
                        RemoveProductThread removeProductThread = new RemoveProductThread(productHandler, productId);
                        removeProductThread.start();
                        try {
                            removeProductThread.join();
                        } catch (InterruptedException e) {
                            e.getMessage();
                        }
                        break;

                    case 8:
                        CompletableFuture.runAsync(() -> {
                            System.out.println("View Purchase History Running");
                            productPurchase.getPurchaseHistory();
                            System.out.println("View Purchase History Completed");
                        });
                        break;

                    case 9:
                        System.out.println("Enter category name: ");
                        String newCategoryName = sc.nextLine();
                        Category newCategory = new Category();
                        newCategory.setName(newCategoryName);
                        CompletableFuture.runAsync(() -> {
                            categoryHandler.addCategory(newCategory);
                            System.out.println("Category Added");
                        });
                        break;

                    case 10:
                        System.out.println("Enter category id: ");
                        String removeCategoryId = sc.nextLine();
                        Thread categoryThread = new Thread(() -> {
                            categoryHandler.removeCategory(removeCategoryId);
                        });
                        categoryThread.start();
                        try {
                            categoryThread.join();
                        } catch (InterruptedException e) {
                            e.getMessage();
                        }
                        break;

                    case 11:
                        List<Category> allCategories = categoryHandler.getCategories();
                        if(allCategories.isEmpty())
                        {
                            System.out.println("No categories available");
                            break;
                        }
                        System.out.println("Available categories:");
                        allCategories.forEach(category1 -> System.out.println("ID: " + category1.getCategoryId() + ", Name: " + category1.getName()));
                        break;

                    case 12:
                        System.out.println("Exiting...");
                        return;

                    default:
                        System.out.println("Invalid input");
                        break;
                }
            }
        } catch (SQLException e) {
            e.getMessage();
        }
    }
}
