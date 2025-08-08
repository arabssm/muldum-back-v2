package co.kr.muldum.presentation.notice;

import static org.mockito.Mockito.when;

import co.kr.muldum.application.notice.command.CreateNoticeRequest;
import co.kr.muldum.application.notice.command.NoticeCommandService;
import co.kr.muldum.global.exception.GlobalExceptionHandler;
import co.kr.muldum.global.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
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
   * Test {@link NoticeTeacherController#createNotice(CreateNoticeRequest, CustomUserDetails)}.
   *
   * <p>Method under test: {@link NoticeTeacherController#createNotice(CreateNoticeRequest,
   * CustomUserDetails)}
   */
  @Test
  @DisplayName("Test createNotice(CreateNoticeRequest, CustomUserDetails)")
  void testCreateNotice() throws Exception {
    // Arrange
    when(noticeCommandService.createNotice(Mockito.<CreateNoticeRequest>any(), Mockito.<Long>any()))
            .thenReturn(1L);
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
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
            .andExpect(
                    MockMvcResultMatchers.content()
                            .string("{\"id\":1,\"message\":\"공지사항이 성공적으로 등록되었습니다.\"}"));
  }
}
