package co.kr.muldum.presentation.user;

import static org.mockito.Mockito.doNothing;

import co.kr.muldum.application.user.LogoutService;
import co.kr.muldum.domain.user.model.UserType;
import co.kr.muldum.presentation.dto.LogoutRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {LogoutController.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class LogoutControllerDiffblueTest {
    @Autowired
    private LogoutController logoutController;

    @MockitoBean
    private LogoutService logoutService;

    /**
     * Test {@link LogoutController#logout(LogoutRequestDto)}.
     *
     * <p>Method under test: {@link LogoutController#logout(LogoutRequestDto)}
     */
    @Test
    @DisplayName("Test logout(LogoutRequestDto)")
    void testLogout() throws Exception {
        // Arrange
        doNothing().when(logoutService).logout(Mockito.<LogoutRequestDto>any());
        MockHttpServletRequestBuilder contentTypeResult =
                MockMvcRequestBuilders.post("/ara/auth/logout").contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        MockHttpServletRequestBuilder requestBuilder =
                contentTypeResult.content(
                        objectMapper.writeValueAsString(new LogoutRequestDto("ABC123", UserType.STUDENT, 1L)));

        // Act and Assert
        MockMvcBuilders.standaloneSetup(logoutController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("{\"message\":\"로그아웃 되었습니다.\"}"));
    }
}
