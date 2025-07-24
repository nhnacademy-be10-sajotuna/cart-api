package com.dooray.bookstorecarts.controllertest;

import com.dooray.bookstorecarts.controller.UserCartController;
import com.dooray.bookstorecarts.response.CartResponse;
import com.dooray.bookstorecarts.service.UserCartService;
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
@WebMvcTest(controllers = UserCartController.class)
public class UserCartControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserCartService userCartService;

    @Test
    public void getUserCart() throws Exception {
        // given
        Long userId = 33L;

        CartResponse cartResponse = new CartResponse();
        given(userCartService.getCartByUserId(userId)).willReturn(cartResponse);

        // when then
        mockMvc.perform(
                get("/api/user-carts")
                        .header("X-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteUserCart() throws Exception {
        // given
        Long userId = 33L;

        willDoNothing().given(userCartService).deleteUserCart(userId);

        // when then
        mockMvc.perform(
                delete("/api/user-carts")
                        .header("X-User-Id", userId))
                .andExpect(status().isNoContent());
    }
}
