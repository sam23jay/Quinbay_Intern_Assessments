import java.util.Scanner;

public class ProductUpdater {
    private ProductHandler productHandler;

    public ProductUpdater(ProductHandler productHandler) {
        this.productHandler = productHandler;
    }

    public void updateAttributes(int id, int updatedStock) {
        Product product = productHandler.getProduct(id);
        if (product == null) {
            System.out.println("Product doesn't exist: " + id);
        } else {
            System.out.println("Do you want to increment the stock? (Y/N)");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().trim().toUpperCase();
            if ("Y".equals(choice)) {
                product.setStock(product.getStock() + updatedStock);
            } else if("N".equals(choice)){
                product.setStock(updatedStock);
            }
            else{
                System.out.println("Invalid Answer");
            }
            productHandler.saveProductsToFile();
        }
    }

    public void updateAttributes(int id, double updatedPrice) {
        Product product = productHandler.getProduct(id);
        if (product == null) {
            System.out.println("Product doesn't exist: " + id);
        } else {
            System.out.println("Do you want to increment the price? (Y/N)");
            Scanner scanner = new Scanner(System.in);
            String choice = scanner.nextLine().trim().toUpperCase();
            if ("Y".equals(choice)) {
                product.setPrice(product.getPrice() + updatedPrice);
            }  else if("N".equals(choice)){
                product.setPrice(updatedPrice);
            }
            else{
                System.out.println("Invalid Answer");
            }
            productHandler.saveProductsToFile();
        }
    }
}
