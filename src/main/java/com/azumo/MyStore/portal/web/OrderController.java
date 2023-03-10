package com.azumo.MyStore.portal.web;

import com.azumo.MyStore.portal.PlaceOrderFromCart;
import com.azumo.MyStore.portal.PrepareOrderDelivery;
import com.azumo.MyStore.sales.cart.Cart;
import com.azumo.MyStore.sales.cart.RetrieveCart;
import com.azumo.MyStore.shipping.delivery.Address;
import com.azumo.MyStore.shipping.delivery.Person;
import com.azumo.MyStore.shipping.delivery.Place;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * Web controller for Order use-cases.
 */
@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
class OrderController {

    private final @NonNull RetrieveCart retrieveCart;
    private final @NonNull PlaceOrderFromCart placeOrderFromCart;
    private final @NonNull PrepareOrderDelivery prepareOrderDelivery;

    @GetMapping
    public String index() {
        return "order";
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String place(@NonNull String name, @NonNull String address,
                        HttpServletRequest request, HttpServletResponse response) {
        Cart cart = retrieveCart.byId(new CartIdFromCookies(request, response).cartId());
        // Order ID is generated by portal immediately when the purchase process begins.
        // This makes it possible to Prepare Delivery before an Order is placed.
        UUID orderId = UUID.randomUUID();

        // Neither order nor synchronous manner is not necessary here.
        // Place Order and Prepare Delivery are meant to be called from service-oriented widgets.
        placeOrderFromCart.placeOrder(orderId, cart);

        prepareOrderDelivery.prepareDelivery(
                orderId,
                new Address(new Person(name), new Place(address)));

        cart.empty();

        return "redirect:/order/success";
    }

    @GetMapping("/success")
    public String success(HttpServletRequest request, HttpServletResponse response) {
        return "order-success";
    }

    @GetMapping("/error")
    public String error(String message, Model model) {
        model.addAttribute("messageCode", message);
        return "order-error";
    }

    @ExceptionHandler({PlaceOrderFromCart.NoItemsToOrderException.class, IllegalArgumentException.class})
    String exception(Exception ex) {
        return "redirect:/order/error?message=" + errorCode(ex);
    }

    private String errorCode(Exception e) {
        if (e instanceof PlaceOrderFromCart.NoItemsToOrderException) {
            return "noitems";
        }
        if (e instanceof IllegalArgumentException) {
            return "requires";
        }
        return "default";
    }
}
