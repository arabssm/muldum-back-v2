package co.kr.muldum.presentation.notice;

import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import co.kr.muldum.application.notice.command.CreateNoticeRequest;
import co.kr.muldum.application.notice.command.NoticeCommandService;
import co.kr.muldum.global.exception.GlobalExceptionHandler;
import co.kr.muldum.global.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.FormLoginRequestBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {NoticeTeacherController.class, GlobalExceptionHandler.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class NoticeTeacherControllerDiffblueTest {
  @Autowired
  private GlobalExceptionHandler globalExceptionHandler;

  @MockitoBean
  private NoticeCommandService noticeCommandService;

  @Autowired
  private NoticeTeacherController noticeTeacherController;

  /**
   * Test {@link NoticeTeacherController#updateNotice(Long, CreateNoticeRequest,
   * CustomUserDetails)}.
   *
   * <p>Method under test: {@link NoticeTeacherController#updateNotice(Long, CreateNoticeRequest,
   * CustomUserDetails)}
   */
  @Test
  @DisplayName("Test updateNotice(Long, CreateNoticeRequest, CustomUserDetails)")
  @Tag("ContributionFromDiffblue")
  void testUpdateNotice() throws Exception {
    // Arrange
    MockHttpServletRequestBuilder contentTypeResult =
            MockMvcRequestBuilders.patch("/tch/notice/{notice_id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON);

    ObjectMapper objectMapper = new ObjectMapper();
    MockHttpServletRequestBuilder requestBuilder =
            contentTypeResult.content(objectMapper.writeValueAsString(new CreateNoticeRequest()));

    // Act and Assert
    MockMvcBuilders.standaloneSetup(noticeTeacherController)
            .setControllerAdvice(globalExceptionHandler)
            .build()
            .perform(requestBuilder)
            .andExpect(MockMvcResultMatchers.status().is(400));
  }

  /**
   * Test {@link NoticeTeacherController#createNotice(CreateNoticeRequest, CustomUserDetails)}.
   *
   * <p>Method under test: {@link NoticeTeacherController#createNotice(CreateNoticeRequest,
   * CustomUserDetails)}
   */
  @Test
  @DisplayName("Test createNotice(CreateNoticeRequest, CustomUserDetails)")
  @Tag("ContributionFromDiffblue")
  void testCreateNotice() throws Exception {
    // Arrange
    MockHttpServletRequestBuilder contentTypeResult =
            MockMvcRequestBuilders.post("/tch/notice").contentType(MediaType.APPLICATION_JSON);
    ObjectMapper objectMapper = new ObjectMapper();
    MockHttpServletRequestBuilder requestBuilder =
            contentTypeResult.content(objectMapper.writeValueAsString(new CreateNoticeRequest()));

    // Act and Assert
    MockMvcBuilders.standaloneSetup(noticeTeacherController)
            .setControllerAdvice(globalExceptionHandler)
            .build()
            .perform(requestBuilder)
            .andExpect(MockMvcResultMatchers.status().is(400));
  }

  /**
   * Test {@link NoticeTeacherController#deleteNotice(Long, CustomUserDetails)}.
   *
   * <ul>
   *   <li>Given one.
   *   <li>Then status four hundred.
   * </ul>
   *
   * <p>Method under test: {@link NoticeTeacherController#deleteNotice(Long, CustomUserDetails)}
   */
  @Test
  @DisplayName("Test deleteNotice(Long, CustomUserDetails); given one; then status four hundred")
  @Tag("ContributionFromDiffblue")
  void testDeleteNotice_givenOne_thenStatusFourHundred() throws Exception {
    // Arrange
    doNothing().when(noticeCommandService).deleteNotice(Mockito.<Long>any(), Mockito.<Long>any());
    MockHttpServletRequestBuilder requestBuilder =
            MockMvcRequestBuilders.delete("/tch/notice/{notice_id}", "Uri Variables", "Uri Variables");

    // Act and Assert
    MockMvcBuilders.standaloneSetup(noticeTeacherController)
            .setControllerAdvice(globalExceptionHandler)
            .build()
            .perform(requestBuilder)
            .andExpect(MockMvcResultMatchers.status().is(400));
  }

  /**
   * Test {@link NoticeTeacherController#deleteNotice(Long, CustomUserDetails)}.
   *
   * <ul>
   *   <li>Given {@code /tch/notice/{notice_id}}.
   *   <li>When formLogin.
   *   <li>Then status {@link StatusResultMatchers#isNotFound()}.
   * </ul>
   *
   * <p>Method under test: {@link NoticeTeacherController#deleteNotice(Long, CustomUserDetails)}
   */
  @Test
  @DisplayName(
          "Test deleteNotice(Long, CustomUserDetails); given '/tch/notice/{notice_id}'; when formLogin; then status isNotFound()")
  @Tag("ContributionFromDiffblue")
  void testDeleteNotice_givenTchNoticeNoticeId_whenFormLogin_thenStatusIsNotFound()
          throws Exception {
    // Arrange
    doNothing().when(noticeCommandService).deleteNotice(Mockito.<Long>any(), Mockito.<Long>any());
    FormLoginRequestBuilder requestBuilder = SecurityMockMvcRequestBuilders.formLogin();

    // Act and Assert
    MockMvcBuilders.standaloneSetup(noticeTeacherController)
            .setControllerAdvice(globalExceptionHandler)
            .build()
            .perform(requestBuilder)
            .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  /**
   * Test {@link NoticeTeacherController#deleteNotice(Long, CustomUserDetails)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then status {@link StatusResultMatchers#isOk()}.
   * </ul>
   *
   * <p>Method under test: {@link NoticeTeacherController#deleteNotice(Long, CustomUserDetails)}
   */
  @Test
  @DisplayName("Test deleteNotice(Long, CustomUserDetails); when one; then status isOk()")
  @Tag("ContributionFromDiffblue")
  void testDeleteNotice_whenOne_thenStatusIsOk() throws Exception {
    // Arrange
    doNothing().when(noticeCommandService).deleteNotice(Mockito.<Long>any(), Mockito.<Long>any());
    MockHttpServletRequestBuilder requestBuilder =
            MockMvcRequestBuilders.delete("/tch/notice/{notice_id}", 1L).with(csrf());

    // Act and Assert
    MockMvcBuilders.standaloneSetup(noticeTeacherController)
            .apply(springSecurity())
            .setControllerAdvice(globalExceptionHandler)
            .build()
            .perform(requestBuilder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(
                    MockMvcResultMatchers.content().string("{\"message\":\"공지사항이 성공적으로 삭제되었습니다.\"}"));
  }
}
