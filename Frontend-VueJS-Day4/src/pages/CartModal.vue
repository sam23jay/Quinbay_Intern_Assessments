<template>
  <div id="cart-modal" class="modal" v-if="show">
    <div class="modal-content">
      <span class="close" @click="$emit('close')">&times;</span>
      <h2>Cart Items</h2>
      <div id="cart-items">
        <div v-for="(item, index) in cart" :key="index" class="cart-item">
          <div v-if="products[item.index]" class="cart-item-content">
            <img
              :src="products[item.index].imageUrl"
              alt="Product Image"
              class="cart-item-image"
            />
            <div class="cart-item-details">
              <p>
                {{ products[item.index].name }} - ₹{{
                  products[item.index].discountPrice
                }}
              </p>
            </div>
            <div class="quantity-controls">
              <button @click="updateQuantity(index, -1)">-</button>
              <span>{{ item.quantity }}</span>
              <button @click="updateQuantity(index, 1)">+</button>
            </div>
          </div>
        </div>
      </div>
      <div class="total-price">Total Price: ₹{{ totalPrice.toFixed(2) }}</div>
      <button id="checkout-button" @click="handleCheckout">Checkout</button>
    </div>
  </div>
</template>

<script>
export default {
  props: ["show", "cart", "products"],

  computed: {
    totalPrice() {
      return this.cart.reduce((total, item) => {
        if (this.products[item.index]) {
          return (
            total + this.products[item.index].discountPrice * item.quantity
          );
        }
        return total;
      }, 0);
    },
  },
  methods: {
    updateQuantity(index, change) {
      const cart = JSON.parse(localStorage.getItem("cart")) || [];
      const cartItem = cart[index];
      if (this.products[cartItem.index]) {
        const product = this.products[cartItem.index];
        if (cartItem.quantity + change > 0) {
          if (cartItem.quantity + change > product.stock) {
            alert("Cannot add more than available stock.");
          } else {
            cartItem.quantity += change;
          }
        } else {
          cart.splice(index, 1);
        }
        localStorage.setItem("cart", JSON.stringify(cart));
        this.$emit("update-cart");
      }
    },
    handleCheckout() {
      const cart = JSON.parse(localStorage.getItem("cart")) || [];
      let products = JSON.parse(localStorage.getItem("products")) || [];
      cart.forEach((cartItem) => {
        if (products[cartItem.index]) {
          const product = products[cartItem.index];
          if (product) {
            product.stock -= cartItem.quantity;
          }
        }
      });
      localStorage.setItem("products", JSON.stringify(products));
      localStorage.setItem("cart", JSON.stringify([]));
      this.$emit("update-products");
      this.$emit("update-cart");
      this.$emit("close");
    },
  },
};
</script>

<style scoped>
@import "../assets/scss/CartModal.scss";

.cart-item-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.cart-item-image {
  width: 60px;
  height: 60px;
  margin-right: 10px;
}

.cart-item-details {
  flex: 1;
}

.quantity-controls {
  display: flex;
  align-items: center;
}

.quantity-controls button {
  background-color: #ddd;
  border: none;
  padding: 5px 10px;
  cursor: pointer;
  margin: 0 5px;
}

.quantity-controls span {
  width: 30px;
  text-align: center;
}

.total-price {
  text-align: right;
  padding: 10px;
  font-size: 1.2em;
  font-weight: bold;
  border-top: 1px solid #ccc;
}

#checkout-button {
  padding: 10px 20px;
  color: white;
  background-color: #1c4bf6;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  transition: background-color 0.3s ease;
  margin-top: 20px;
  width: 100%;
}

#checkout-button:hover {
  background-color: #456dff;
}
</style>
