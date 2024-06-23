package org.example.operations;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Scanner;


public class ProductUpdater {
    private MongoCollection<Document> productCollection;
    private ProductHandler productHandler;

    public ProductUpdater(MongoDatabase database, ProductHandler productHandler) {
        this.productCollection = database.getCollection("products");
        this.productHandler = productHandler;
    }

    public synchronized void updateAttributes(String productId, String attribute, double updatedValue) {
        Document query = new Document("product_id", productId);
        Document productDoc = productCollection.find(query).first();

        if (productDoc == null) {
            System.out.println("Product doesn't exist: " + productId);
        } else {
            System.out.println("Do you want to increment the " + attribute + "? (Y/N)");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().trim().toUpperCase();
            if (attribute.equalsIgnoreCase("stock")) {
                int stock = productDoc.getInteger("stock");
                if ("Y".equals(choice)) {
                    stock += (int) updatedValue;
                } else if ("N".equals(choice)) {
                    stock = (int) updatedValue;
                } else {
                    System.out.println("Invalid Answer");
                    return;
                }

                productCollection.updateOne(query, new Document("$set", new Document("stock", stock)));
                productHandler.updateStock(productId, stock);
            } else if (attribute.equalsIgnoreCase("price")) {
                double price = productDoc.getDouble("price");
                if ("Y".equals(choice)) {
                    price += updatedValue;
                } else if ("N".equals(choice)) {
                    price = updatedValue;
                } else {
                    System.out.println("Invalid Answer");
                    return;
                }
                productCollection.updateOne(query, new Document("$set", new Document("price", price)));
                productHandler.updatePrice(productId, price);
            } else {
                System.out.println("Invalid attribute: " + attribute);
            }
        }
    }
}
