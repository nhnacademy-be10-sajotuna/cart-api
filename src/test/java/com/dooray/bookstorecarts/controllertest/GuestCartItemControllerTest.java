package com.dooray.bookstorecarts.controllertest;

import com.dooray.bookstorecarts.controller.GuestCartItemController;
import com.dooray.bookstorecarts.request.CartItemRequest;
import com.dooray.bookstorecarts.service.GuestCartItemService;
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
@WebMvcTest(controllers = GuestCartItemController.class)
public class GuestCartItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GuestCartItemService guestCartItemService;

    @Test
    public void addGuestCartItem() throws Exception {
        // given
        String cartId = "guestCartId";

        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setCartItemId(1L);
        cartItemRequest.setIsbn("isbn");
        cartItemRequest.setQuantity(5L);

        willDoNothing().given(guestCartItemService).addGuestCartItem(cartId, cartItemRequest);
        // when then
        mockMvc.perform(
                post("/api/guest-cart-items")
                        .header("X-Guest-Cart-Id", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartItemRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    public void updateGuestCartItem() throws Exception {
        // given
        String cartId = "guestCartId";

        CartItemRequest cartItemRequest = new CartItemRequest();
        cartItemRequest.setCartItemId(1L);
        cartItemRequest.setIsbn("isbn");
        cartItemRequest.setQuantity(5L);

        willDoNothing().given(guestCartItemService).updateQuantity(cartId, cartItemRequest);
        // when then
        mockMvc.perform(
                post("/api/guest-cart-items/update")
                        .header("X-Guest-Cart-Id", cartId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(cartItemRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteGuestCartItem() throws Exception {
        // given
        String cartId = "guestCartId";
        String isbn = "isbn";

        willDoNothing().given(guestCartItemService).deleteGuestCartItem(cartId, isbn);
        // when then
        mockMvc.perform(
                delete("/api/guest-cart-items/{isbn}", isbn)
                        .header("X-Guest-Cart-Id", cartId))
                .andExpect(status().isNoContent());
    }

    @Test
    public void clearGuestCartItems() throws Exception {
        String cartId = "guestCartId";

        willDoNothing().given(guestCartItemService).deleteAllGuestCartItems(cartId);
        // when then
        mockMvc.perform(
                delete("/api/guest-cart-items")
                        .header("X-Guest-Cart-Id", cartId))
                .andExpect(status().isNoContent());
    }
}
