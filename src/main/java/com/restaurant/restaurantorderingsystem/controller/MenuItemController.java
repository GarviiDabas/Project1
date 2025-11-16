package com.restaurant.restaurantorderingsystem.controller;

import com.restaurant.restaurantorderingsystem.entity.MenuItem;
import com.restaurant.restaurantorderingsystem.repository.MenuItemRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class MenuItemController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    // ✅ Admin: Show full menu
    @GetMapping("/menu/manage")
    public String showMenu(@RequestParam(value = "category", required = false) String category,
                           Model model,
                           HttpSession session) {

    	if (category == null || category.trim().isEmpty()) {
    	    category = "All";
    	}

    	List<MenuItem> items = (category != null && !category.equalsIgnoreCase("All"))
    	        ? menuItemRepository.findByCategoryIgnoreCase(category)
    	        : menuItemRepository.findAll();


        boolean isAdmin = session.getAttribute("isAdmin") != null && (boolean) session.getAttribute("isAdmin");

        @SuppressWarnings("unchecked")
        List<com.restaurant.restaurantorderingsystem.model.CartItem> cart =
                (List<com.restaurant.restaurantorderingsystem.model.CartItem>) session.getAttribute("cart");

        int cartCount = (cart != null) ? cart.stream().mapToInt(com.restaurant.restaurantorderingsystem.model.CartItem::getQuantity).sum() : 0;

        model.addAttribute("menuItems", items);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("selectedCategory", category != null ? category : "All");
        model.addAttribute("cartCount", cartCount);

        return "menu";
    }

    // ✅ Handle Add Item
    @PostMapping("/menu/add")
    public String addMenuItem(@RequestParam String name,
                              @RequestParam String description,
                              @RequestParam double price,
                              @RequestParam String category,
                              @RequestParam MultipartFile imageFile,
                              HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "redirect:/login";

        String imageUrl = saveImage(imageFile);
        MenuItem menuItem = new MenuItem(name, description, price, category, imageUrl);
        menuItemRepository.save(menuItem);
        return "redirect:/admin/menu/manage?category=All";
    }

    @GetMapping("/menu/add")
    public String showAddForm(Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "redirect:/login";

        model.addAttribute("menuItem", new MenuItem());
        return "add-item";
    }

    // ✅ Show Edit Form
    @GetMapping("/menu/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "redirect:/login";

        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id: " + id));
        model.addAttribute("menuItem", menuItem);
        return "edit-item";
    }

    // ✅ Handle Update Item
    @PostMapping("/menu/edit/{id}")
    public String updateMenuItem(@PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam String description,
                                 @RequestParam double price,
                                 @RequestParam String category,
                                 @RequestParam(required = false) MultipartFile imageFile,
                                 HttpSession session) {
        if (session.getAttribute("isAdmin") == null) return "redirect:/login";

        MenuItem existing = menuItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item Id: " + id));

        existing.setName(name);
        existing.setDescription(description);
        existing.setPrice(price);
        existing.setCategory(category);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = saveImage(imageFile);
            if (!imageUrl.isEmpty()) {
                existing.setImageUrl(imageUrl);
            }
        }

        menuItemRepository.save(existing);
        return "redirect:/admin/menu/manage?category=All";
    }

    // ✅ Public Menu (Optional)
    @GetMapping("/menu/public")
    public String showPublicMenu(Model model) {
    	model.addAttribute("menuItems", menuItemRepository.findAll());
        return "public-menu";
    }


    // ✅ Utility Method to Save Image
    private String saveImage(MultipartFile imageFile) {
        try {
            if (!imageFile.isEmpty()) {
                String originalFileName = Paths.get(imageFile.getOriginalFilename()).getFileName().toString();
                String uniqueFileName = UUID.randomUUID() + "_" + originalFileName;

                Path uploadDir = Paths.get("uploads"); // This will save to project root: /uploads/

                // 🔍 Log the path
                System.out.println("Upload directory: " + uploadDir.toAbsolutePath());

                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                    System.out.println("Created upload directory.");
                }

                Path filePath = uploadDir.resolve(uniqueFileName);

                // 🔍 Log the target path
                System.out.println("Saving file to: " + filePath.toAbsolutePath());

                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                System.out.println("Image saved successfully: " + uniqueFileName);

                return "/uploads/" + uniqueFileName;
            }
        } catch (IOException e) {
            System.out.println("Error occurred while saving image:");
            e.printStackTrace();
        }
        return "";
    }

    @GetMapping("/menu")
    public String redirectToManage() {
        return "redirect:/admin/menu/manage";
    }


}
