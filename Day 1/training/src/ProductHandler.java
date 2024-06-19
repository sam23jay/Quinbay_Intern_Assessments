import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductHandler implements Serializable  {
    private List<Product> products;
    private static final String FILE_NAME = "products.txt";

    public ProductHandler() {
        products = new ArrayList<>();
        loadProductsFromFile();
    }

    public void addProduct(Product product) {
        products.add(product);
        saveProductsToFile();
        System.out.println("Product added successfully");
    }

    public void removeProduct(int id) {
        Product product = getProduct(id);
        if (product == null) {
            System.out.println("Product doesn't exist");
            return;
        }
        product.setDeleted(true);
        saveProductsToFile();
    }

    public Product getProduct(int id) {
        for (Product product : products) {
            if (product.getId() == id && !product.isDeleted()) {
                return product;
            }
        }
        return null;
    }

    public List<Product> getProducts() {
        List<Product> activeProducts = new ArrayList<>();
        for (Product product : products) {
            if (!product.isDeleted()) {
                activeProducts.add(product);
            }
        }
        if(activeProducts.isEmpty())
        {
            System.out.println("No Products available");
        }
        return activeProducts;
    }

    public void loadProductsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                products.add(Product.fromFileString(line));
            }
        } catch (FileNotFoundException error) {
            System.out.println("Products file not found");
        } catch (IOException error) {
            error.getMessage();
        }
    }

    public void saveProductsToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Product product : products) {
                bw.write(product.toFileString());
                bw.newLine();
            }
        } catch (IOException error) {
            error.getMessage();
        }
    }

    public boolean verifyProductExists(int id) {
        return getProduct(id) != null;
    }

    public int getMaxProductId() {
        int maxId = 0;
        for (Product product : products) {
            if (product.getId() > maxId) {
                maxId = product.getId();
            }
        }
        if(maxId==0)
        {
            return -1;
        }
        return maxId;
    }
}
