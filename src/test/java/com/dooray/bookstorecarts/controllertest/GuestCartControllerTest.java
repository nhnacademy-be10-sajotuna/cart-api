package com.dooray.bookstorecarts.controllertest;

import com.dooray.bookstorecarts.controller.GuestCartController;
import com.dooray.bookstorecarts.response.CartResponse;
import com.dooray.bookstorecarts.service.GuestCartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@WebMvcTest(controllers = GuestCartController.class)
public class GuestCartControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    private GuestCartService guestCartService;

    @Test
    public void getGuestCart() throws Exception {
        // given
        String cartId = "guestCartId";

        CartResponse cartResponse = new CartResponse();
        given(guestCartService.getCartByCartId(cartId)).willReturn(cartResponse);

        // when then
        mockMvc.perform(
                get("/api/guest-carts")
                        .header("X-Guest-Cart-Id", cartId))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteGuestCart() throws Exception {
        // given
        String cartId = "guestCartId";

        willDoNothing().given(guestCartService).deleteGuestCart(cartId);

        // when then
        mockMvc.perform(
                delete("/api/guest-carts")
                        .header("X-Guest-Cart-Id", cartId))
                .andExpect(status().isNoContent());
    }
}
