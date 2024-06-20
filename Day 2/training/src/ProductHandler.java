import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;

public class ProductHandler {
    private List<Product> products;
    private static final String FILE_NAME = "products.txt";

    public ProductHandler() {
        products = new ArrayList<>();
        loadProductsFromFile();
    }

    public synchronized void addProduct(Product product) {
        products.add(product);
        saveProductsToFileAsync();
    }

    public synchronized void removeProduct(int id) {
        Product product = getProduct(id);
        if (product != null) {
            product.setDeleted(true);
            saveProductsToFileAsync();
        }
    }

    public synchronized Product getProduct(int id) {
        return products.stream()
                .filter(product -> product.getId() == id && !product.isDeleted())
                .findFirst()
                .orElse(null);
    }

    public synchronized List<Product> getProducts() {
        return products.stream()
                .filter(product -> !product.isDeleted())
                .collect(Collectors.toList());
    }

    public  void saveProductsToFileAsync() {
        CompletableFuture.runAsync(() ->{
            System.out.println("Save thread running");
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
                products.forEach(product -> {
                    try {
                        bw.write(product.toFileString());
                        bw.newLine();
                    } catch (IOException e) {
                        e.getMessage();
                    }
                });
            } catch (IOException e) {
                e.getMessage();
            }
        });
    }


    private synchronized void loadProductsFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                products.add(Product.fromFileString(line));
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public synchronized boolean verifyProductExists(int id) {
        return getProduct(id) != null;
    }

    public synchronized int getMaxProductId() {
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
