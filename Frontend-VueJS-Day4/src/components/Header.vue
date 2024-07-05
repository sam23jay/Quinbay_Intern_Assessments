<template>
  <header id="header">
    <div class="company-name">Inventory Management</div>
    <RouterLink :to="`/index/${2}`">Login</RouterLink>

    <div class="search-bar">
      <input
        type="text"
        placeholder="Search products..."
        v-model="searchTerm"
        @input="debouncedHandleSearch"
      />
      <span class="clear-search" v-if="searchTerm" @click="clearSearch">✕</span>
    </div>
    <div class="auth-buttons">
      <div class="cart-icon" @click="openCart">🛒</div>
      <template v-if="isLoggedIn">
        <button class="logout" @click="logout">Logout</button>
      </template>
      <template v-else>
        <button class="login" @click="goToLogin">Login</button>
        <button class="signup" @click="goToSignup">Sign Up</button>
      </template>
    </div>
    <div class="hamburger-menu">
      <div class="hamburger-icon">☰</div>
    </div>
  </header>
</template>

<script>
import { RouterLink } from "vue-router";

export default {
  data() {
    return {
      searchTerm: localStorage.getItem("searchTerm") || "",
      isLoggedIn: !!JSON.parse(localStorage.getItem("loggedInUser")),
    };
  },
  methods: {
    handleSearch() {
      localStorage.setItem("searchTerm", this.searchTerm);
    },
    clearSearch() {
      this.searchTerm = "";
      localStorage.removeItem("searchTerm");
    },
    openCart() {
      this.$emit("open-cart");
    },
    logout() {
      localStorage.removeItem("loggedInUser");
      this.isLoggedIn = false;
      this.$router.push("/login");
    },
    goToLogin() {
      this.$router.push("/login");
    },
    goToSignup() {
      this.$router.push("/signup");
    },
    checkLoginStatus() {
      this.isLoggedIn = !!JSON.parse(localStorage.getItem("loggedInUser"));
    },
    debounce(fn, delay) {
      let timeout;
      return function (...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => fn.apply(this, args), delay);
      };
    },
  },
  mounted() {
    this.debouncedHandleSearch = this.debounce(this.handleSearch, 300);
    window.addEventListener("storage", this.checkLoginStatus);
  },
  beforeDestroy() {
    window.removeEventListener("storage", this.checkLoginStatus);
  },
};
</script>

<style scoped>
@import "../assets/scss/Header.scss";

.search-bar {
  position: relative;
}

.clear-search {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
}
</style>
