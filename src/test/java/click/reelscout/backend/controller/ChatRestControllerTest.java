package click.reelscout.backend.controller;

import click.reelscout.backend.dto.response.ChatMessageResponseDTO;
import click.reelscout.backend.dto.response.ConversationResponseDTO;
import click.reelscout.backend.service.definition.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatRestControllerTest {

    @Mock
    private ChatService chatService;

    @Mock
    private Authentication authentication;

    private ChatRestController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ChatRestController(chatService);
        when(authentication.getName()).thenReturn("meUser");
    }

    @Test
    void getDirectHistory_shouldClampPageAndSize_andDelegateToService() {
        // Arrange
        ChatMessageResponseDTO dto = new ChatMessageResponseDTO();
        Page<ChatMessageResponseDTO> page = new PageImpl<>(List.of(dto));
        when(chatService.getDirectHistory(anyString(), anyString(), any()))
                .thenReturn(page);

        // Act
        ResponseEntity<Page<ChatMessageResponseDTO>> response =
                controller.getDirectHistory("otherUser", -5, 500, authentication);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());

        // Capture the pageable passed to the service
        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(chatService).getDirectHistory(eq("meUser"), eq("otherUser"), captor.capture());

        PageRequest usedPageable = captor.getValue();
        assertEquals(0, usedPageable.getPageNumber(), "Page should be clamped to 0");
        assertEquals(200, usedPageable.getPageSize(), "Size should be clamped to 200");
    }

    @Test
    void getDirectHistory_shouldUseValidPageAndSize() {
        // Arrange
        when(chatService.getDirectHistory(anyString(), anyString(), any()))
                .thenReturn(Page.empty());

        // Act
        controller.getDirectHistory("otherUser", 2, 20, authentication);

        // Assert
        ArgumentCaptor<PageRequest> captor = ArgumentCaptor.forClass(PageRequest.class);
        verify(chatService).getDirectHistory(eq("meUser"), eq("otherUser"), captor.capture());

        PageRequest pageable = captor.getValue();
        assertEquals(2, pageable.getPageNumber());
        assertEquals(20, pageable.getPageSize());
    }

    @Test
    void getRecentConversations_shouldClampSize_andDelegateToService() {
        // Arrange
        ConversationResponseDTO dto = new ConversationResponseDTO("conv-1", "otherUser", "Hello there", LocalDateTime.now());
        when(chatService.getRecentDirectConversations(anyString(), anyInt()))
                .thenReturn(List.of(dto));

        // Act
        ResponseEntity<List<ConversationResponseDTO>> response =
                controller.getRecentConversations(500, authentication);

        // Assert
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());

        // Size should be clamped to 100
        verify(chatService).getRecentDirectConversations("meUser", 100);
    }

    @Test
    void getRecentConversations_shouldClampSizeToMin1() {
        // Arrange
        when(chatService.getRecentDirectConversations(anyString(), anyInt()))
                .thenReturn(List.of());

        // Act
        controller.getRecentConversations(0, authentication);

        // Assert
        verify(chatService).getRecentDirectConversations("meUser", 1);
    }

    @Test
    void getRecentConversations_shouldUseDefaultValueWithinRange() {
        // Arrange
        when(chatService.getRecentDirectConversations(anyString(), anyInt()))
                .thenReturn(List.of());

        // Act
        controller.getRecentConversations(50, authentication);

        // Assert
        verify(chatService).getRecentDirectConversations("meUser", 50);
    }
}