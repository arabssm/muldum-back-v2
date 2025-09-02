package co.kr.muldum.presentation.item;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.kr.muldum.domain.item.dto.ItemResponseDto;
import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.dto.TempItemListResponseDto;
import co.kr.muldum.domain.item.service.ItemListService;
import co.kr.muldum.domain.item.service.ItemRequestFinalizer;
import co.kr.muldum.domain.item.service.ItemRequestService;

import java.util.Arrays;
import java.util.List;

import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.presentation.item.StudentItemController;
import co.kr.muldum.global.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

@WebMvcTest(StudentItemController.class)
class ItemControllerTest {

    @MockitoBean
    private ItemRequestService itemRequestService;

    @MockitoBean
    private ItemListService itemListService;

    @MockitoBean
    private ItemRequestFinalizer itemRequestFinalizer;

    @MockitoBean
    private UserReader userReader;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("임시 물품 신청 성공 테스트")
    void testCreateTempItemRequest_Success() throws Exception {
        // Given
        Long userId = 123L;
        TempItemRequestDto requestDto = new TempItemRequestDto(
                "상상소나무",
                3,
                "32000",
                "https://devicemart.co.kr/test",
                "umm"
        );

        ItemResponseDto responseDto = ItemResponseDto.builder()
                .status("INTEMP")
                .message("임시 신청이 완료되었습니다.")
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(userId, "student");

        when(itemRequestService.createTempItemRequest(requestDto, userId))
                .thenReturn(responseDto);

        ObjectMapper objectMapper = new ObjectMapper();

        // When & Then
        mockMvc.perform(post("/std/items/temp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("INTEMP"))
                .andExpect(jsonPath("$.message").value("임시 신청이 완료되었습니다."));
    }

    @Test
    @DisplayName("임시 물품 목록 조회 성공 테스트")
    void testGetTempItemRequests_Success() throws Exception {
        // Given
        Long userId = 123L;
        List<TempItemListResponseDto> responseList = Arrays.asList(
                TempItemListResponseDto.builder()
                        .id(1L)
                        .product_name("상상소나무")
                        .quantity(3)
                        .price("32000")
                        .status("INTEMP")
                        .type("network")
                        .build(),
                TempItemListResponseDto.builder()
                        .id(2L)
                        .product_name("테스트 제품")
                        .quantity(1)
                        .price("15000")
                        .status("INTEMP")
                        .type("network")
                        .build()
        );

        CustomUserDetails userDetails = new CustomUserDetails(userId, "student");

        when(itemRequestService.getTempItemRequests(userId))
                .thenReturn(responseList);

        // When & Then
        mockMvc.perform(get("/std/items/temp")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].productName").value("상상소나무"))
                .andExpect(jsonPath("$[0].quantity").value(3))
                .andExpect(jsonPath("$[0].price").value("32000"))
                .andExpect(jsonPath("$[0].status").value("INTEMP"))
                .andExpect(jsonPath("$[0].type").value("network"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].productName").value("테스트 제품"));
    }

    @Test
    @DisplayName("인증 없이 접근 시 401 에러 테스트")
    @WithMockUser
    void testCreateTempItemRequest_Unauthorized() throws Exception {
        // Given
        String validJson = "{ \"productName\": \"테스트\", \"quantity\": 1, \"price\": \"1000\", \"productLink\": \"https://devicemart.co.kr/test\", \"reason\": \"테스트\" }";

        // When & Then
        mockMvc.perform(post("/std/items/temp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isUnauthorized());
    }
}