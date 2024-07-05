<template>
  <div class="main-content">
    <section class="add-section">
      <AddProduct @update-products="loadProducts" />
    </section>
    <section class="product-section-wrapper">
      <ViewProducts :products="products" @update-cart="loadCart" />
    </section>
  </div>
</template>

<script>
import AddProduct from "@/pages/AddProduct.vue";
import ViewProducts from "@/pages/ViewProducts.vue";

export default {
  components: {
    AddProduct,
    ViewProducts,
  },
  data() {
    return {
      products: [],
    };
  },
  mounted() {
    this.loadProducts();
  },
  methods: {
    loadProducts() {
      this.products = JSON.parse(localStorage.getItem("products")) || [];
    },
    loadCart() {
      this.$emit("update-cart");
    },
  },
};
</script>

<style scoped>
.main-content {
  display: flex;
  padding-top: 80px;
}

@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }

  .product-section-wrapper {
    width: 100%;
  }

  .product-card {
    width: 100%;
  }
}
</style>
