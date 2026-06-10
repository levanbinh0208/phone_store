package org.example.phone_store.controller;

import org.example.phone_store.entity.Product;
import org.example.phone_store.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    private final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping({"/", "/admin"})
    public String home(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.trim().isEmpty()) {
            model.addAttribute("products", productService.searchProducts(search.trim()));
            model.addAttribute("searchTerm", search);
        } else {
            model.addAttribute("products", productService.getAllProducts());
        }
        return "admin/product";
    }

    @GetMapping("/admin/products")
    public String listProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "admin/products";
    }

    @GetMapping("/admin/products/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        return "admin/add-product";
    }

    @PostMapping("/admin/products/add")
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("image") MultipartFile image,
                             RedirectAttributes redirectAttributes) {

        try {
            if (!image.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);

                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                product.setImageUrl("/uploads/" + fileName);
            }

            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm thành công!");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi upload ảnh!");
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        return "admin/edit-product";
    }

    @PostMapping("/admin/products/edit")
    public String updateProduct(@ModelAttribute Product product,
                                @RequestParam("image") MultipartFile image,
                                RedirectAttributes redirectAttributes) {

        try {
            if (!image.isEmpty()) {
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = UUID.randomUUID().toString() + "_" + image.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);

                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                product.setImageUrl("/uploads/" + fileName);
            }

            productService.saveProduct(product);
            redirectAttributes.addFlashAttribute("success", "Cập nhật sản phẩm thành công!");

        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật ảnh!");
        }

        return "redirect:/admin/products";
    }

    @GetMapping("/admin/products/delete/{id}")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("success", "Xóa sản phẩm thành công!");
        return "redirect:/admin/products";
    }
}