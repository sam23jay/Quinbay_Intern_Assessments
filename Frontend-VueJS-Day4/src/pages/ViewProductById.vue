<template>
  <div class="product-details-wrapper">
    <div v-if="!product" class="loading">Loading product details...</div>
    <div v-else class="product-details">
      <img
        src="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTCSZZGy55E3_-gPfi_IRbtMwCmWby9Wg6nLg&s"
        alt="Product Image"
        class="product-image"
      />
      <div class="product-info">
        <h2 class="product-name">{{ product.productName }}</h2>
        <p class="product-price">₹{{ product.price }}</p>
        <p
          class="product-stock"
          :class="product.stock > 0 ? 'in-stock' : 'out-of-stock'"
        >
          {{ product.stock > 0 ? "In Stock" : "Out of Stock" }}
        </p>
        <p class="product-seller">Seller: {{ product.seller.name }}</p>
        <p class="product-category">Category: {{ product.category.name }}</p>
        <button @click="addToCart" class="add-to-cart-button">
          Add to Cart
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import API from "@/api/test-api";

export default {
  data() {
    return {
      product: null,
    };
  },
  methods: {
    fetchProductById() {
      const productId = this.$route.params.id;
      API.getDataById(productId)
        .then((response) => {
          console.log("Fetched product by ID response:", response);
          if (response && response.body) {
            this.product = response.body;
          } else {
            console.error("Unexpected response structure:", response);
          }
        })
        .catch((error) => {
          console.error("Error fetching product details:", error);
        });
    },
    addToCart() {
      let cart = JSON.parse(localStorage.getItem("cart")) || [];
      const cartItem = cart.find((item) => item.id === this.product.id);
      if (this.product.stock === 0) {
        alert("This product is out of stock.");
        return;
      }
      if (cartItem) {
        if (cartItem.quantity + 1 > this.product.stock) {
          alert("Cannot add more than available stock.");
        } else {
          cartItem.quantity += 1;
        }
      } else {
        cart.push({ id: this.product.id, quantity: 1 });
      }
      localStorage.setItem("cart", JSON.stringify(cart));
      this.$emit("update-cart");
    },
  },
  mounted() {
    this.fetchProductById();
  },
};
</script>

<style scoped>
.product-details-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
}

.loading {
  font-size: 1.5em;
  color: #333;
}

.product-details {
  display: flex;
  background-color: #fff;
  padding: 2%;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border-radius: 10px;
}

.product-image {
  width: 300px;
  height: auto;
  border-radius: 10px;
  margin-right: 20px;
}

.product-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 2em;
  color: #333;
  margin-bottom: 10px;
}

.product-price {
  font-size: 1.5em;
  color: #28a745;
  margin-bottom: 10px;
}

.product-stock {
  font-size: 1.2em;
  margin-bottom: 10px;
}

.in-stock {
  color: green;
}

.out-of-stock {
  color: red;
}

.product-seller,
.product-category {
  font-size: 1.1em;
  color: #666;
  margin-bottom: 10px;
}

.add-to-cart-button {
  padding: 10px 20px;
  background-color: rgb(28, 75, 246);
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.add-to-cart-button:hover {
  background-color: rgb(69, 109, 255);
}
</style>
