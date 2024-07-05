<template>
  <div class="add-section">
    <h1>Add Product</h1>
    <form @submit.prevent="addProduct">
      <label for="name">Name: </label>
      <input
        type="text"
        id="name"
        v-model="product.productName"
        placeholder="Enter Name..."
        :disabled="!isAdmin"
      />
      <label for="brand">Brand: </label>
      <input
        type="text"
        id="brand"
        v-model="product.brand"
        placeholder="Enter Brand..."
        :disabled="!isAdmin"
      />
      <label for="price">Price: </label>
      <input
        type="text"
        id="price"
        v-model="product.price"
        placeholder="Enter Price..."
        :disabled="!isAdmin"
      />
      <label for="stock">Stock: </label>
      <input
        type="text"
        id="stock"
        v-model="product.stock"
        placeholder="Enter Stock..."
        :disabled="!isAdmin"
      />
      <label for="Category">Category: </label>
      <select id="Category" v-model="product.category.id" :disabled="!isAdmin">
        <option value="1">Electronics</option>
        <option value="2">Footwear</option>
        <option value="3">Clothing</option>
      </select>
      <button type="submit" :disabled="!isAdmin">Add</button>
      <p v-if="!isAdmin" class="warning-text">Only admins can add products.</p>
    </form>
  </div>
</template>

<script>
import API from "@/api/test-api";
import { api } from "@/config";

export default {
  data() {
    return {
      product: {
        productName: "",
        brand: "",
        price: "",
        stock: "",
        category: {
          id: 1, // Default category
        },
        seller: {
          id: 1,
          name: "sam",
          location: "cbe",
        },
      },
      userRole: "",
    };
  },
  computed: {
    isAdmin() {
      return this.userRole === "admin";
    },
  },
  methods: {
    addProduct() {
      if (this.validateProduct()) {
        const newProduct = {
          productName: this.product.productName,
          price: parseFloat(this.product.price),
          stock: parseInt(this.product.stock),
          category: { id: parseInt(this.product.category.id) },
          seller: {
            id: 1,
            name: "sam",
            location: "cbe",
          },
        };

        API.postDataViaAPI(api.postProduct, newProduct)
          .then((response) => {
            console.log(response);
            this.$emit("update-products");
            this.resetForm();
          })
          .catch((error) => {
            console.log("Error adding product:", error);
          });
      }
    },
    validateProduct() {
      const { productName, brand, price, stock, category } = this.product;

      if (!productName || !brand || !price || !stock || !category.id) {
        alert("All fields must be filled out");
        return false;
      }

      if (isNaN(price) || isNaN(stock)) {
        alert("Price and Stock must be numbers");
        return false;
      }

      return true;
    },
    resetForm() {
      this.product = {
        productName: "",
        brand: "",
        price: "",
        stock: "",
        category: {
          id: 1, // Reset to default category
        },
        seller: {
          id: 1,
          name: "sam",
          location: "cbe",
        },
      };
    },
    getUserRole() {
      const user = JSON.parse(localStorage.getItem("loggedInUser"));
      if (user && user.role) {
        this.userRole = user.role;
      }
    },
  },
  mounted() {
    this.getUserRole();
  },
};
</script>

<style scoped>
@import "../assets/scss/AddProduct.scss";

.warning-text {
  color: red;
  font-weight: bold;
  margin-top: 10px;
}
</style>
