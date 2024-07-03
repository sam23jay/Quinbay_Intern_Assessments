const form = document.getElementById("product-form");
const productList = document.getElementById("product-list");
const searchInput = document.querySelector("#header .search-bar input");
const clearSearch = document.getElementById("clear-search");
const cartIcon = document.getElementById("cart-icon");
const cartPopup = document.getElementById("cart-modal");
const closeCartPopup = document.getElementById("close-cart-modal");
const cartItemsContainer = document.getElementById("cart-items");
const checkoutButton = document.getElementById("checkout-button");
const availabilityCheckbox = document.getElementById("availability");
const minPriceInput = document.getElementById("minPrice");
const maxPriceInput = document.getElementById("maxPrice");
const applyFiltersButton = document.getElementById("applyFilters");
const resetFiltersButton = document.getElementById("resetFilters");
let products = [];
let cart = [];

form.addEventListener("submit", (e) => {
  e.preventDefault();

  const name = form.name.value.trim();
  const brand = form.brand.value.trim();
  const originalPrice = form.originalPrice.value.trim();
  const discountPrice = form.discountPrice.value.trim();
  const stock = form.stock.value.trim();
  const category = form.Category.value;
  const seller = form.Seller.value;

  if (
    !name ||
    !brand ||
    !originalPrice ||
    !discountPrice ||
    !stock ||
    !category ||
    !seller
  ) {
    alert("All fields must be filled out");
    return;
  }

  if (isNaN(originalPrice) || isNaN(discountPrice) || isNaN(stock)) {
    alert("Original Price, Discounted Price, and Stock must be numbers");
    return;
  }
  if (originalPrice <= discountPrice) {
    alert("Invalid prices entered");
  }
  const product = {
    name,
    brand,
    originalPrice: parseFloat(originalPrice),
    discountPrice: parseFloat(discountPrice),
    stock: parseInt(stock),
    category,
    seller,
    imageUrl:
      "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTCSZZGy55E3_-gPfi_IRbtMwCmWby9Wg6nLg&s",
  };

  products.push(product);
  displayProducts(products);
  form.reset();
});

function debounce(func, delay) {
  let timeout;
  return function (...args) {
    clearTimeout(timeout);
    timeout = setTimeout(() => func.apply(this, args), delay);
  };
}

const handleSearch = debounce(() => {
  const searchTerm = searchInput.value.toLowerCase();
  if (searchTerm) {
    clearSearch.style.display = "block";
  } else {
    clearSearch.style.display = "none";
  }

  const filteredProducts = products.filter((product) =>
    product.name.toLowerCase().includes(searchTerm)
  );
  displayProducts(filteredProducts);
}, 300);

searchInput.addEventListener("input", handleSearch);

clearSearch.addEventListener("click", () => {
  searchInput.value = "";
  clearSearch.style.display = "none";
  displayProducts(products);
});

cartIcon.addEventListener("click", () => {
  displayCartItems();
  cartPopup.style.display = "block";
});

closeCartPopup.addEventListener("click", () => {
  cartPopup.style.display = "none";
});

window.addEventListener("click", (event) => {
  if (event.target == cartModal) {
    cartPopup.style.display = "none";
  }
});

applyFiltersButton.addEventListener("click", () => {
  const isAvailable = availabilityCheckbox.checked;
  const minPrice = parseFloat(minPriceInput.value) || 0;
  const maxPrice = parseFloat(maxPriceInput.value) || Number.MAX_VALUE;

  const filteredProducts = products.filter((product) => {
    const isAvailability = !isAvailable || product.stock > 0;
    const isPriceRange =
      product.discountPrice >= minPrice && product.discountPrice <= maxPrice;
    return isAvailability && isPriceRange;
  });

  displayProducts(filteredProducts);
});

resetFiltersButton.addEventListener("click", () => {
  availabilityCheckbox.checked = false;
  minPriceInput.value = "";
  maxPriceInput.value = "";
  displayProducts(products);
});

function displayProducts(productsToDisplay) {
  productList.innerHTML = "";
  productsToDisplay.forEach((product, index) => {
    const productCard = document.createElement("div");
    productCard.classList.add("product-card");
    productCard.innerHTML = `
        <img
          src="${product.imageUrl}"
          alt="Product Image"
        />
        <p class="seller">${product.seller}</p>
        <h3>${product.name}</h3>
        <div class="price-details">
          <span class="price">₹${product.discountPrice}</span>
          <span class="original-price">₹${product.originalPrice}</span>
          <span class="discount">${(
            ((product.originalPrice - product.discountPrice) /
              product.originalPrice) *
            100
          ).toFixed(2)}% off</span>
        </div>
        ${
          product.stock === 0
            ? '<p class="out-of-stock">Out of Stock</p>'
            : '<p class="in-stock">In Stock</p>'
        }
        <button class="add-to-cart" data-index="${index}">Add to Cart</button>
      `;
    productList.appendChild(productCard);
  });

  const addToCartButtons = document.querySelectorAll(".add-to-cart");
  addToCartButtons.forEach((button) =>
    button.addEventListener("click", (e) => {
      const index = e.target.dataset.index;
      addToCart(index);
    })
  );
}

function addToCart(index) {
  const product = products[index];
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
  console.log(cart);
}

function displayCartItems() {
  cartItemsContainer.innerHTML = "";
  let totalPrice = 0;
  cart.forEach((item, cartIndex) => {
    const product = products[item.index];
    totalPrice += product.discountPrice * item.quantity;
    const cartItem = document.createElement("div");
    cartItem.classList.add("cart-item");
    cartItem.innerHTML = `
        <img src="${product.imageUrl}" alt="${product.name}" class="cart-item-image"/>
        <div class="cart-item-details">
          <p>${product.name} - ₹${product.discountPrice} x ${item.quantity}</p>
          <div class="quantity-controls">
            <button onclick="updateQuantity(${cartIndex}, -1)">-</button>
            <span>${item.quantity}</span>
            <button onclick="updateQuantity(${cartIndex}, 1)">+</button>
          </div>
        </div>
      `;
    cartItemsContainer.appendChild(cartItem);
  });

  const totalPriceElement = document.createElement("div");
  totalPriceElement.classList.add("total-price");
  totalPriceElement.innerHTML = `<p>Total Price: ₹${totalPrice.toFixed(2)}</p>`;
  cartItemsContainer.appendChild(totalPriceElement);
}

window.updateQuantity = (cartIndex, change) => {
  const cartItem = cart[cartIndex];
  const product = products[cartItem.index];
  if (cartItem.quantity + change > 0) {
    if (cartItem.quantity + change > product.stock) {
      alert("Cannot add more than available stock.");
    } else {
      cartItem.quantity += change;
    }
  } else {
    cart.splice(cartIndex, 1);
  }
  displayCartItems();
};

checkoutButton.addEventListener("click", () => {
  cart.forEach((cartItem) => {
    const product = products[cartItem.index];
    if (product) {
      product.stock -= cartItem.quantity;
    }
  });

  cart = [];
  cartPopup.style.display = "none";
  displayProducts(products);
});
