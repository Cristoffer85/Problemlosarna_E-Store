package com.example.shopfrontend.controller;


import com.example.shopfrontend.http.BasketHttp;
import com.example.shopfrontend.http.OrderHttp;
import com.example.shopfrontend.http.ProductHttp;
import com.example.shopfrontend.models.dto.BasketDTO;
import com.example.shopfrontend.models.dto.OrderDTO;
import com.example.shopfrontend.models.dto.ProductDTO;
import com.example.shopfrontend.models.dto.UpdateBasketDTO;
import com.example.shopfrontend.models.userDetails.UserDTO;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.util.List;

import com.example.shopfrontend.http.UserHttp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;

import static com.example.shopfrontend.controller.IndexController.currentUser;

@Slf4j
@Controller
public class UserController {


    private final ProductHttp productHttp;
    private final OrderHttp orderHttp;
    private final BasketHttp basketHttp;
    private final UserHttp userHttp;

    private final String UNAUTHORIZED_URL = "/unauthorized";
    private final String ERROR_URL = "/error";

    String message = "";

    int status = 0;



    public UserController(ProductHttp productHttp, OrderHttp orderHttp, BasketHttp basketHttp, UserHttp userHttp) {
        this.productHttp = productHttp;
        this.orderHttp = orderHttp;
        this.basketHttp = basketHttp;
        this.userHttp = userHttp;
    }

    @GetMapping("/user")
    public String userIndex(Model model) throws IOException, ParseException {
        if(currentUser.getRole() == null) {
            return "redirect:" + UNAUTHORIZED_URL;
        }
        List<ProductDTO> products = productHttp.getAllProducts();
        if(products == null) {
            return "redirect:" + ERROR_URL;
        } else {
            model.addAttribute("products", products);
            model.addAttribute("username", currentUser.getUsername());
            model.addAttribute("newProduct", new UpdateBasketDTO());
            model.addAttribute("message", message);
            return "user_index";
        }
    }


    @GetMapping("/user/basket")
    public String getBasket(Model model) throws IOException, ParseException {
        if(currentUser.getRole() == null) {
            return "redirect:" + UNAUTHORIZED_URL;
        }
        BasketDTO basket = basketHttp.getBasket(currentUser.getToken());
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("newProduct", new UpdateBasketDTO());
        model.addAttribute("basket", basket);
        if(basket == null) {
            return "user_basket_empty";
        }
        return "user_basket";
    }

    @GetMapping("/user/basket/add/{id}")
    public String addToBasket(@PathVariable long id) throws IOException {
        if(currentUser.getRole() == null) {
            return "redirect:" + UNAUTHORIZED_URL;
        }
        UpdateBasketDTO newProduct = new UpdateBasketDTO();
        newProduct.setProductId(id);
        newProduct.setQuantity(1);
        status = basketHttp.addProductToBasket(newProduct, currentUser.getToken());
        if (status == 200 || status == 400) {
            return "redirect:/user";
        }
        if (status == 401 || status == 403) {
            return "redirect:" + UNAUTHORIZED_URL;
        } else {
            return "redirect:" + ERROR_URL;
        }
    }

    @PostMapping("/user/basket/edit/{id}")
    public String updateBasketItem(@PathVariable long id ,@ModelAttribute UpdateBasketDTO newProduct) throws IOException {
        if(currentUser.getRole() == null) {
            return "redirect:" + UNAUTHORIZED_URL;
        }
        log.info("updateBasketItem: " + id + " " + newProduct);
        newProduct.setProductId(id);
        status = basketHttp.updateProductQuantityInBasket(newProduct, currentUser.getToken());
        if(status == 200) {
            return "redirect:/user/basket";
        }
        if (status == 401 || status == 403) {
            return "redirect:" + UNAUTHORIZED_URL;
        } else {
            return "redirect:" + ERROR_URL;
        }
    }

    @GetMapping("/user/basket/remove/{id}")
    public String removeBasketItem(@PathVariable long id) throws IOException {
        if(currentUser.getRole() == null) {
            return "redirect:" + UNAUTHORIZED_URL;
        }
        UpdateBasketDTO itemToRemove = new UpdateBasketDTO();
        itemToRemove.setProductId(id);
        log.info("removeBasketItem: " + itemToRemove);
        status = basketHttp.removeProductFromBasket(itemToRemove, currentUser.getToken());
        if(status == 204) {
            return "redirect:/user/basket";
        }
        if (status == 401 || status == 403) {
            return "redirect:" + UNAUTHORIZED_URL;
        } else {
            return "redirect:" + ERROR_URL;
        }
    }


    @GetMapping("/user/details")
    public String viewUserDetails(Model model) throws IOException, ParseException {
        if(currentUser.getRole() == null) {
            return "redirect:" + UNAUTHORIZED_URL;
        }
        model.addAttribute("username", currentUser.getUsername());
        UserDTO user = userHttp.getUserDetails(currentUser.getToken());
        if (user == null) {
            return "redirect:" + ERROR_URL;
        }
        model.addAttribute("user", user);
        return "user_details";
    }

    @GetMapping("/user/orders")
    public String getOrders(Model model) throws IOException, ParseException {
        if(currentUser.getRole() == null) {
            return "redirect:" + UNAUTHORIZED_URL;
        }
        OrderDTO orders = orderHttp.getAllOrdersForOne(currentUser.getToken());
        model.addAttribute("username", currentUser.getUsername());
        model.addAttribute("pastOrders", orders);
        if(orders == null) {
            return "user_past_orders_empty";
        }
        return "user_past_orders";
    }

    @GetMapping("/user/checkout")
    public String checkoutBasket () throws IOException {
        if(currentUser.getRole() == null) {
            return "redirect:" + UNAUTHORIZED_URL;
        }
        status = orderHttp.placeOrder(currentUser.getToken());
        if (status == 200) {
            return "redirect:/user";
        }
        else {
            return "redirect:" + ERROR_URL;
        }
    }
}
