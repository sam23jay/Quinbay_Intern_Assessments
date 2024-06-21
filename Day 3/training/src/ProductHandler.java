import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductHandler {
    private MongoCollection<Document> productCollection;

    public ProductHandler(MongoDatabase database) {
        this.productCollection = database.getCollection("products");
    }

    public void addProduct(Product product) {
        product.setProductId(generateProductId());
        Document doc = new Document("product_id", product.getProductId())
                .append("name", product.getName())
                .append("stock", product.getStock())
                .append("price", product.getPrice())
                .append("category", new Document("category_id", product.getCategory().getCategoryId())
                        .append("name", product.getCategory().getName()))
                .append("deleted", false);
        productCollection.insertOne(doc);
    }

    public void removeProduct(String productId) {
        System.out.println("Removing product with ID: " + productId);
        Document query = new Document("product_id", productId);
        Document update = new Document("$set", new Document("deleted", true));
        productCollection.updateOne(query, update);
    }

    public Product getProduct(String productId) {
        Document doc = productCollection.find(new Document("product_id", productId).append("deleted", false)).first();
        if (doc != null) {
            return documentToProduct(doc);
        }
        return null;
    }

    public List<Product> getProducts() {
        List<Document> docs = productCollection.find(new Document("deleted", false)).into(new ArrayList<>());
        return docs.stream().map(this::documentToProduct).collect(Collectors.toList());
    }

    public boolean verifyProductExists(String productId) {
        return getProduct(productId) != null;
    }

    private String generateProductId() {
        Document doc = productCollection.find().sort(new Document("product_id", -1)).first();
        if (doc != null) {
            String lastId = doc.getString("product_id");
            String numericPart = lastId.replaceAll("\\D+", "");
            int idNum = Integer.parseInt(numericPart) + 1;
            return "prod-" + idNum;
        }
        return "prod-1";
    }

    public void updateStock(String productId, int updatedStock) {
        Document query = new Document("product_id", productId);
        Document update = new Document("$set", new Document("stock", updatedStock));
        productCollection.updateOne(query, update);
    }

    public void updatePrice(String productId, double updatedPrice) {
        Document query = new Document("product_id", productId);
        Document update = new Document("$set", new Document("price", updatedPrice));
        productCollection.updateOne(query, update);
    }

    private Product documentToProduct(Document doc) {
        Product product = new Product();
        product.setProductId(doc.getString("product_id"));
        product.setName(doc.getString("name"));
        product.setStock(doc.getInteger("stock"));

        Object priceObject = doc.get("price");
        if (priceObject instanceof Integer) {
            product.setPrice(((Integer) priceObject).doubleValue());
        } else if (priceObject instanceof Double) {
            product.setPrice((Double) priceObject);
        }

        product.setDeleted(doc.getBoolean("deleted"));
        Document categoryDoc = doc.get("category", Document.class);
        if (categoryDoc != null) {
            Category category = new Category();
            category.setCategoryId(categoryDoc.getString("category_id"));
            category.setName(categoryDoc.getString("name"));
            product.setCategory(category);
        }
        return product;
    }
}
