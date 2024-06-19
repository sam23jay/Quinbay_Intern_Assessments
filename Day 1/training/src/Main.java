import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ProductHandler productHandler = new ProductHandler();
        ProductUpdater productUpdater = new ProductUpdater(productHandler);
        ProductPurchase productPurchase = new ProductPurchase(productHandler);
        Scanner sc = new Scanner(System.in);

        int counter = productHandler.getMaxProductId() + 1;
        int flag = 0;

        while (flag == 0) {
            System.out.println("1.Add Product\n2.View Product\n3.View All Products\n4.Update Stock\n5.Update Price\n6.Purchase Product\n7.Remove Product\n8.View Purchase History\n9.Exit");
            int option = sc.nextInt();
            switch (option) {
                case 1:
                    sc.nextLine();
                    System.out.println("Enter name: ");
                    String name = sc.nextLine();
                    System.out.println("Enter price: ");
                    float price = sc.nextFloat();
                    if (price < 0) {
                        System.out.println("Invalid");
                        break;
                    }
                    System.out.println("Enter stock: ");
                    int stock = sc.nextInt();
                    if (stock < 0) {
                        System.out.println("Invalid");
                        break;
                    }
                    productHandler.addProduct(new Product(counter, name, price, stock));
                    counter++;
                    break;
                case 2:
                    System.out.println("Enter product id: ");
                    int id = sc.nextInt();
                    if (!productHandler.verifyProductExists(id)) {
                        System.out.println("Product doesn't exist");
                        break;
                    }
                    Product viewProduct = productHandler.getProduct(id);
                    System.out.println(viewProduct.toString());
                    break;
                case 3:
                    List<Product> products = productHandler.getProducts();
                    for (Product product : products) {
                        System.out.println(product.toString());
                    }
                    break;
                case 4:
                    System.out.println("Enter id: ");
                    int updateId = sc.nextInt();
                    if (!productHandler.verifyProductExists(updateId)) {
                        System.out.println("Product doesn't exist");
                        break;
                    }
                    System.out.println("Enter updated stock value: ");
                    int updateStock = sc.nextInt();
                    if (updateStock < 0) {
                        System.out.println("Invalid");
                        break;
                    }
                    productUpdater.updateAttributes(updateId, updateStock);
                    break;
                case 5:
                    System.out.println("Enter id: ");
                    int updateId1 = sc.nextInt();
                    if (!productHandler.verifyProductExists(updateId1)) {
                        System.out.println("Product doesn't exist");
                        break;
                    }
                    System.out.println("Enter updated price value: ");
                    double updatePrice = sc.nextFloat();
                    if (updatePrice < 0) {
                        System.out.println("Invalid");
                        break;
                    }
                    productUpdater.updateAttributes(updateId1, updatePrice);
                    break;
                case 6:
                    System.out.println("Enter id: ");
                    int purchaseId = sc.nextInt();
                    if (!productHandler.verifyProductExists(purchaseId)) {
                        System.out.println("Product doesn't exist");
                        break;
                    }
                    System.out.println("Enter quantity: ");
                    int purchaseQuantity = sc.nextInt();
                    productPurchase.purchaseProduct(purchaseQuantity, purchaseId);
                    break;
                case 7:
                    System.out.println("Enter id");
                    int removeId = sc.nextInt();
                    if (!productHandler.verifyProductExists(removeId)) {
                        System.out.println("Product doesn't exist");
                        break;
                    }
                    productHandler.removeProduct(removeId);
                    break;
                case 8:
                    List<Purchase> list = productPurchase.getPurchaseHistory();
                    for (Purchase purchase : list) {
                        System.out.println(purchase.toString());
                    }
                    break;
                case 9:
                    flag = 1;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid input");
            }
        }
    }
}
