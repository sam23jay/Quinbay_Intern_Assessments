const app = {
  home: "Home",
  about: "About",
  viewProducts: "ViewProducts",
  addProduct: "AddProduct",
  login: "Login",
  signUp: "SignUp",
  viewProductById: "ViewProductById",
};

const pages = {
  home: "/",
  about: "/about",
  viewProducts: "/view-products",
  addProduct: "/add-product",
  login: "/login",
  signUp: "/signup",
  viewProductById: "/view/:id",
};

const api = {
  getAllProducts: "/product/getAll",
  postProduct: "/product/post",
  getById: "/product/get/:id",
  testGetAPI: {
    api: "/backend/test",
  },
  testPostAPI: {
    api: "/backend/test",
  },
};

export { app, pages, api };
