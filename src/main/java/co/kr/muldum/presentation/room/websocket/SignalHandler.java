package co.kr.muldum.presentation.room.websocket;

import co.kr.muldum.application.room.dto.room.response.RoomDetailResponse;
import co.kr.muldum.application.room.port.in.FindRoomByIdUseCase;
import co.kr.muldum.global.exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Slf4j
public class SignalHandler extends TextWebSocketHandler {

    private static final int DEFAULT_MAX_PARTICIPANTS = 4;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Map<String, UserSessionInfo>> roomSessions = new ConcurrentHashMap<>();
    private final FindRoomByIdUseCase findRoomByIdUseCase;

    public SignalHandler(FindRoomByIdUseCase findRoomByIdUseCase) {
        this.findRoomByIdUseCase = findRoomByIdUseCase;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomId = getRoomId(session);
        if (roomId == null) {
            session.close(CloseStatus.BAD_DATA.withReason("Room ID is required"));
            return;
        }

        try {
            RoomDetailResponse roomDetail = findRoomByIdUseCase.findRoomById(roomId);
            int maxParticipants = roomDetail.getMaxParticipants() != null
                    ? roomDetail.getMaxParticipants()
                    : DEFAULT_MAX_PARTICIPANTS;

            Long userId = extractUserId(session);
            String userName = extractUserName(session);

            Map<String, UserSessionInfo> sessionsInRoom = roomSessions.computeIfAbsent(
                    roomId,
                    key -> new ConcurrentHashMap<>()
            );

            if (sessionsInRoom.size() >= maxParticipants) {
                log.warn("Room {} is full. Rejecting connection for session {}", roomId, session.getId());
                sendMessage(session, Map.of("type", "error", "message", "room_full"));
                session.close(CloseStatus.POLICY_VIOLATION.withReason("Room is full"));
                return;
            }

            Set<UserInfoPayload> existingUsers = sessionsInRoom.values().stream()
                    .map(UserInfoPayload::from)
                    .collect(Collectors.toSet());

            UserSessionInfo currentInfo = new UserSessionInfo(session, userId, userName);
            sessionsInRoom.put(session.getId(), currentInfo);
            log.info("Session {} connected to room {}. Total users: {}", session.getId(), roomId, sessionsInRoom.size());

            if (!existingUsers.isEmpty()) {
                Map<String, Object> newUserMessage = Map.of(
                        "type", "new_user",
                        "data", UserInfoPayload.from(currentInfo)
                );
                sessionsInRoom.values().forEach(info -> {
                    if (!info.session().getId().equals(session.getId())) {
                        try {
                            sendMessage(info.session(), newUserMessage);
                        } catch (IOException e) {
                            log.error("Failed to notify session {}: {}", info.session().getId(), e.getMessage());
                        }
                    }
                });
                sendMessage(session, Map.of("type", "existing_users", "data", existingUsers));
            }
        } catch (CustomException e) {
            log.warn("Connection attempt to non-existent room {}. Session ID: {}", roomId, session.getId());
            sendMessage(session, Map.of("type", "error", "message", "room_not_found"));
            session.close(CloseStatus.POLICY_VIOLATION.withReason("Room not found"));
        } catch (IllegalArgumentException e) {
            log.warn("Invalid connection parameters for room {}: {}", roomId, e.getMessage());
            sendMessage(session, Map.of("type", "error", "message", e.getMessage()));
            session.close(CloseStatus.BAD_DATA.withReason(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error during connection establishment for room {}: {}", roomId, e.getMessage(), e);
            session.close(CloseStatus.SERVER_ERROR.withReason("Unexpected server error during validation"));
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String roomId = getRoomId(session);
        if (roomId == null) {
            return;
        }

        try {
            Map<String, Object> msg = objectMapper.readValue(message.getPayload(), Map.class);
            String to = (String) msg.get("to");
            if (to == null) {
                log.warn("Received message without 'to' field: {}", msg);
                return;
            }

            msg.put("from", session.getId());

            roomSessions.getOrDefault(roomId, Map.of()).values().stream()
                    .filter(info -> info.session().getId().equals(to) && info.session().isOpen())
                    .findFirst()
                    .ifPresent(recipient -> {
                        try {
                            sendMessage(recipient.session(), msg);
                        } catch (IOException e) {
                            log.error("Failed to send message to session {}: {}", to, e.getMessage());
                        }
                    });

        } catch (JsonProcessingException e) {
            log.error("Error parsing message: {}", message.getPayload(), e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = getRoomId(session);
        if (roomId == null) {
            return;
        }

        Map<String, UserSessionInfo> sessionsInRoom = roomSessions.get(roomId);
        if (sessionsInRoom != null) {
            UserSessionInfo removedInfo = sessionsInRoom.remove(session.getId());
            boolean removed = removedInfo != null;
            if (removed) {
                log.info("Session {} disconnected from room {}. Remaining users: {}", session.getId(), roomId, sessionsInRoom.size());
                Map<String, Object> userLeftMessage = Map.of(
                        "type", "user_left",
                        "data", removedInfo != null
                                ? UserInfoPayload.from(removedInfo)
                                : Map.of("sessionId", session.getId())
                );
                broadcastToAll(roomId, userLeftMessage);

                if (sessionsInRoom.isEmpty()) {
                    roomSessions.remove(roomId);
                    log.info("Room {} is now empty and has been removed.", roomId);
                }
            }
        }
    }

    private String getRoomId(WebSocketSession session) {
        return getQueryParam(session, "roomId");
    }

    private Long extractUserId(WebSocketSession session) {
        String userIdValue = getQueryParam(session, "userId");
        if (userIdValue == null) {
            throw new IllegalArgumentException("userId is required");
        }
        try {
            return Long.parseLong(userIdValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid userId format");
        }
    }

    private String extractUserName(WebSocketSession session) {
        String userName = getQueryParam(session, "userName");
        if (userName == null || userName.isBlank()) {
            throw new IllegalArgumentException("userName is required");
        }
        return userName;
    }

    private String getQueryParam(WebSocketSession session, String key) {
        if (session.getUri() == null || session.getUri().getQuery() == null) {
            return null;
        }
        String query = session.getUri().getQuery();
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2 && key.equals(pair[0])) {
                return pair[1];
            }
        }
        return null;
    }

    private void sendMessage(WebSocketSession session, Map<String, ?> message) throws IOException {
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
    }

    private void broadcastToAll(String roomId, Map<String, ?> message) {
        roomSessions.getOrDefault(roomId, Map.of()).values().forEach(info -> {
            try {
                WebSocketSession session = info.session();
                if (session.isOpen()) {
                    sendMessage(session, message);
                }
            } catch (IOException e) {
                log.error("Failed to broadcast to session {}: {}", info.session().getId(), e.getMessage());
            }
        });
    }

    private record UserSessionInfo(WebSocketSession session, Long userId, String userName) {
    }

    private record UserInfoPayload(String sessionId, Long userId, String userName) {
        static UserInfoPayload from(UserSessionInfo info) {
            return new UserInfoPayload(info.session().getId(), info.userId(), info.userName());
        }
    }
}
