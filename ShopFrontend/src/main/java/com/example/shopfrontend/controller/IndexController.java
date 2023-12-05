package com.example.shopfrontend.controller;

import com.example.shopfrontend.http.ProductHttp;
import com.example.shopfrontend.http.UserHttp;
import com.example.shopfrontend.models.LoginForm;
import com.example.shopfrontend.models.RegistrationForm;
import com.example.shopfrontend.models.User;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;

@Controller
@AllArgsConstructor

public class IndexController {

    private ProductHttp productHttp;
    private  UserHttp userHttp;

    @GetMapping("/index")
    public String listProducts(Model model) throws IOException, ParseException {

        model.addAttribute("products", productHttp.getAllProducts());
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        LoginForm loginform = new LoginForm();
        model.addAttribute("loginform", loginform);
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(User user) {
        User userToLogin = userHttp.loginUser(user);
        if (user == null) { //if the is no user
            return "redirect:/registration";
        } else {
            if (user.getRoles() = "ADMIN") {  // not correct but you get the idea
                return "redirect:/admin_index";
            } else {
                return "redirect:/user_index";
            }
        }
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        RegistrationForm registrationForm = new RegistrationForm();
        model.addAttribute("registrationForm", registrationForm);
        return "registration";
    }

    @PostMapping("/index")
    public String registerUser(User user) {
        userHttp.registerUser(user);
        return "redirect:/login";
    }
}
