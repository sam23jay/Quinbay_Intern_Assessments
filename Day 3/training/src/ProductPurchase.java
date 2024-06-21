import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.sql.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class ProductPurchase {
    private MongoCollection<Document> productCollection;
    private List<Purchase> purchaseHistory;
    private final Lock lock = new ReentrantLock();
    private Connection postgresConnection;

    public ProductPurchase(MongoDatabase mongoDatabase, Connection postgresConnection) {
        this.productCollection = mongoDatabase.getCollection("products");
        this.purchaseHistory = new ArrayList<>();
        this.postgresConnection = postgresConnection;
    }

    public void purchaseProduct(String initialProductId) {
        lock.lock();
        Map<String, Integer> productsToPurchase = new HashMap<>();
        Scanner sc = new Scanner(System.in);

        try {
            Document initialDoc = productCollection.find(new Document("product_id", initialProductId).append("deleted", false)).first();
            if (initialDoc == null) {
                System.out.println("Product doesn't exist");
                return;
            }

            int initialStock = initialDoc.getInteger("stock");
            System.out.println("Enter quantity: ");
            int initialQuantity = sc.nextInt();
            sc.nextLine();

            if (initialQuantity <= 0) {
                System.out.println("Invalid quantity");
                return;
            }

            if (initialStock < initialQuantity) {
                System.out.println("Only " + initialStock + " available");
                return;
            }

            productsToPurchase.put(initialProductId, initialQuantity);

            while (true) {
                System.out.println("Do you want to purchase another product? (Y/N)");
                String choice = sc.nextLine().trim().toUpperCase();
                if ("N".equals(choice)) {
                    if (productsToPurchase.isEmpty()) {
                        System.out.println("Nothing in cart. No order placed");
                        return;
                    }
                    System.out.println("Do you want to confirm your purchase? (Y/N)");
                    String confirm = sc.nextLine().trim().toUpperCase();
                    if ("Y".equals(confirm)) {
                        break;
                    } else {
                        System.out.println("Order not placed");
                        return;
                    }
                } else if ("Y".equals(choice)) {
                    System.out.println("Enter product id: ");
                    String productId = sc.nextLine();

                    Document productDoc = productCollection.find(new Document("product_id", productId).append("deleted", false)).first();
                    if (productDoc == null) {
                        System.out.println("Product doesn't exist or has been deleted: " + productId);
                    } else {
                        System.out.println("Enter quantity: ");
                        int quantity = sc.nextInt();
                        sc.nextLine();

                        if (quantity <= 0) {
                            System.out.println("Invalid quantity");
                            continue;
                        }

                        int currentStock = productDoc.getInteger("stock");
                        int totalRequested = productsToPurchase.getOrDefault(productId, 0) + quantity;

                        if (currentStock >= totalRequested) {
                            productsToPurchase.put(productId, totalRequested);
                        } else {
                            System.out.println("Quantity not available. Only " + currentStock + " available for product ID: " + productId);
                        }
                    }
                } else {
                    return;
                }
            }

            double totalPrice = 0.0;
            List<Purchase> currentOrder = new ArrayList<>();

            for (Map.Entry<String, Integer> entry : productsToPurchase.entrySet()) {
                String productId = entry.getKey();
                int quantity = entry.getValue();
                Document productDoc = productCollection.find(new Document("product_id", productId).append("deleted", false)).first();
                if (productDoc != null) {
                    int currentStock = productDoc.getInteger("stock");
                    if (currentStock >= quantity) {
                        double price = productDoc.getDouble("price") * quantity;
                        totalPrice += price;
                        String categoryName = getCategoryName(productDoc);
                        currentOrder.add(new Purchase(productId, quantity, price, categoryName));

                        int updatedStock = currentStock - quantity;
                        productCollection.updateOne(new Document("product_id", productId), new Document("$set", new Document("stock", updatedStock)));
                    } else {
                        System.out.println("Only " + currentStock + " available for product ID: " + productId);
                        return;
                    }
                } else {
                    System.out.println("Product doesn't exist or has been deleted: " + productId);
                    return;
                }
            }

            if (!currentOrder.isEmpty()) {
                int orderId = insertOrder(totalPrice);
                for (Purchase purchase : currentOrder) {
                    insertOrderItems(orderId, purchase.getProductId(), purchase.getQuantity(), getProductName(purchase.getProductId()), getProductPrice(purchase.getProductId()));
                    purchaseHistory.add(purchase);
                }
                System.out.println("Order placed successfully, total price: " + totalPrice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private String getCategoryName(Document productDoc) {
        Document categoryDoc = productDoc.get("category", Document.class);
        if (categoryDoc != null) {
            return categoryDoc.getString("name");
        }
        return "unknown";
    }

    private int insertOrder(double totalAmount) throws SQLException {
        String insertOrderSQL = "INSERT INTO orders (order_date, total_amount, status) VALUES (?, ?, ?) RETURNING order_id";
        try (PreparedStatement pstmt = postgresConnection.prepareStatement(insertOrderSQL)) {
            pstmt.setTimestamp(1, Timestamp.from(Instant.now()));
            pstmt.setDouble(2, totalAmount);
            pstmt.setString(3, "Completed");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            throw new SQLException("Failed to insert order");
        }
    }

    private void insertOrderItems(int orderId, String productId, int quantity, String productName, double pricePerUnit) throws SQLException {
        String insertOrderItemsSQL = "INSERT INTO order_items (order_id, product_id, quantity, price, name) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = postgresConnection.prepareStatement(insertOrderItemsSQL)) {
            pstmt.setInt(1, orderId);
            pstmt.setString(2, productId);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, pricePerUnit);
            pstmt.setString(5, productName);
            pstmt.executeUpdate();
        }
    }

    private String getProductName(String productId) {
        Document productDoc = productCollection.find(new Document("product_id", productId)).first();
        if (productDoc != null) {
            return productDoc.getString("name");
        }
        return "unknown";
    }

    private double getProductPrice(String productId) {
        Document productDoc = productCollection.find(new Document("product_id", productId)).first();
        if (productDoc != null) {
            return productDoc.getDouble("price");
        }
        return 0.0;
    }

    public void getPurchaseHistory() {
        lock.lock();
        try {
            Map<Integer, List<Purchase>> orderMap = new HashMap<>();
            String query = "SELECT o.order_id, o.order_date, o.total_amount, oi.product_id, oi.name, oi.quantity, oi.price FROM orders o JOIN order_items oi ON o.order_id = oi.order_id";
            try (Statement stmt = postgresConnection.createStatement()) {
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    String productId = rs.getString("product_id");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("price");

                    Document productDoc = productCollection.find(new Document("product_id", productId)).first();
                    String categoryName = getCategoryName(productDoc);

                    Purchase purchase = new Purchase(productId, quantity, price, categoryName);
                    orderMap.computeIfAbsent(orderId, k -> new ArrayList<>()).add(purchase);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if(orderMap.isEmpty())
            {
                System.out.println("No Purchases made");
            }
            for (Map.Entry<Integer, List<Purchase>> entry : orderMap.entrySet()) {
                int orderId = entry.getKey();
                List<Purchase> purchases = entry.getValue();
                System.out.println("Order ID: " + orderId);

                Set<String> uniqueProducts = purchases.stream()
                        .map(Purchase::getProductId)
                        .collect(Collectors.toSet());
                System.out.println("\tTotal Products: " + uniqueProducts.size());

                for (Purchase purchase : purchases) {
                    System.out.println("\tProduct ID: " + purchase.getProductId() + ", Name: " + getProductName(purchase.getProductId()) +
                            ", Quantity: " + purchase.getQuantity() + ", Price: " + purchase.getTotalPrice() + ", Category: " + purchase.getCategoryName());
                }
            }
        } finally {
            lock.unlock();
        }
    }
}
