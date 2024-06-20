import java.util.Scanner;

public class ProductUpdater {
    private ProductHandler productHandler;

    public ProductUpdater(ProductHandler productHandler) {
        this.productHandler = productHandler;
    }

    public synchronized void updateAttributes(int id, String attribute, double updatedValue) {
        Product product = productHandler.getProduct(id);
        if (product == null) {
            System.out.println("Product doesn't exist: " + id);
        } else {
            System.out.println("Do you want to increment the " + attribute + "? (Y/N)");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().trim().toUpperCase();

            if (attribute.equalsIgnoreCase("stock")) {
                if ("Y".equals(choice)) {
                    product.setStock(product.getStock() + (int) updatedValue);
                } else if ("N".equals(choice)) {
                    product.setStock((int) updatedValue);
                } else {
                    System.out.println("Invalid Answer");
                }
            } else if (attribute.equalsIgnoreCase("price")) {
                if ("Y".equals(choice)) {
                    product.setPrice(product.getPrice() + updatedValue);
                } else if ("N".equals(choice)) {
                    product.setPrice(updatedValue);
                } else {
                    System.out.println("Invalid Answer");
                }
            } else {
                System.out.println("Invalid attribute: " + attribute);
            }

            productHandler.saveProductsToFileAsync();
        }
    }
}
