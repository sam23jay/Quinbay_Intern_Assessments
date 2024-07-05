<template>
  <div class="login-signup-container">
    <div class="login-signup-box">
      <h2>Sign In</h2>
      <form @submit.prevent="login">
        <div class="input-group">
          <input
            type="text"
            v-model="username"
            placeholder="Username or email"
            required
          />
        </div>
        <div class="input-group">
          <input
            type="password"
            v-model="password"
            placeholder="Password"
            required
          />
        </div>
        <button type="submit">Sign In</button>
      </form>
      <div class="footer-links">
        <router-link to="/signup"
          >Dont have an account? Sign up instead!</router-link
        >
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: "",
      password: "",
    };
  },
  methods: {
    login() {
      const users = JSON.parse(localStorage.getItem("users")) || [];
      const user = users.find(
        (user) =>
          user.username === this.username && user.password === this.password
      );
      if (user) {
        alert("Login successful!");
        localStorage.setItem("loggedInUser", JSON.stringify(user));
        this.$router.push("/");
      } else {
        alert("Invalid username or password.");
      }
    },
    forgotPassword() {
      alert("Password recovery process.");
    },
  },
};
</script>

<style scoped>
@import "../assets/scss/LoginSignUp.scss";
</style>
