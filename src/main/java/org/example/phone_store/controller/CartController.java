package org.example.phone_store.controller;

import jakarta.servlet.http.HttpSession;
import org.example.phone_store.entity.CartItem;
import org.example.phone_store.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    private Integer getUserId(HttpSession session) {
        return (Integer) session.getAttribute("userId");
    }

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Integer userId = getUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        List<CartItem> cartItems = cartService.getCart(userId);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", cartService.getTotal(cartItems));
        return "cart";
    }

    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addToCart(
            @RequestParam("productId") Integer productId,
            @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
            HttpSession session) {

        Map<String, Object> result = new HashMap<>();
        Integer userId = getUserId(session);

        if (userId == null) {
            result.put("success", false);
            result.put("message", "Vui lòng đăng nhập để thêm vào giỏ hàng");
            return result;
        }

        cartService.addToCart(userId, productId, quantity);
        result.put("success", true);
        return result;
    }

    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam("productId") Integer productId,
            @RequestParam("quantity") Integer quantity,
            HttpSession session) {

        Integer userId = getUserId(session);
        if (userId == null) return "redirect:/login";

        cartService.updateQuantity(userId, productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Integer productId, HttpSession session) {
        Integer userId = getUserId(session);
        if (userId == null) return "redirect:/login";

        cartService.removeItem(userId, productId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(HttpSession session) {
        Integer userId = getUserId(session);
        if (userId == null) return "redirect:/login";

        cartService.clearCart(userId);
        return "redirect:/cart";
    }
}