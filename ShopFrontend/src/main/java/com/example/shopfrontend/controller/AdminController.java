package com.example.shopfrontend.controller;

import com.example.shopfrontend.http.OrderHttp;
import com.example.shopfrontend.http.ProductHttp;
import com.example.shopfrontend.models.dto.OrderDetailsDTO;

import com.example.shopfrontend.models.dto.ProductDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static com.example.shopfrontend.controller.IndexController.currentUser;

/** This class is responsible for handling requests from the admin user.
 * It checks if the user is logged in and if the user has the admin role before
 * allowing the user to render the admin pages.
 * it uses a Logger to log the requests and responses to the console.
 */
@Slf4j
@Controller
public class AdminController {

    private final ProductHttp productHttp;
    private final OrderHttp orderHttp;

    // the following variables are used to redirect the user to the appropriate page
    private final String ADMIN_URL = "/admin";
    private final String UNAUTHORIZED_URL = "/unauthorized";
    private final String ERROR_URL = "/error";
    private final String ADMIN_ROLE = "ROLE_ADMIN";
    int status = 0;


    public AdminController(ProductHttp productHttp, OrderHttp orderHttp) {
        this.productHttp = productHttp;
        this.orderHttp = orderHttp;
    }

    // This method is responsible for rendering the admin_index page.
    // it checks if the user is logged in and if the user has the admin role before rendering the page.
    // if the product list is null or not fetched correctly, the user is redirected to the error page.
    // if all is correct so far, the user is directed to the admin_index page and the product list is displayed,
    // along with the username of the logged in user.
    @GetMapping("/admin")
    public String adminIndex(Model model) throws IOException, ParseException {
        if(currentUser.getRole() == null || !currentUser.getRole().contains(ADMIN_ROLE)) {
            log.info("not authorized");
            return UNAUTHORIZED_URL;
        }
        List<ProductDTO> products = productHttp.getAllProducts();
        if(products == null) {
            log.info("products not found");
            return "redirect:" + ERROR_URL;
        } else {
            model.addAttribute("products", products);
            model.addAttribute("username", IndexController.currentUser.getUsername());
            return "admin_index";
        }
    }

    // This method is responsible for rendering the admin_create_product page.
    // it checks if the user is logged in and if the user has the admin role before rendering the page.
    // if all is correct so far, an empty product object is created and the user is directed to the admin_create_product page.
    @GetMapping ("/admin/create_product")
    public String createProductForm(Model model) {
        if(currentUser.getRole() == null || !currentUser.getRole().contains(ADMIN_ROLE)) {
            log.info("Not authorized");
            return "redirect:" + UNAUTHORIZED_URL;
        }
        ProductDTO product = new ProductDTO();
        model.addAttribute("product", product);
        return "create_product";
    }

    // This method is responsible for posting the product object to the backend.
    // it checks if the user is logged in and if the user has the admin role before posting the product object.
    // if the product is posted successfully, the user is redirected to the admin_index page.
    // if the product is not posted successfully or the user is not logged in or does not have the admin role,
    // the user is redirected to appropriate page.

    @PostMapping("/admin/create_product")
    public String saveProduct(ProductDTO product) throws IOException {
        if(currentUser.getRole() == null || !currentUser.getRole().contains(ADMIN_ROLE)) {
            return "redirect:" + UNAUTHORIZED_URL;
        }
        status = productHttp.createProduct(product,IndexController.currentUser.getToken());
        if (status == 200) {
            log.info("saveProduct: " + product);
            return "redirect:" + ADMIN_URL;
        }
        if (status == 401 || status == 403) {
            log.info("not authorized");
            return "redirect:" + UNAUTHORIZED_URL;
        }
        else {
            log.info("error");
            return "redirect:" + ERROR_URL;
        }
    }

    // This method is responsible for rendering the admin_edit_product page.
    // it checks if the user is logged in and if the user has the admin role before rendering the page.
    // if the product is null or not fetched correctly, the user is redirected to the error page.
    // if all is correct so far, the user is directed to the admin_edit_product page and the product object is displayed.
    @GetMapping("/admin/edit_product/{id}")
    public String updateProductForm(@PathVariable long id ,Model model) throws IOException, ParseException {
        if(currentUser.getRole() == null || !currentUser.getRole().contains(ADMIN_ROLE)) {
            log.info("not authorized");
            return "redirect:" + UNAUTHORIZED_URL;
        }
        ProductDTO product = productHttp.getProductById(id);
        if(product == null) {
            log.info("product not found");
            return "redirect:" + ERROR_URL;
        } else {
            model.addAttribute("product", product);
            return "update_product";
        }
    }

    // This method is responsible for updating the product object in the backend.
    // it checks if the user is logged in and if the user has the admin role before updating the product object.
    // if the product is updated successfully, the user is redirected to the admin_index page.
    // if the product is not updated successfully or the user is not logged in or does not have the admin role,
    // the user is redirected to appropriate page.
    @PostMapping ("/admin/edit_product/{id}")
    public String updateProduct(@PathVariable long id , @ModelAttribute ProductDTO product) throws IOException, ParseException {
        if(currentUser.getRole() == null || !currentUser.getRole().contains(ADMIN_ROLE)) {
            log.info("not authorized");
            return "redirect:" + UNAUTHORIZED_URL;
        }
        ProductDTO productToUpdate = productHttp.getProductById(id);
        if (productToUpdate == null) {
            log.info("product not found");
            return "redirect:" + ERROR_URL;
        }
        productToUpdate.setId(product.getId());
        productToUpdate.setName(product.getName());
        productToUpdate.setPrice(product.getPrice());
        productToUpdate.setDescription(product.getDescription());
        status = productHttp.updateProduct(productToUpdate,IndexController.currentUser.getToken());
        if (status == 200) {
            log.info("updateProduct: " + productToUpdate);
            return "redirect:" + ADMIN_URL;
        } else {
            log.info("error");
            return "redirect:" + ERROR_URL;
        }
    }

    // This method is responsible for deleting the product object in the backend.
    // it checks if the user is logged in and if the user has the admin role before deleting the product object.
    // if the product is deleted successfully, the user is redirected to the admin_index page.
    // if the product is not deleted successfully or the user is not logged in or does not have the admin role,
    // the user is redirected to appropriate page.
    @GetMapping("/admin/delete_product/{id}")
    public String deleteProductForm(@PathVariable long id) throws IOException {
        if(currentUser.getRole() == null || !currentUser.getRole().contains(ADMIN_ROLE)) {
            log.info("not authorized");
            return "redirect:" + UNAUTHORIZED_URL;
        }
        ProductDTO productToDelete = new ProductDTO();
        productToDelete.setId(id);
        status = productHttp.deleteProductById(productToDelete,IndexController.currentUser.getToken());
        if (status == 204) {
            log.info("deleteProduct: " + productToDelete);
            return "redirect:" + ADMIN_URL;
        }
        if (status == 401 || status == 403) {
            log.info("not authorized");
            return "redirect:" + UNAUTHORIZED_URL;
        }
        else {
            log.info("error");
            return "redirect:" + ERROR_URL;
        }
    }

    // This method is responsible for rendering the all_orders page.
    // it checks if the user is logged in and if the user has the admin role before rendering the page.
    // if the order list is null or not fetched correctly, the user is redirected to the error page.
    // if all is correct so far, the user is directed to the all_orders page and the order list is displayed,
    // along with the username of the logged in user.
    @GetMapping("/admin/all_orders")
    public String getAllOrders(Model model) throws IOException, ParseException {
        if(currentUser.getRole() == null || !currentUser.getRole().contains(ADMIN_ROLE)) {
            log.info("not authorized");
            return "redirect:" + UNAUTHORIZED_URL;
        }
        OrderDetailsDTO orders = orderHttp.getAllOrdersForAll(IndexController.currentUser.getToken());
        if(orders == null) {
            log.info("orders not found");
            return "redirect:" + ERROR_URL;
        } else {
            model.addAttribute("pastOrders", orders);
            model.addAttribute("username", IndexController.currentUser.getUsername());
            return "all_orders";
        }
    }
}
