package com.dooray.bookstorecarts.controllertest;

import com.dooray.bookstorecarts.controller.GuestCartController;
import com.dooray.bookstorecarts.controller.UserCartItemController;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.service.UserCartItemService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = UserCartItemController.class)
public class UserCartItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserCartItemService userCartItemService;

    @Test
    public void addUserCartItem() throws Exception {
        // given
        Long userId = 33L;
        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setCartItemId(5L);
        cartItemRequest.setIsbn("isbn");
        cartItemRequest.setQuantity(3L);

        willDoNothing().given(userCartItemService).addUserCartItem(userId, cartItemRequest);
        // when then
        mockMvc.perform(
                post("/api/user-cart-items")
                        .header("X-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartItemRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    public void updateUserCartItem() throws Exception {
        // given
        Long cartItemId = 5L;
        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setCartItemId(5L);
        cartItemRequest.setIsbn("isbn");
        cartItemRequest.setQuantity(3L);

        willDoNothing().given(userCartItemService).updateQuantity(cartItemId, cartItemRequest);
        // when then
        mockMvc.perform(
                post("/api/user-cart-items/update/{cartItemId}", cartItemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartItemRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUserCartItem() throws Exception {
        // given
        Long cartItemId = 5L;

        willDoNothing().given(userCartItemService).deleteCartItem(cartItemId);
        // when then
        mockMvc.perform(
                delete("/api/user-cart-items/{cartItemId}", cartItemId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void clearUserCartItems() throws Exception {
        // given
        Long userId = 33L;

        willDoNothing().given(userCartItemService).deleteAllCartItemsFromUserId(userId);
        // when then
        mockMvc.perform(
                delete("/api/user-cart-items")
                .header("X-User-Id", userId))
                .andExpect(status().isNoContent());
    }
}
