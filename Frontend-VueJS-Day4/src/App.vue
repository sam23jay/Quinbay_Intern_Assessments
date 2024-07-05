<template>
  <div id="app">
    <RouterView />
    <Header @open-cart="openCart" />
    <Footer />
    <CartModal
      :show="showCart"
      :cart="cart"
      :products="products"
      @close="closeCart"
      @update-cart="loadCart"
      @update-products="loadProducts"
      @checkout="checkout"
    />
  </div>
</template>

<script>
import Header from "./components/Header.vue";
import Footer from "./components/Footer.vue";
import CartModal from "./pages/CartModal.vue";
import API from "@/api/test-api.js";
import { api } from "@/config";

export default {
  components: {
    Header,
    Footer,
    CartModal,
  },
  data() {
    return {
      products: [],
      cart: [],
      showCart: false,
    };
  },
  created() {
    this.loadCart();
  },
  methods: {
    loadCart() {
      this.cart = JSON.parse(localStorage.getItem("cart")) || [];
    },
    openCart() {
      this.showCart = true;
      this.loadCart();
    },
    closeCart() {
      this.showCart = false;
    },
    checkout() {
      const cart = JSON.parse(localStorage.getItem("cart")) || [];
      let products = JSON.parse(localStorage.getItem("products")) || [];
      cart.forEach((cartItem) => {
        const product = products[cartItem.index];
        if (product) {
          product.stock -= cartItem.quantity;
        }
      });
      localStorage.setItem("products", JSON.stringify(products));
      localStorage.setItem("cart", JSON.stringify([]));
      this.loadCart();
    },
  },
};
</script>

<style lang="scss">
body {
  font-family: Arial, Helvetica, sans-serif;
}
</style>
