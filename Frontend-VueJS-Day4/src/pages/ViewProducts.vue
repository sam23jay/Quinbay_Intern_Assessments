<template>
  <div>
    <section class="filters">
      <input type="checkbox" id="availability" v-model="filters.available" />
      <label for="availability">Available</label>
      <input
        type="number"
        v-model.number="filters.minPrice"
        placeholder="Min Price"
      />
      <input
        type="number"
        v-model.number="filters.maxPrice"
        :placeholder="maxPricePlaceholder"
      />
      <button @click="resetFilters">Reset Filters</button>
    </section>
    <section class="product-section" id="product-list">
      <div v-if="products.length === 0">No products available</div>
      <div
        v-else
        v-for="(product, index) in filteredProducts"
        :key="index"
        @click="navigateToProduct(product.id)"
      >
        <img
          src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTCSZZGy55E3_-gPfi_IRbtMwCmWby9Wg6nLg&s"
          alt="Product Image"
          class="product-card-image"
        />
        <p class="product-card-seller">{{ product.seller?.name }}</p>
        <h3 class="product-card-name">{{ product.productName }}</h3>
        <div class="product-card-price-details">
          <span class="product-card-price">₹{{ product.price }}</span>
        </div>
        <p v-if="product.stock === 0" class="product-card-stock-out">
          Out of Stock
        </p>
        <p v-else class="product-card-stock-in">In Stock</p>
        <button @click.stop="addToCart(index)" class="product-card-button">
          Add to Cart
        </button>
      </div>
    </section>
  </div>
</template>

<script>
import API from "@/api/test-api";
import { api } from "@/config";

export default {
  data() {
    return {
      products: [], // Ensure products is initialized as an empty array
      filters: {
        available: false,
        minPrice: 0,
        maxPrice: null,
        searchTerm: localStorage.getItem("searchTerm") || "",
      },
    };
  },
  computed: {
    filteredProducts() {
      return this.products.filter((product) => {
        if (!product || !product.productName) return false; // Skip if product or productName is undefined
        const matchesSearch = product.productName
          .toLowerCase()
          .includes(this.filters.searchTerm.toLowerCase());
        const isAvailability = !this.filters.available || product.stock > 0;
        const isPriceRange =
          product.price >= this.filters.minPrice &&
          (this.filters.maxPrice === null ||
            product.price <= this.filters.maxPrice);
        return matchesSearch && isAvailability && isPriceRange;
      });
    },
    maxPricePlaceholder() {
      return "Max Price";
    },
  },
  methods: {
    fetchProducts() {
      API.getDataByAPI(api.getAllProducts)
        .then((response) => {
          if (response && response.body) {
            this.products = Array.isArray(response.body) ? response.body : [];
          } else {
            console.error("Unexpected response structure:", response);
          }
        })
        .catch((error) => {
          console.error("Error fetching products:", error);
          this.products = []; // Ensure products is an empty array on error
        });
    },
    resetFilters() {
      this.filters = {
        available: false,
        minPrice: 0,
        maxPrice: null,
        searchTerm: localStorage.getItem("searchTerm") || "",
      };
    },
    discountPercentage(product) {
      return (
        ((product.originalPrice - product.discountPrice) /
          product.originalPrice) *
        100
      ).toFixed(2);
    },
    addToCart(index) {
      const product = this.products[index];
      let cart = JSON.parse(localStorage.getItem("cart")) || [];
      const cartItem = cart.find((item) => item.index === index);
      if (product.stock === 0) {
        alert("This product is out of stock.");
        return;
      }
      if (cartItem) {
        if (cartItem.quantity + 1 > product.stock) {
          alert("Cannot add more than available stock.");
        } else {
          cartItem.quantity += 1;
        }
      } else {
        cart.push({ index, quantity: 1 });
      }
      localStorage.setItem("cart", JSON.stringify(cart));
      this.$emit("update-cart");
    },
    navigateToProduct(productId) {
      this.$router.push({ name: "ViewProductById", params: { id: productId } });
    },
  },
  mounted() {
    this.filters.searchTerm = localStorage.getItem("searchTerm") || "";
    this.fetchProducts();
  },
};
</script>

<style scoped>
@import "../assets/scss/ViewProducts.scss";
</style>
