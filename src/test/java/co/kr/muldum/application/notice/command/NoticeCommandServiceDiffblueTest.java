package co.kr.muldum.application.notice.command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import co.kr.muldum.domain.file.repository.FileBookRepository;
import co.kr.muldum.domain.file.repository.FileRepository;
import co.kr.muldum.domain.notice.factory.NoticeRequestFactory;
import co.kr.muldum.domain.notice.repository.NoticeRepository;
import co.kr.muldum.domain.notice.repository.NoticeTeamRepository;
import co.kr.muldum.domain.teamspace.repository.TeamRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {NoticeCommandService.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class NoticeCommandServiceDiffblueTest {
  @MockitoBean
  private FileBookRepository fileBookRepository;

  @MockitoBean
  private FileRepository fileRepository;

  @Autowired
  private NoticeCommandService noticeCommandService;

  @MockitoBean
  private NoticeRepository noticeRepository;

  @MockitoBean
  private NoticeRequestFactory noticeRequestFactory;

  @MockitoBean
  private NoticeTeamRepository noticeTeamRepository;

  @MockitoBean
  private TeamRepository teamRepository;

  /**
   * Test {@link NoticeCommandService#createNotice(CreateNoticeRequest, Long)}.
   *
   * <ul>
   *   <li>Given {@link NoticeRepository}.
   *   <li>When {@link CreateNoticeRequest} (default constructor).
   * </ul>
   *
   * <p>Method under test: {@link NoticeCommandService#createNotice(CreateNoticeRequest, Long)}
   */
  @Test
  @DisplayName(
          "Test createNotice(CreateNoticeRequest, Long); given NoticeRepository; when CreateNoticeRequest (default constructor)")
  void testCreateNotice_givenNoticeRepository_whenCreateNoticeRequest() {
    // Arrange
    when(noticeRequestFactory.createNotice(Mockito.<CreateNoticeRequest>any(), Mockito.<Long>any()))
            .thenThrow(new IllegalArgumentException("foo"));

    // Act and Assert
    assertThrows(
            IllegalArgumentException.class,
            () -> noticeCommandService.createNotice(new CreateNoticeRequest(), 1L));
    verify(noticeRequestFactory).createNotice(isA(CreateNoticeRequest.class), eq(1L));
  }

  /**
   * Test {@link NoticeCommandService#deleteNotice(Long, Long)}.
   *
   * <p>Method under test: {@link NoticeCommandService#deleteNotice(Long, Long)}
   */
  @Test
  @DisplayName("Test deleteNotice(Long, Long)")
  void testDeleteNotice() {
    // Arrange
    when(noticeRepository.findById(Mockito.<Long>any()))
            .thenThrow(new IllegalArgumentException("foo"));

    // Act and Assert
    assertThrows(IllegalArgumentException.class, () -> noticeCommandService.deleteNotice(1L, 1L));
    verify(noticeRepository).findById(eq(1L));
  }
}
