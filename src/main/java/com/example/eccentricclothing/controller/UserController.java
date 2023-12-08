package com.example.eccentricclothing.controller;

import com.example.eccentricclothing.model.*;
import com.example.eccentricclothing.repository.CategoryRepository;
import com.example.eccentricclothing.repository.PaymentRepository;
import com.example.eccentricclothing.service.*;
import com.example.eccentricclothing.util.OrderStatus;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private  OrderStatusUpdateService orderStatusUpdateService;

    @Autowired
    private  ReviewService reviewService;

    @Autowired
    private UserService userService;

    @Autowired
    private WishListService wishListService;

    @Autowired
    private CartService cartService;

    @Autowired
    private AddressService addressService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CouponService couponService;


    @GetMapping("/")
    public String landingPageLogin(){
        return "landing-page";
    }

    @GetMapping("/trackOrder/{orderId}")
    public String trackOrder(@PathVariable Long orderId, Model model) {
        List<OrderStatusUpdate> orderStatusUpdates = orderStatusUpdateService.getOrderStatusUpdatesByOrderId(orderId);
        List<OrderStatusUpdate> orderPlacedUpdates = new ArrayList<>();
        List<OrderStatusUpdate> dispatchedUpdates = new ArrayList<>();
        List<OrderStatusUpdate> outForDeliveryUpdates = new ArrayList<>();
        List<OrderStatusUpdate> deliveredUpdates = new ArrayList<>();
        LocalDateTime placedTimestamp = null;
        LocalDateTime dispatchedTimestamp = null;
        LocalDateTime outForDeliveryTimestamp = null;
        LocalDateTime deliveredTimestamp = null;
        String placedPlace=null;
        String shippedPlace=null;
        String outFdPlace=null;
        String deliveryPlace=null;

        for (OrderStatusUpdate update : orderStatusUpdates) {
            switch (update.getUpdatedStatus()) {
                case PLACED:
                    orderPlacedUpdates.add(update);
                    placedTimestamp = update.getUpdateDateTime();
                    placedPlace = update.getPlaceReached();
                    break;
                case SHIPPED:
                    dispatchedUpdates.add(update);
                    dispatchedTimestamp = update.getUpdateDateTime();
                    shippedPlace = update.getPlaceReached();
                    break;
                case OUT_FOR_DELIVERY:
                    outForDeliveryUpdates.add(update);
                    outForDeliveryTimestamp = update.getUpdateDateTime();
                    outFdPlace = update.getPlaceReached();
                    break;
                case DELIVERED:
                    deliveredUpdates.add(update);
                    deliveredTimestamp = update.getUpdateDateTime();
                    deliveryPlace = update.getPlaceReached();
                    break;
                default:
                    break;
            }

            // Print updateDateTime for debugging
            System.out.println("updateDateTime: " + update.getUpdateDateTime());
        }


        // Add timestamps to the model
        model.addAttribute("placedTimestamp", placedTimestamp);
        model.addAttribute("dispatchedTimestamp", dispatchedTimestamp);
        model.addAttribute("outForDeliveryTimestamp", outForDeliveryTimestamp);
        model.addAttribute("deliveredTimestamp", deliveredTimestamp);

        model.addAttribute("placedPlace", placedPlace);
        model.addAttribute("shippedPlace", shippedPlace);
        model.addAttribute("outFdPlace", outFdPlace);
        model.addAttribute("deliveryPlace", deliveryPlace);

        return "trackOrder";
    }




    @GetMapping("/coupons")
    public ResponseEntity<List<Coupon>> getCoupons() {
        List<Coupon> coupons = couponService.getAllCoupon();
        return ResponseEntity.ok(coupons);
    }

    @PostMapping("/saveReview")
    public String postReviewAdd(@ModelAttribute("review") Review review,
                                @RequestParam("productId") Long productId){
        User user = userService.getLoggedInUser();
        Product product = productService.getProductById(productId);
        review.setUser(user);
        review.setProduct(product);
        reviewService.saveReview(review);
        return "landing-page";
    }


    @GetMapping("/byFilters")
    public String getProductsByFilters(@RequestParam(name = "brands", required = false) List<String> brands,
                                       @RequestParam(name = "genders", required = false) List<String> genders,
                                       @RequestParam(name = "size", required = false) List<String> sizes,
                                       @RequestParam(name = "price", required = false,defaultValue = "0") double price,
                                       Model model) {
        List<Product> products = productService.getProductsByFilters(brands, genders,sizes,price);

        List<String> brandNames = new ArrayList<>();
        Set<String> uniqueSizes = new HashSet<>();
        for (Product product : products) {
            brandNames.add(product.getBrand());
            if (uniqueSizes.add(product.getSize())) {
                uniqueSizes.add(product.getSize());
            }
        }
        model.addAttribute("brandNames", brandNames);
        model.addAttribute("products", products);
        model.addAttribute("size",uniqueSizes);

        return "showproducts";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "category", required = false) String category,
            Model model
    ) {
        List<Product> products = productService.searchProducts(query, category);
        List<String> brandNames = new ArrayList<>();
        Set<String> uniqueSizes = new HashSet<>();
        for (Product product : products) {
            brandNames.add(product.getBrand());
            if (uniqueSizes.add(product.getSize())) {
                uniqueSizes.add(product.getSize());
            }
        }
        model.addAttribute("brandNames", brandNames);
        model.addAttribute("products", products);
        model.addAttribute("size",uniqueSizes);
        return "showproducts";
    }

    @GetMapping("/placeorder")
    public String placeOrder( Model model)
    {
        User user = userService.getLoggedInUser();
        Cart cart = userService.getUserCart(user);
        double totalCartAmount = cart.getTotalPrice();
        List<Address> userAddresses = addressService.getAddressesByUser(user);
        model.addAttribute("total", totalCartAmount);
        model.addAttribute("address",userAddresses);
        return "placeorder";
    }


    @PostMapping("/create_order")
    @ResponseBody
    public String createOrder(@RequestBody Map<String, Object> data, Principal principal) throws Exception {





        try {

            System.out.println(data);

            double amt = Double.parseDouble(data.get("amount").toString());
            var client = new RazorpayClient("rzp_test_SFjHv4xXWflKId", "04OdyFbM1EuvizwDFUaD9p8D");

            JSONObject ob = new JSONObject();
            ob.put("amount", amt*100);
            ob.put("currency", "INR");
            ob.put("receipt", "txn_123435");

            //create new order
            com.razorpay.Order order = client.orders.create(ob);
            System.out.println(order);

            //save the order in database
            Payment myOrder = new Payment();
            myOrder.setAmount(order.get("amount")+ "");
            myOrder.setOrderId(order.get("id"));
            myOrder.setPaymentId(null);
            myOrder.setPaymentStatus("created");
            myOrder.setUser(userService.getUserByEmail(principal.getName()));
            myOrder.setReceipt(order.get("receipt"));

            this.paymentRepository.save(myOrder);

            return order.toString();




        } catch (Exception e) {
            e.printStackTrace();
            // Log the exception or return a meaningful error message
            return "Error processing the order: " + e.getMessage();
        }

    }

    @PostMapping("/placeOrderBuy")
        public String placeOrder(@RequestParam("selectedAddressId") Long selectedAddressId,
                             @RequestParam("paymentMethod") String paymentMethod,
                             Model model)  {

        User user = userService.getLoggedInUser();
        Cart cart = userService.getUserCart(user);


                Map<Long, Integer> productCountMap = new HashMap<>();
        for (Product product : cart.getProducts()) {
            Long productId = product.getId();
            productCountMap.put(productId, productCountMap.getOrDefault(productId, 0) + 1);
        }
        productService.reduceProductQuantities(productCountMap);
        for (Map.Entry<Long, Integer> entry : productCountMap.entrySet()) {
            Long productId = entry.getKey();
            Integer count = entry.getValue();
            Product product = productService.getProductById(productId);
            Order orders = new Order();
            orders.setUser(user);
            Optional<Address> selectedAddress = addressService.getAddressById(selectedAddressId);
            orders.setShippingAddressString(selectedAddress.get().getFullAddress());
            orders.setOrderDateTime(LocalDateTime.now());
            orders.setProductName(product.getName());
            orders.setPaymentMethod("Razorpay");
            orderService.saveOrder(orders);

            OrderStatusUpdate orderStatusUpdate = new OrderStatusUpdate();
            orderStatusUpdate.setOrder(orders);
            orderStatusUpdate.setUpdatedStatus(OrderStatus.PLACED);
            orderStatusUpdate.setUpdateDateTime(LocalDateTime.now());
            orderStatusUpdateService.saveOrderStatusUpdate(orderStatusUpdate);
        }

        return "redirect:/admin/userOrder";
    }




    @GetMapping("/orders")
    public String viewOrders(Model model) {
        User user = userService.getLoggedInUser();
        List<Order> orders = orderService.getOrdersByUser(user);
        model.addAttribute("orders", orders);
        return "userOrder";
    }

    @GetMapping("/cancelOrder/{orderId}")
    public String cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return "redirect:/user/orders";
    }

    @GetMapping("/returnOrder/{orderId}")
    public String returnOrder(@PathVariable Long orderId) {
        orderService.returnOrder(orderId);
        return "redirect:/user/orders";
    }


    @GetMapping("/cart")
    public String getCart(Model model) {
        User user = userService.getLoggedInUser();
        Cart cart = userService.getUserCart(user);
        double totalCartAmount = cart.getTotalPrice();

        Set<Long> uniqueProductIds = new HashSet<>();
        List<Product> uniqueProducts = new ArrayList<>();
        Map<Long, Integer> productCounts = new HashMap<>();

        for (Product product : cart.getProducts()) {
            Long productId = product.getId();
            if (!uniqueProductIds.contains(productId)) {
                uniqueProducts.add(product);
                uniqueProductIds.add(productId);
                productCounts.put(productId, 1);
            } else {
                productCounts.put(productId, productCounts.get(productId) + 1);
            }
        }

        model.addAttribute("cartCount", uniqueProducts.size());
        model.addAttribute("productCounts", productCounts);
        model.addAttribute("total", totalCartAmount);
        model.addAttribute("cart", uniqueProducts);
        return "cart";
    }


    @PostMapping("/addtocart")
    public String addToCart(@RequestParam Long productId, Model model) {
        Product product = productService.getProductById(productId);
        User user = userService.getLoggedInUser();
        userService.addToUserCart(user, product);
        return "redirect:/user/cart";
    }

    @PostMapping("/removefromCart")
    public String removeFromCart(@RequestParam Long productId) {
        cartService.removeProductFromCart(productId);
        return "redirect:/user/cart";
    }








    @GetMapping("/address")
    public String showAddress(Model model){
        User user = userService.getLoggedInUser();
        List<Address> userAddresses = addressService.getAddressesByUser(user);
        model.addAttribute("address",userAddresses);
        return "addresses";
    }

    @GetMapping("/addaddress")
    public String addAddress(Model model){
        model.addAttribute("address", new Address());
        return "addaddress";
    }

    @PostMapping("/addaddressform")
    public String addAddressPost(@ModelAttribute Address address) {
        User user = userService.getLoggedInUser();
        System.out.println("edwjhcgvdhjbd"+user.getEmail());
        System.out.println("wewjhdgdshgvdfghj" +address);
        addressService.saveAddress(address,user);
        return "redirect:/user/address";
    }

    @GetMapping("/removeAddress/{addressId}")
    public String removeAddress(@PathVariable Long addressId, Model model) {
        System.out.println(addressId);
        addressService.removeAddressById(addressId);
        return "redirect:/user/address";
    }

    @GetMapping("/editAddress/{addressId}")
    public String editAddress(@PathVariable Long addressId, Model model) {
        Optional<Address> address = addressService.getAddressById(addressId);
        model.addAttribute("address", address);
        return "editAddress";
    }

    @PostMapping("/editAddressPost")
    public String editAddressPost(@ModelAttribute("address") Address updatedAddress) {
        System.out.println("Updating address with ID: " + updatedAddress.getId());

        addressService.updateAddress(updatedAddress);

        return "redirect:/user/address";
    }












    @PostMapping("/addToWishlist")
    public String addToWishlist(@RequestParam Long productId) {
        User loggedInUser = userService.getLoggedInUser();
        Integer userId = loggedInUser.getId();
        if (!wishListService.isProductInWishlist(userId, productId)) {
            wishListService.addToWishlist(userId, productId);
        }
        return "wishlist";
    }

    @PostMapping("/removefromWishlist")
    public String removeFromWishlist(@RequestParam Long productId) {
        User loggedInUser = userService.getLoggedInUser();
        Integer userId = loggedInUser.getId();
        wishListService.removeWishListById(userId,productId);
        return "redirect:/user/showwishlist";
    }

    @GetMapping("/showwishlist")
    public String showWishList(Model model){
        User currentUser = userService.getLoggedInUser();
        Integer userId= currentUser.getId();
        List<WishList> wishlists = wishListService.getProductsByUserId(userId);
        model.addAttribute("wishlists", wishlists);
        return "wishlist";
    }


    @GetMapping("/profile")
    public String userProfile(Model model) {
        User loggedInUser = userService.getLoggedInUser();
        model.addAttribute("user", loggedInUser);
        return "profiledetails";
    }

    @GetMapping("/editprofileform")
    public String editProfileForm(Model model) {
        User user = userService.getLoggedInUser();
        model.addAttribute("user", user);
        return "profileedit";
    }

    @PostMapping("/editprofile")
    public String editProfileSubmit(@ModelAttribute User user) {
        User existingUser = userService.getLoggedInUser();
        if (existingUser != null) {
            // Update the existing user's details
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            existingUser.setGender(user.getGender());
            existingUser.setMobile(user.getMobile());
            userService.updateUser(existingUser); // Update the user in the database
        }
        return "redirect:/user/profile";
    }


    @GetMapping("/byCategory")
    public String getProductsByCategory(@RequestParam String category, Model model,String gender) {
        List<Product> products = productService.getProductsByCategoryNameAndGender(category,gender);

        List<String> brandNames = new ArrayList<>();
        Set<String> uniqueSizes = new HashSet<>();

        for (Product product : products) {
            brandNames.add(product.getBrand());
            if (uniqueSizes.add(product.getSize())) {
                uniqueSizes.add(product.getSize());
            }
        }
        model.addAttribute("products", products);
        model.addAttribute("brandNames", brandNames);
        model.addAttribute("size", uniqueSizes);
        return "showproducts";
    }




    @GetMapping("/singleproduct/{productId}")
    public String viewSingleProduct(@PathVariable int productId,  Model model) {
        System.out.println(productId);
        Optional<Product> optionalProductproduct = productService.getProductById(productId); // Fetch the product from service
        if(optionalProductproduct.isPresent()){
            Product product = optionalProductproduct.get();
            List<Review> reviews = reviewService.getAllReviewsForProduct((long) productId);
            List<String> imageNames = product.getImages().stream()
                    .map(ProductImage::getImageName)
                    .collect(Collectors.toList());
            model.addAttribute("product", product);
            model.addAttribute("imageNames", imageNames);
            model.addAttribute("review",reviews);
            return "singleproduct";
        }else {
            return "productnotfound";
        }
    }


}
