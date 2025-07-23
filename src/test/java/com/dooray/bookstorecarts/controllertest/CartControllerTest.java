package com.dooray.bookstorecarts.controllertest;

import com.dooray.bookstorecarts.controller.CartController;
import com.dooray.bookstorecarts.response.CartResponse;
import com.dooray.bookstorecarts.service.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(CartController.class)
public class CartControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @Test
    public void mergeCarts() throws Exception {
        // given
        Long userId = 1L;
        String guestCartId = "guestCartId";

        CartResponse cartResponse = new CartResponse();
        given(cartService.mergeCarts(userId, guestCartId)).willReturn(cartResponse);

        // when then
        mockMvc.perform(
                post("/api/carts/merge")
                        .header("X-User-Id", userId)
                        .header("X-Guest-Cart-Id", guestCartId))
                        .andExpect(status().isOk());
    }
}
