package com.example.eccentricclothing.controller;

import com.example.eccentricclothing.dto.ProductDTO;
import com.example.eccentricclothing.model.*;
import com.example.eccentricclothing.repository.OrderRepository;
import com.example.eccentricclothing.repository.OrderStatusUpdateRepository;
import com.example.eccentricclothing.repository.ProductRepository;
import com.example.eccentricclothing.service.*;
import com.example.eccentricclothing.util.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    public static String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/productImages";

    @Autowired
    CategoryService categoryService;

    @Autowired
    OrderStatusUpdateRepository orderStatusUpdateRepository;

    @Autowired
    CouponService couponService;

    @Autowired
    OrderService orderService;

    @Autowired
    ProductService productService;

    @Autowired
    UserService userService;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;



    @PostMapping("/orderstatus/{orderId}/update-status")
    public String updateOrderStatus(@PathVariable Long orderId, @RequestParam OrderStatus newStatus) {
        System.out.println("order id           :"+orderId);
        System.out.println("newStatus           :"+newStatus);
        orderService.updateOrderStatus(orderId, newStatus);
        return "redirect:/admin/orderlist";
    }

    @GetMapping("/couponlist")
    public String getCouponList(Model model){
        model.addAttribute("coupons",couponService.getAllCoupon());
        return "couponlist";
    }


    @GetMapping("/couponadd")
    public String getCouponAdd(Model model){
        model.addAttribute("coupon",new Coupon());
        return "couponAdd";
    }

    @PostMapping("/couponsave")
    public String saveCoupon(@ModelAttribute("coupon") Coupon coupon) {
        coupon.setStartDate(LocalDate.parse(coupon.getStartDateString()));
        coupon.setEndDate(LocalDate.parse(coupon.getEndDateString()));
        couponService.saveCoupon(coupon);
        return "redirect:/admin/couponlist";
    }

    @GetMapping("/couponDelete/{id}")
    public String deleteCoupon(@PathVariable("id") Long id) {
        couponService.deleteCoupon(id);
        return "redirect:/admin/couponlist";
    }

    @GetMapping("/couponUpdate/{id}")
    public String updateCoupon(@PathVariable Long id, Model model) {
        Optional<Coupon> coupon = couponService.getCouponById(id);

        if (coupon.isPresent()) {
            model.addAttribute("coupon", coupon.get());
            return "couponAdd";
        } else {
            return "404";
        }
    }

    @GetMapping("/")
    public String displayDashBoard(){
        return "dashboard";
    }






    @GetMapping("/categoryadd")
    public String getCategoryadd(Model model){
        model.addAttribute("category",new Category());
        return "categoryadd";
    }

    @PostMapping("/categoryadd")
    public String postCategoryadd(@ModelAttribute("category") Category category, Model model){
        if (categoryService.isCategoryUnique(category.getName())) {
            categoryService.addCategory(category);
            return "redirect:/admin/categorylist";
        }
        else{
            model.addAttribute("categoryExists", true);
            return "categoryadd";
        }
    }

    @PostMapping("/categoryaddproduct")
    public String postCategoryaddProduct(
            @RequestParam("name") String categoryName,
            @RequestParam("description") String categoryDescription,
            Model model) {
        Category category = new Category();
        category.setName(categoryName);
        category.setDescription(categoryDescription);

        if (categoryService.isCategoryUnique(category.getName())) {
            categoryService.addCategory(category);
            return "redirect:/admin/productadd";
        } else {
            model.addAttribute("categoryExists", true);
            return "/productadd";
        }
    }


    @GetMapping("/categorylist")
    public String getCategoryList(Model model){
        model.addAttribute("categories",categoryService.getAllCategory());
        return "categorylist";
    }

    @GetMapping("/orderlist")
    public String getOrderList(Model model){
        List<Order> orders = orderService.getAllOrder();
        System.out.println(orders.size());
        model.addAttribute("orders",orders);
        return "orderlist";
    }


    @GetMapping("/{id}")
    public String deleteCategory(@PathVariable int id) {
        categoryService.removeById(id);
        return "redirect:categorylist";
    }

    @GetMapping("/update/{id}")
    public String updateCat(@PathVariable int id,Model model) {
        Optional<Category> category = categoryService.getCategoryById(id);
         if(category.isPresent()){
             model.addAttribute("category",category.get());
             return "categoryadd";
         }
         else
             return "404";
    }

    @GetMapping("/productadd")
    public String productAdd(Model model){
        model.addAttribute("productDTO",new ProductDTO());
        model.addAttribute("categories",categoryService.getAllCategory());
        return "productadd";
    }

    @PostMapping("/productadd")
    public String postProductAdd(@ModelAttribute("productDTO") ProductDTO productDTO,
                                 @RequestParam("productImages") List<MultipartFile> files,
                                 @RequestParam("imgNames") List<String> imgNames, Model model, RedirectAttributes redirectAttributes) throws IOException {
        Optional<Product> existingProduct = productRepository.findByName(productDTO.getName());
        if (existingProduct.isPresent()) {
            redirectAttributes.addFlashAttribute("productExists", true);
            return "redirect:/admin/productadd";

        }
        Product product = new Product();
        product.setId(productDTO.getId());
        product.setName(productDTO.getName());
        product.setBrand(productDTO.getBrand());
        product.setGender(productDTO.getGender());
        product.setCategory(categoryService.getCategoryById(productDTO.getCategoryId()).get());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setDescription(productDTO.getDescription());
        List<ProductImage> images = new ArrayList<>();
        List<Long> imageIds = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            String imageUUID;
            if (!file.isEmpty()) {
                imageUUID = file.getOriginalFilename();
                Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
                Files.write(fileNameAndPath, file.getBytes());
            } else {
                if (i < imgNames.size()) {
                    imageUUID = imgNames.get(i);
                } else {
                    imageUUID = "images/no_image_available.png";
                }
            }
            ProductImage image = new ProductImage();
            image.setImageName(imageUUID);
            image.setProduct(product);
            images.add(image);
        }
        for (ProductImage image : images) {
            Long imageId = productService.saveImageAndGetId(image.getImageName());
            imageIds.add(imageId);
        }
        product.setImages(images);
        productService.addProduct(product);
        return "redirect:/admin/productlist";
    }

    @GetMapping("/productlist")
    public String getProducts(Model model){
        model.addAttribute("products", productService.getAllProduct());
        return "productlist";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable long id){
        productService.removeProductById(id);
        return "redirect:/admin/productlist";
    }

    @GetMapping("/updateproduct/{id}")
    public String updateProduct(@PathVariable long id, Model model){
        Product product = productService.getProductById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        Category category = product.getCategory();
        if (category != null) {
            productDTO.setCategoryId(category.getId());
        }
        productDTO.setPrice(product.getPrice());
        productDTO.setQuantity(product.getQuantity());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageNames(product.getImages().stream().map(ProductImage::getImageName).collect(Collectors.toList()));

        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("productDTO", productDTO);

        return "productadd";
    }

    @GetMapping("/customerlist")
    public String customerList(Model model) {
        List<User> usersWithUserRole = userService.getUsersWithUserRole();
        model.addAttribute("users", usersWithUserRole);
        return "customerlist";
    }

    @PostMapping("/block/{id}")
    public String blockUser(@PathVariable("id") Integer userId, @RequestParam("enable") boolean enable) {
        userService.blockUser(userId, enable);
        return "redirect:/admin/customerlist"; // Redirect to the customer list page
    }

    @PostMapping("/unblock/{id}")
    public String unblockUser(@PathVariable("id") Integer userId, @RequestParam("enable") boolean enable) {
        userService.unBlockUser(userId, enable);
        return "redirect:/admin/customerlist"; // Redirect to the customer list page
    }

}
