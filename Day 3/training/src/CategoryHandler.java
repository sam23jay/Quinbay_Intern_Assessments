import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CategoryHandler {
    private MongoCollection<Document> categoryCollection;

    public CategoryHandler(MongoDatabase database) {
        this.categoryCollection = database.getCollection("categories");
    }

    public synchronized void addCategory(Category category) {
        category.setCategoryId(generateCategoryId());
        Document doc = new Document("category_id", category.getCategoryId())
                .append("name", category.getName())
                .append("deleted", false);
        categoryCollection.insertOne(doc);
    }

    public synchronized void removeCategory(String categoryId) {
        Document query = new Document("category_id", categoryId);
        Document update = new Document("$set", new Document("deleted", true));
        categoryCollection.updateOne(query, update);
    }

    public synchronized List<Category> getCategories() {
        List<Document> docs = categoryCollection.find(new Document("deleted", false)).into(new ArrayList<>());
        return docs.stream()
                .map(this::documentToCategory)
                .collect(Collectors.toList());
    }

    private synchronized String generateCategoryId() {
        Document doc = categoryCollection.find().sort(new Document("category_id", -1)).first();
        if (doc != null) {
            String lastId = doc.getString("category_id");
            String numericPart = lastId.replaceAll("\\D+", "");
            int idNum = Integer.parseInt(numericPart) + 1;
            return "cat-" + idNum;
        }
        return "cat-1";
    }

    private Category documentToCategory(Document doc) {
        Category category = new Category();
        category.setCategoryId(doc.getString("category_id"));
        category.setName(doc.getString("name"));
        category.setDeleted(doc.getBoolean("deleted"));
        return category;
    }
}
