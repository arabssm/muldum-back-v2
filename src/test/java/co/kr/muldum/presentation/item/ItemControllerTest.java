package co.kr.muldum.presentation.item;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.dto.TempItemResponseDto;
import co.kr.muldum.domain.item.dto.ItemListResponseDto;
import co.kr.muldum.domain.item.service.ItemRequestService;
import co.kr.muldum.domain.item.service.ItemListService;
import co.kr.muldum.domain.item.service.ItemRequestFinalizer;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.Student;
import co.kr.muldum.domain.user.model.UserInfo;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@WebMvcTest(ItemController.class)
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
                .status(ItemStatus.INTEMP.name())
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
                .andExpect(jsonPath("$.status").value(ItemStatus.INTEMP.name()))
                .andExpect(jsonPath("$.message").value("임시 신청이 완료되었습니다."));
    }

    @Test
    @DisplayName("팀 물품 목록 조회 성공 테스트")
    void testGetTeamItems_Success() throws Exception {
        // Given
        Long userId = 123L;
        Long teamId = 1L;
        CustomUserDetails userDetails = new CustomUserDetails(userId, "student");
        
        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .teamId(teamId)
                .build();
                
        List<ItemListResponseDto> mockItems = Arrays.asList(
                ItemListResponseDto.builder()
                        .id(3L)
                        .product_name("아두이노")
                        .quantity(5)
                        .price(12000)
                        .status("PENDING")
                        .type("network")
                        .build(),
                ItemListResponseDto.builder()
                        .id(4L)
                        .product_name("센서 키트")
                        .quantity(2)
                        .price(30000)
                        .status("APPROVED")
                        .type("network")
                        .build()
        );

        when(userReader.read(Student.class, userId)).thenReturn(userInfo);
        when(itemListService.getTeamItemRequests(userInfo)).thenReturn(mockItems);

        // When & Then
        mockMvc.perform(get("/std/items")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(3))
                .andExpect(jsonPath("$[0].product_name").value("아두이노"))
                .andExpect(jsonPath("$[0].quantity").value(5))
                .andExpect(jsonPath("$[0].price").value(12000))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[0].type").value("network"))
                .andExpect(jsonPath("$[1].id").value(4))
                .andExpect(jsonPath("$[1].product_name").value("센서 키트"))
                .andExpect(jsonPath("$[1].quantity").value(2))
                .andExpect(jsonPath("$[1].price").value(30000))
                .andExpect(jsonPath("$[1].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].type").value("network"));
    }

    @Test
    @DisplayName("팀 물품 목록 조회 - 빈 목록 테스트")
    void testGetTeamItems_EmptyList() throws Exception {
        // Given
        Long userId = 123L;
        Long teamId = 1L;
        CustomUserDetails userDetails = new CustomUserDetails(userId, "student");
        
        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .teamId(teamId)
                .build();

        when(userReader.read(Student.class, userId)).thenReturn(userInfo);
        when(itemListService.getTeamItemRequests(userInfo)).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/std/items")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("최종 물품 신청 성공 테스트")
    void testFinalizeItemRequest_Success() throws Exception {
        // Given
        Long userId = 123L;
        Long teamId = 1L;
        CustomUserDetails userDetails = new CustomUserDetails(userId, "student");
        
        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .teamId(teamId)
                .build();
                
        ItemRequestFinalizer.FinalizeResult result = 
                ItemRequestFinalizer.FinalizeResult.of(ItemStatus.PENDING, "총 2개 물품이 성공적으로 신청되었습니다.");

        when(userReader.read(Student.class, userId)).thenReturn(userInfo);
        when(itemRequestFinalizer.finalizeRequest(userInfo)).thenReturn(result);

        // When & Then
        mockMvc.perform(patch("/std/items")
                        .with(user(userDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.message").value("총 2개 물품이 성공적으로 신청되었습니다."));
    }

    @Test
    @DisplayName("최종 물품 신청 실패 테스트 - 임시 신청된 물품이 없음")
    void testFinalizeItemRequest_NoTempItems() throws Exception {
        // Given
        Long userId = 123L;
        Long teamId = 1L;
        CustomUserDetails userDetails = new CustomUserDetails(userId, "student");
        
        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .teamId(teamId)
                .build();
                
        ItemRequestFinalizer.FinalizeResult result = 
                ItemRequestFinalizer.FinalizeResult.of(ItemStatus.REJECTED, "임시 신청된 물품이 없습니다.");

        when(userReader.read(Student.class, userId)).thenReturn(userInfo);
        when(itemRequestFinalizer.finalizeRequest(userInfo)).thenReturn(result);

        // When & Then
        mockMvc.perform(patch("/std/items")
                        .with(user(userDetails)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value("REJECTED"))
                .andExpect(jsonPath("$.message").value("임시 신청된 물품이 없습니다."));
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