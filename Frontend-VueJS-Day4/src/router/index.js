import { createRouter, createWebHistory } from "vue-router";
import { app, pages } from "@/config";
import HomePage from "@/pages/HomePage.vue";
import ViewProducts from "@/pages/ViewProducts.vue";
import AddProduct from "@/pages/AddProduct.vue";
import LoginPage from "@/pages/LoginPage.vue";
import SignUpPage from "@/pages/SignUpPage.vue";
import ViewProductById from "@/pages/ViewProductById.vue";

const AboutPage = () =>
  import(/* webpackChunkName: "p-about" */ "@/pages/AboutPage.vue");

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: pages.home,
      name: app.home,
      component: HomePage,
    },
    {
      path: pages.viewProducts,
      name: app.viewProducts,
      component: ViewProducts,
    },
    {
      path: pages.addProduct,
      name: app.addProduct,
      component: AddProduct,
      meta: { requiresAuth: true },
    },
    {
      path: pages.about,
      name: app.about,
      component: AboutPage,
    },
    {
      path: pages.login,
      name: app.login,
      component: LoginPage,
    },
    {
      path: pages.signUp,
      name: app.signUp,
      component: SignUpPage,
    },
    {
      path: pages.viewProductById,
      name: app.viewProductById,
      component: ViewProductById,
    },
  ],
});

router.beforeEach((to, from, next) => {
  const isLoggedIn = !!JSON.parse(localStorage.getItem("loggedInUser"));

  if (to.matched.some((record) => record.meta.requiresAuth)) {
    if (!isLoggedIn) {
      next({ path: pages.login });
    } else {
      next();
    }
  } else if (to.path === pages.login && isLoggedIn) {
    next(pages.home);
  } else {
    next();
  }
});

export default router;
