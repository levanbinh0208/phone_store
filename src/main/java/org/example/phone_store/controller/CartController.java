package org.example.phone_store.controller;

import jakarta.servlet.http.HttpSession;
import org.example.phone_store.entity.CartItem;
import org.example.phone_store.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
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
        if (userId == null) {return "redirect:/login";}

        List<CartItem> cartItems = cartService.getCart(userId);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", cartService.getTotal(cartItems));
        return "cart";
    }

    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addToCart(@RequestParam("productId") Integer productId, @RequestParam(value = "quantity", defaultValue = "1") Integer quantity,
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
    public String updateQuantity(@RequestParam("productId") Integer productId, @RequestParam("quantity") Integer quantity,
            HttpSession session,RedirectAttributes redirectAttributes) {

        Integer userId = getUserId(session);
        if (userId == null) return "redirect:/login";
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công");
        cartService.updateQuantity(userId, productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{id}")
    public String removeItem(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        cartService.removeItem(userId, id);
        redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng!");
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(HttpSession session) {
        Integer userId = getUserId(session);
        if (userId == null) return "redirect:/login";

        cartService.clearCart(userId);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        List<CartItem> cartItems = cartService.getCart(userId);
        BigDecimal total = cartItems.stream().map(item -> item.getProduct()
                                .getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);

        return "checkout";
    }

    @PostMapping("/checkout")
    public String placeOrder(
            @RequestParam String fullName,
            @RequestParam String phone,
            @RequestParam String address,
            HttpSession session,
            RedirectAttributes ra) {

        Integer userId =
                (Integer) session.getAttribute("userId");

        List<CartItem> cartItems =
                cartService.getCart(userId);

        if(cartItems.isEmpty()){
            ra.addFlashAttribute(
                    "error",
                    "Giỏ hàng trống");
            return "redirect:/cart";
        }

        Order order = new Order();

        order.setUserId(userId);
        order.setStatus("PENDING");

        double total = 0;

        for(CartItem item : cartItems){
            total += item.getProduct().getPrice()
                    * item.getQuantity();
        }

        order.setTotalAmount(total);

        orderRepository.save(order);

        for(CartItem item : cartItems){

            OrderDetail detail =
                    new OrderDetail();

            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(
                    item.getProduct().getPrice());

            orderDetailRepository.save(detail);
        }

        cartService.clearCart(userId);

        ra.addFlashAttribute(
                "success",
                "Đặt hàng thành công");

        return "redirect:/orders";
    }
}