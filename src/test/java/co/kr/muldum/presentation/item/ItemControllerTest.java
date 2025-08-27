package co.kr.muldum.presentation.item;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.dto.TempItemResponseDto;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.service.ItemRequestService;
import co.kr.muldum.global.exception.GlobalExceptionHandler;
import co.kr.muldum.global.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

@ContextConfiguration(classes = {ItemController.class, GlobalExceptionHandler.class})
@ExtendWith(SpringExtension.class)
class ItemControllerTest {

    @MockitoBean
    private ItemRequestService itemRequestService;


    @Autowired
    private ItemController itemController;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    @DisplayName("임시 물품 신청 성공 테스트")
    void testCreateTempItemRequest_Success() throws Exception {
        // Given
        Long userId = 123L;
        TempItemRequestDto requestDto = new TempItemRequestDto(
                "상상소나무",
                3,
                "32000",
                "https://coupang.com/padark",
                ItemSource.COUPANG,
                "umm"
        );

        TempItemResponseDto responseDto = TempItemResponseDto.builder()
                .status("INTEMP")
                .message("임시 신청이 완료되었습니다.")
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(userId, "student");

        when(itemRequestService.createTempItemRequest(requestDto, userId))
                .thenReturn(responseDto);

        ObjectMapper objectMapper = new ObjectMapper();
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(globalExceptionHandler)
                .build();

        // When & Then
        mockMvc.perform(post("/std/items/temp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .principal(new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("INTEMP"))
                .andExpect(jsonPath("$.message").value("임시 신청이 완료되었습니다."));
    }

    @Test
    @DisplayName("잘못된 요청 데이터로 400 에러 테스트")
    void testCreateTempItemRequest_BadRequest() throws Exception {
        // Given
        String invalidJson = "{ \"invalidField\": \"value\" }";

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(itemController)
                .setControllerAdvice(globalExceptionHandler)
                .build();

        // When & Then
        mockMvc.perform(post("/std/items/temp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}