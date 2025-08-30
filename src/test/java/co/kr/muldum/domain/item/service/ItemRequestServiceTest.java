package co.kr.muldum.domain.item.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.muldum.domain.item.dto.TempItemRequestDto;
import co.kr.muldum.domain.item.dto.ItemResponseDto;
import co.kr.muldum.domain.item.model.ItemRequest;
import co.kr.muldum.domain.item.model.enums.ItemSource;
import co.kr.muldum.domain.item.model.enums.ItemStatus;
import co.kr.muldum.domain.item.repository.ItemRequestRepository;
import co.kr.muldum.domain.user.UserReader;
import co.kr.muldum.domain.user.model.Student;
import co.kr.muldum.domain.user.model.UserInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserReader userReader;

    @InjectMocks
    private ItemRequestService itemRequestService;

    @Test
    @DisplayName("디바이스마트 링크로 임시 물품 신청 생성 성공 테스트")
    void testCreateTempItemRequest_Success_Devicemart() {
        // Given
        Long userId = 123L;
        Integer teamId = 1;
        TempItemRequestDto requestDto = new TempItemRequestDto(
                "Arduino UNO",
                2,
                "25000",
                "https://www.devicemart.co.kr/goods/view?no=1234",
                "아두이노 프로젝트용"
        );

        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .teamId(teamId.longValue())
                .build();

        ItemRequest savedItemRequest = ItemRequest.builder()
                .id(1L)
                .teamId(teamId)
                .requesterUserId(userId.intValue())
                .status(ItemStatus.INTEMP)
                .build();

        when(userReader.read(Student.class, userId)).thenReturn(userInfo);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedItemRequest);

        // When
        ItemResponseDto result = itemRequestService.createTempItemRequest(requestDto, userId);

        // Then
        assertThat(result.getStatus()).isEqualTo("INTEMP");
        assertThat(result.getMessage()).isEqualTo("임시 신청이 완료되었습니다.");

        ArgumentCaptor<ItemRequest> itemRequestCaptor = ArgumentCaptor.forClass(ItemRequest.class);
        verify(itemRequestRepository).save(itemRequestCaptor.capture());

        ItemRequest capturedItemRequest = itemRequestCaptor.getValue();
        assertThat(capturedItemRequest.getTeamId()).isEqualTo(teamId);
        assertThat(capturedItemRequest.getRequesterUserId()).isEqualTo(userId.intValue());
        assertThat(capturedItemRequest.getStatus()).isEqualTo(ItemStatus.INTEMP);
        assertThat(capturedItemRequest.getProductInfo().getName()).isEqualTo("Arduino UNO");
        assertThat(capturedItemRequest.getProductInfo().getQuantity()).isEqualTo(2);
        assertThat(capturedItemRequest.getProductInfo().getPrice()).isEqualTo("25000");
        assertThat(capturedItemRequest.getProductInfo().getLink()).isEqualTo("https://www.devicemart.co.kr/goods/view?no=1234");
        assertThat(capturedItemRequest.getProductInfo().getItemSource()).isEqualTo(ItemSource.DEVICEMART);
        assertThat(capturedItemRequest.getRequestDetails().getReason()).isEqualTo("아두이노 프로젝트용");
    }

    @Test
    @DisplayName("11번가 링크로 임시 물품 신청 생성 성공 테스트")
    void testCreateTempItemRequest_Success_ElevenMarket() {
        // Given
        Long userId = 123L;
        Integer teamId = 1;
        TempItemRequestDto requestDto = new TempItemRequestDto(
                "노트북 케이스",
                1,
                "15000",
                "https://11st.co.kr/products/1234567",
                "노트북 보호용"
        );

        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .teamId(teamId.longValue())
                .build();

        ItemRequest savedItemRequest = ItemRequest.builder()
                .id(1L)
                .teamId(teamId)
                .requesterUserId(userId.intValue())
                .status(ItemStatus.INTEMP)
                .build();

        when(userReader.read(Student.class, userId)).thenReturn(userInfo);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedItemRequest);

        // When
        ItemResponseDto result = itemRequestService.createTempItemRequest(requestDto, userId);

        // Then
        assertThat(result.getStatus()).isEqualTo("INTEMP");
        assertThat(result.getMessage()).isEqualTo("임시 신청이 완료되었습니다.");

        ArgumentCaptor<ItemRequest> itemRequestCaptor = ArgumentCaptor.forClass(ItemRequest.class);
        verify(itemRequestRepository).save(itemRequestCaptor.capture());

        ItemRequest capturedItemRequest = itemRequestCaptor.getValue();
        assertThat(capturedItemRequest.getProductInfo().getItemSource()).isEqualTo(ItemSource.ELEVENMARKET);
        assertThat(capturedItemRequest.getStatus()).isEqualTo(ItemStatus.INTEMP);
    }

    @Test
    @DisplayName("허용되지 않은 쇼핑몰 링크로 신청 시 REJECTED 상태 테스트")
    void testCreateTempItemRequest_Rejected_UnallowedSite() {
        // Given
        Long userId = 123L;
        Integer teamId = 1;
        TempItemRequestDto requestDto = new TempItemRequestDto(
                "상상소나무",
                3,
                "32000",
                "https://coupang.com/padark",
                "umm"
        );

        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .teamId(teamId.longValue())
                .build();

        ItemRequest savedItemRequest = ItemRequest.builder()
                .id(1L)
                .teamId(teamId)
                .requesterUserId(userId.intValue())
                .status(ItemStatus.REJECTED)
                .build();

        when(userReader.read(Student.class, userId)).thenReturn(userInfo);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(savedItemRequest);

        // When
        ItemResponseDto result = itemRequestService.createTempItemRequest(requestDto, userId);

        // Then
        assertThat(result.getStatus()).isEqualTo("REJECTED");
        assertThat(result.getMessage()).isEqualTo("허용되지 않은 쇼핑몰입니다. 디바이스마트(devicemart.co.kr) 또는 11번가(11st.co.kr)에서만 신청 가능합니다.");

        ArgumentCaptor<ItemRequest> itemRequestCaptor = ArgumentCaptor.forClass(ItemRequest.class);
        verify(itemRequestRepository).save(itemRequestCaptor.capture());

        ItemRequest capturedItemRequest = itemRequestCaptor.getValue();
        assertThat(capturedItemRequest.getProductInfo().getItemSource()).isEqualTo(ItemSource.OTHER);
        assertThat(capturedItemRequest.getStatus()).isEqualTo(ItemStatus.REJECTED);
    }

    @Test
    @DisplayName("팀ID가 null인 사용자의 물품 신청 시 REJECTED 상태 테스트")
    void testCreateTempItemRequest_Rejected_NullTeamId() {
        // Given
        Long userId = 123L;
        TempItemRequestDto requestDto = new TempItemRequestDto(
                "Arduino UNO",
                2,
                "25000",
                "https://www.devicemart.co.kr/goods/view?no=1234",
                "아두이노 프로젝트용"
        );

        UserInfo userInfo = UserInfo.builder()
                .userId(userId)
                .teamId(null)  // teamId가 null
                .build();

        when(userReader.read(Student.class, userId)).thenReturn(userInfo);

        // When
        ItemResponseDto result = itemRequestService.createTempItemRequest(requestDto, userId);

        // Then
        assertThat(result.getStatus()).isEqualTo("REJECTED");
        assertThat(result.getMessage()).isEqualTo("팀 정보가 없습니다. 팀에 소속되어야 물품을 신청할 수 있습니다.");

        // itemRequestRepository.save()가 호출되지 않았는지 확인
        verify(itemRequestRepository, org.mockito.Mockito.never()).save(any(ItemRequest.class));
    }
}