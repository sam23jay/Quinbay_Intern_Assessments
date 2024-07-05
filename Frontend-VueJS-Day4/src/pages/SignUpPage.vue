<template>
  <div class="login-signup-container">
    <div class="login-signup-box">
      <h2>Sign Up</h2>
      <form @submit.prevent="signUp">
        <div class="input-group">
          <input
            type="text"
            v-model="username"
            placeholder="Username"
            required
          />
        </div>
        <div class="input-group">
          <input type="text" v-model="name" placeholder="Name" required />
        </div>
        <div class="input-group">
          <input
            type="password"
            v-model="password"
            placeholder="Password"
            required
          />
        </div>
        <div class="input-group">
          <input type="text" v-model="role" placeholder="Role" required />
        </div>
        <button type="submit">Sign Up</button>
      </form>
      <div class="footer-links">
        <router-link to="/login">Log in instead?</router-link>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      username: "",
      name: "",
      password: "",
      role: "",
    };
  },
  methods: {
    signUp() {
      const users = JSON.parse(localStorage.getItem("users")) || [];
      if (users.find((user) => user.username === this.username)) {
        alert("Username already exists.");
      } else {
        const newUser = {
          username: this.username,
          name: this.name,
          password: this.password,
          role: this.role,
        };
        users.push(newUser);
        localStorage.setItem("users", JSON.stringify(users));
        alert("Sign up successful!");
        this.$router.push("/login");
      }
    },
  },
};
</script>

<style scoped>
@import "../assets/scss/LoginSignUp.scss";
</style>
