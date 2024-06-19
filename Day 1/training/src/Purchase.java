import java.io.Serializable;

public class Purchase implements Serializable {
    private int id;
    private int quantity;
    private double totalPrice;

    public Purchase(int id, int quantity, double totalPrice) {
        this.id = id;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    @Override
    public String toString() {
        return "id: " + id + "\nQuantity: " + quantity + "\nTotal Price: " + totalPrice;
    }

    public String toFileString() {
        return "ID: "+id + "," +"Quantity: "+ quantity + ", Price: " + totalPrice;
    }

    public static Purchase fromFileString(String line) {
        String[] parts = line.split(",");
        int id = Integer.parseInt(parts[0].substring(4).trim());
        int quantity = Integer.parseInt(parts[1].substring(parts[1].indexOf(":") + 1).trim());
        double totalPrice = Double.parseDouble(parts[2].substring(parts[2].indexOf(":") + 1).trim());
        return new Purchase(id, quantity, totalPrice);
    }

}
