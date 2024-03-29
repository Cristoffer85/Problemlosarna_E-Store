package com.example.shopbackend.controller;

import com.example.shopbackend.entity.OrderQty;
import com.example.shopbackend.form.UpdateBasketDTO;
import com.example.shopbackend.model.BasketDTO;
import com.example.shopbackend.security.ExtractData;
import com.example.shopbackend.service.BasketService;
import com.example.shopbackend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/webshop/basket")
public class BasketController {

    private final BasketService basketService;
    private final UserService userService;

    public BasketController(BasketService basketService, UserService userService, ExtractData extractData) {
        this.basketService = basketService;
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<BasketDTO> getBasket(Principal principal) {

        Long userid = userService.getUserId(principal);
        BasketDTO basket = null;

        if (userid != null) basket = basketService.getBasket(userid);

        return basket != null ? ResponseEntity.ok(basket) : ResponseEntity.notFound().build();
    }


    @PostMapping("")
    public ResponseEntity<?> addProductToBasket(@RequestBody UpdateBasketDTO payload, Principal principal) {

        Long userid = userService.getUserId(principal);
        OrderQty result = null;

        if (userid != null) result = basketService.addProduct(userid, payload);

        return result != null ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PutMapping("")
    public ResponseEntity<?> updateQuantity(@RequestBody UpdateBasketDTO payload, Principal principal) {

        Long userid = userService.getUserId(principal);
        OrderQty result = null;

        if (userid != null) result = basketService.updateQuantityProduct(userid, payload);

        return result != null ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @DeleteMapping("")
    public ResponseEntity<?> deleteProduct(@RequestBody UpdateBasketDTO payload, Principal principal) {

        Long userid = userService.getUserId(principal);

        return basketService.deleteProductActiveBasket(userid, payload) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
