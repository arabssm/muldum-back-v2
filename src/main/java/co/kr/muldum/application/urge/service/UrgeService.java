package co.kr.muldum.application.urge.service;

import co.kr.muldum.application.urge.dto.UrgeRequest;
import co.kr.muldum.domain.task.model.Task;
import co.kr.muldum.domain.task.repository.TaskRepository;
import co.kr.muldum.domain.user.model.User;
import co.kr.muldum.global.exception.TaskNotFoundException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UrgeService {

    private final JavaMailSender mailSender;
    private final TaskRepository taskRepository;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Transactional(readOnly = true)
    public void sendUrgeEmail(UrgeRequest urgeRequest) {
        Task task = taskRepository.findById(urgeRequest.getTaskId())
                .orElseThrow(TaskNotFoundException::new);

        User assignee = task.getAssignee();
        if (assignee == null) {
            throw new IllegalStateException("작업에 할당된 담당자가 없습니다.");
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(assignee.getEmail());
            helper.setSubject("[Muldum] 과제 독촉 메시지가 도착했습니다.");
            String htmlContent = buildHtmlContent(assignee, task);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Handle exception
            throw new RuntimeException("이메일 전송에 실패했습니다.", e);
        }
    }

    private String buildHtmlContent(User assignee, Task task) {
        String taskUrl = frontendUrl + "/tasks/" + task.getId(); // Assuming this URL structure

        return "<!DOCTYPE html>"
                + "<html>"
                + "<head>"
                + "<style>"
                + "body { font-family: 'Apple SD Gothic Neo', 'sans-serif'; margin: 40px; background-color: #f4f4f4; }"
                + ".container { background-color: #ffffff; border: 1px solid #dddddd; padding: 40px; border-radius: 8px; max-width: 600px; margin: auto; }"
                + ".header { font-size: 24px; font-weight: bold; color: #333333; margin-bottom: 20px; }"
                + ".content { font-size: 16px; color: #555555; line-height: 1.6; }"
                + ".task-info { background-color: #f9f9f9; border-left: 4px solid #FF9B62; padding: 15px; margin: 20px 0; }"
                + ".task-title { font-weight: bold; }"
                + ".button { display: inline-block; padding: 12px 24px; margin-top: 20px; background-color: #FF9B62; color: #ffffff; text-decoration: none; border-radius: 5px; font-size: 16px; }"
                + ".footer { margin-top: 30px; font-size: 12px; color: #999999; text-align: center; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<div class='container'>"
                + "<div class='header'>과제 독촉 알림</div>"
                + "<div class='content'>"
                + "<p>안녕하세요, " + assignee.getName() + "님.</p>"
                + "<p>아래 과제에 대한 독촉 메시지입니다. 확인 후 진행 부탁드립니다.</p>"
                + "<div class='task-info'>"
                + "<strong>과제명:</strong> <span class='task-title'>" + task.getTitle() + "</span><br>"
                + "<strong>마감일:</strong> " + task.getDeadline()
                + "</div>"
                + "<a href='" + taskUrl + "' class='button'>과제로 이동하기</a>"
                + "</div>"
                + "<div class='footer'>"
                + "<p>본 메일은 Muldum 서비스에서 자동 발송되었습니다.</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
    }
}
