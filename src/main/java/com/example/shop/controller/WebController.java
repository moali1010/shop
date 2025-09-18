package com.example.shop.controller;

import com.example.shop.dto.DiscountDTO;
import com.example.shop.dto.IngredientDTO;
import com.example.shop.dto.OrderListDTO;
import com.example.shop.dto.UserDTO;
import com.example.shop.model.type.OrderStatus;
import com.example.shop.model.type.UserRole;
import com.example.shop.model.users.UserAccount;
import com.example.shop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class WebController {

    private final UserService userService;
    private final OrderService orderService;
    private final IngredientService ingredientService;
    private final DiscountService discountService;
    private final PaymentService paymentService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute UserDTO userDTO, RedirectAttributes redirectAttributes) {
        try {
            userService.saveUser(userDTO);
            redirectAttributes.addFlashAttribute("success", "ثبت‌نام موفق!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "خطا در ثبت‌نام: " + e.getMessage());
        }
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        UserAccount user = (UserAccount) auth.getPrincipal();
        if (user.getUserRole() == UserRole.ADMIN) {
            model.addAttribute("customers", userService.getAllCustomers());
            model.addAttribute("orders", orderService.getAllOrders());
            model.addAttribute("ingredients", ingredientService.getAllIngredients());
            model.addAttribute("discounts", discountService.getActiveDiscounts());
            model.addAttribute("statuses", List.of(OrderStatus.values()));
            model.addAttribute("ingredientDTO", new IngredientDTO());
            model.addAttribute("discountDTO", new DiscountDTO());
        } else {
            model.addAttribute("myOrders", orderService.getUserOrders(user));
            model.addAttribute("ingredients", ingredientService.getAllIngredients());
            model.addAttribute("activeDiscounts", discountService.getActiveDiscounts());
//            model.addAttribute("profile", userService.toDTO(user));  // For edit
        }
        return "dashboard";
    }

    @PostMapping("/customer/order")
    public String createOrder(@RequestBody OrderListDTO orderDTO, Authentication auth, Model model) {
        UserAccount customer = (UserAccount) auth.getPrincipal();
        try {
            OrderListDTO savedOrder = orderService.createOrder(customer, orderDTO);
            model.addAttribute("success", "سفارش ایجاد شد: ID " + savedOrder.getId());
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";  // Reload dashboard
    }

    @PostMapping("/customer/discount/{orderId}")
    public String applyDiscount(@PathVariable Long orderId, @RequestParam String code, Authentication auth, Model model) {
        try {
            discountService.applyDiscount(orderId, code);
            model.addAttribute("success", "تخفیف اعمال شد!");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/customer/pay/{orderId}")
    public String processPayment(@PathVariable Long orderId, Authentication auth, Model model) {
        try {
            paymentService.createPayment(orderId, "mock_txn_" + System.currentTimeMillis());
            paymentService.confirmPayment("mock_txn_" + System.currentTimeMillis());
            model.addAttribute("success", "پرداخت موفق! سفارش تکمیل شد.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/ingredient")
    public String saveIngredient(@ModelAttribute IngredientDTO dto, Model model) {
        ingredientService.saveIngredient(dto);
        return "redirect:/dashboard";
    }

    @PostMapping("/admin/discount")
    public String saveDiscount(@ModelAttribute DiscountDTO dto, Model model) {
        discountService.saveDiscount(dto);
        return "redirect:/dashboard";
    }

    @PostMapping("/customer/profile")
    public String updateProfile(@ModelAttribute UserDTO dto, Authentication auth, Model model) {
        UserAccount user = (UserAccount) auth.getPrincipal();
        try {
            userService.updateProfile(user.getId(), dto);
            model.addAttribute("success", "پروفایل به‌روزرسانی شد.");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/dashboard";
    }
}