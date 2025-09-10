package click.reelscout.backend.controller;

import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.FriendshipResponseDTO;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.service.definition.FriendshipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for FriendshipController (no Spring context / no MockMvc).
 * We validate service delegation, ResponseEntity wrapping, and mapping annotations via reflection.
 */
@ExtendWith(MockitoExtension.class)
class FriendshipControllerTest {

    @Mock
    private FriendshipService friendshipService;

    @Mock
    private Member authenticatedMember;

    @Mock
    private CustomResponseDTO customResponse;

    @Mock
    private FriendshipResponseDTO friendshipResponse;

    private FriendshipController controller;

    @BeforeEach
    void setUp() {
        controller = new FriendshipController(friendshipService);
    }

    // -------------------- Behavior tests: delegation + ResponseEntity --------------------

    @Test
    void sendFriendRequest_shouldDelegateToService_andReturnOkBody() {
        // Arrange
        Long memberId = 10L;
        when(friendshipService.sendRequest(authenticatedMember, memberId)).thenReturn(customResponse);

        // Act
        ResponseEntity<CustomResponseDTO> response = controller.sendFriendRequest(authenticatedMember, memberId);

        // Assert
        verify(friendshipService).sendRequest(authenticatedMember, memberId);
        assertEquals(200, response.getStatusCode().value(), "Should return HTTP 200 OK");
        assertSame(customResponse, response.getBody(), "Body should be the service result");
    }

    @Test
    void acceptFriendRequest_shouldDelegateToService_andReturnOkBody() {
        Long memberId = 11L;
        when(friendshipService.acceptRequest(authenticatedMember, memberId)).thenReturn(customResponse);

        ResponseEntity<CustomResponseDTO> response = controller.acceptFriendRequest(authenticatedMember, memberId);

        verify(friendshipService).acceptRequest(authenticatedMember, memberId);
        assertEquals(200, response.getStatusCode().value());
        assertSame(customResponse, response.getBody());
    }

    @Test
    void rejectFriendRequest_shouldDelegateToService_andReturnOkBody() {
        Long memberId = 12L;
        when(friendshipService.rejectRequest(authenticatedMember, memberId)).thenReturn(customResponse);

        ResponseEntity<CustomResponseDTO> response = controller.rejectFriendRequest(authenticatedMember, memberId);

        verify(friendshipService).rejectRequest(authenticatedMember, memberId);
        assertEquals(200, response.getStatusCode().value());
        assertSame(customResponse, response.getBody());
    }

    @Test
    void removeFriend_shouldDelegateToService_andReturnOkBody() {
        Long memberId = 13L;
        when(friendshipService.removeFriend(authenticatedMember, memberId)).thenReturn(customResponse);

        ResponseEntity<CustomResponseDTO> response = controller.removeFriend(authenticatedMember, memberId);

        verify(friendshipService).removeFriend(authenticatedMember, memberId);
        assertEquals(200, response.getStatusCode().value());
        assertSame(customResponse, response.getBody());
    }

    @Test
    void getFriends_shouldDelegateToService_andReturnOkBody() {
        List<FriendshipResponseDTO> expected = List.of(friendshipResponse);
        when(friendshipService.getFriends(authenticatedMember)).thenReturn(expected);

        ResponseEntity<List<FriendshipResponseDTO>> response = controller.getFriends(authenticatedMember);

        verify(friendshipService).getFriends(authenticatedMember);
        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
    }

    @Test
    void getIncomingRequests_shouldDelegateToService_andReturnOkBody() {
        List<FriendshipResponseDTO> expected = List.of(friendshipResponse);
        when(friendshipService.getIncomingRequests(authenticatedMember)).thenReturn(expected);

        ResponseEntity<List<FriendshipResponseDTO>> response = controller.getIncomingRequests(authenticatedMember);

        verify(friendshipService).getIncomingRequests(authenticatedMember);
        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
    }

    @Test
    void getOutgoingRequests_shouldDelegateToService_andReturnOkBody() {
        List<FriendshipResponseDTO> expected = List.of(friendshipResponse);
        when(friendshipService.getOutgoingRequests(authenticatedMember)).thenReturn(expected);

        ResponseEntity<List<FriendshipResponseDTO>> response = controller.getOutgoingRequests(authenticatedMember);

        verify(friendshipService).getOutgoingRequests(authenticatedMember);
        assertEquals(200, response.getStatusCode().value());
        assertSame(expected, response.getBody());
    }

    // -------------------- Annotation tests via reflection --------------------

    @Test
    void controller_shouldHaveClassLevelRequestMapping_friendsPath() {
        RequestMapping mapping = FriendshipController.class.getAnnotation(RequestMapping.class);
        assertNotNull(mapping, "Class should be annotated with @RequestMapping");
        String[] paths = mapping.value().length > 0 ? mapping.value() : mapping.path();
        assertTrue(paths.length > 0, "RequestMapping should define a path/value");
        assertTrue(Arrays.asList(paths).contains("${api.paths.friends}"),
                "Class-level mapping should be \"${api.paths.friends}\"");
    }

    @Test
    void sendFriendRequest_shouldHavePostMapping_andParamAnnotations() throws NoSuchMethodException {
        Method m = FriendshipController.class.getMethod(
                "sendFriendRequest", Member.class, Long.class);

        PostMapping post = m.getAnnotation(PostMapping.class);
        assertNotNull(post, "sendFriendRequest should have @PostMapping");
        assertTrue(pathHas(post.value(), "/request/{memberId}") || pathHas(post.path(), "/request/{memberId}"),
                "Path should be \"/request/{memberId}\"");

        // Param annotations: first param @AuthenticationPrincipal, second @PathVariable
        Annotation[][] paramAnns = m.getParameterAnnotations();
        assertTrue(hasAnnotation(paramAnns[0], AuthenticationPrincipal.class),
                "First parameter should be annotated with @AuthenticationPrincipal");
        assertTrue(hasAnnotation(paramAnns[1], PathVariable.class),
                "Second parameter should be annotated with @PathVariable");
    }

    @Test
    void acceptFriendRequest_shouldHavePatchMapping_andParamAnnotations() throws NoSuchMethodException {
        Method m = FriendshipController.class.getMethod(
                "acceptFriendRequest", Member.class, Long.class);

        PatchMapping patch = m.getAnnotation(PatchMapping.class);
        assertNotNull(patch, "acceptFriendRequest should have @PatchMapping");
        assertTrue(pathHas(patch.value(), "/accept/{memberId}") || pathHas(patch.path(), "/accept/{memberId}"),
                "Path should be \"/accept/{memberId}\"");

        Annotation[][] paramAnns = m.getParameterAnnotations();
        assertTrue(hasAnnotation(paramAnns[0], AuthenticationPrincipal.class));
        assertTrue(hasAnnotation(paramAnns[1], PathVariable.class));
    }

    @Test
    void rejectFriendRequest_shouldHavePatchMapping_andParamAnnotations() throws NoSuchMethodException {
        Method m = FriendshipController.class.getMethod(
                "rejectFriendRequest", Member.class, Long.class);

        PatchMapping patch = m.getAnnotation(PatchMapping.class);
        assertNotNull(patch, "rejectFriendRequest should have @PatchMapping");
        assertTrue(pathHas(patch.value(), "/reject/{memberId}") || pathHas(patch.path(), "/reject/{memberId}"),
                "Path should be \"/reject/{memberId}\"");

        Annotation[][] paramAnns = m.getParameterAnnotations();
        assertTrue(hasAnnotation(paramAnns[0], AuthenticationPrincipal.class));
        assertTrue(hasAnnotation(paramAnns[1], PathVariable.class));
    }

    @Test
    void removeFriend_shouldHaveDeleteMapping_andParamAnnotations() throws NoSuchMethodException {
        Method m = FriendshipController.class.getMethod(
                "removeFriend", Member.class, Long.class);

        DeleteMapping del = m.getAnnotation(DeleteMapping.class);
        assertNotNull(del, "removeFriend should have @DeleteMapping");
        assertTrue(pathHas(del.value(), "/remove/{memberId}") || pathHas(del.path(), "/remove/{memberId}"),
                "Path should be \"/remove/{memberId}\"");

        Annotation[][] paramAnns = m.getParameterAnnotations();
        assertTrue(hasAnnotation(paramAnns[0], AuthenticationPrincipal.class));
        assertTrue(hasAnnotation(paramAnns[1], PathVariable.class));
    }

    @Test
    void getFriends_shouldHaveGetMapping_rootPath_andParamAnnotation() throws NoSuchMethodException {
        Method m = FriendshipController.class.getMethod("getFriends", Member.class);

        GetMapping get = m.getAnnotation(GetMapping.class);
        assertNotNull(get, "getFriends should have @GetMapping");
        // No path specified: expect empty (root of class-level mapping)
        assertTrue((get.value().length == 0) && (get.path().length == 0),
                "getFriends should map to the class-level root path");

        Annotation[][] paramAnns = m.getParameterAnnotations();
        assertTrue(hasAnnotation(paramAnns[0], AuthenticationPrincipal.class));
    }

    @Test
    void getIncomingRequests_shouldHaveGetMapping_andParamAnnotation() throws NoSuchMethodException {
        Method m = FriendshipController.class.getMethod("getIncomingRequests", Member.class);

        GetMapping get = m.getAnnotation(GetMapping.class);
        assertNotNull(get, "getIncomingRequests should have @GetMapping");
        assertTrue(pathHas(get.value(), "/requests/incoming") || pathHas(get.path(), "/requests/incoming"),
                "Path should be \"/requests/incoming\"");

        Annotation[][] paramAnns = m.getParameterAnnotations();
        assertTrue(hasAnnotation(paramAnns[0], AuthenticationPrincipal.class));
    }

    @Test
    void getOutgoingRequests_shouldHaveGetMapping_andParamAnnotation() throws NoSuchMethodException {
        Method m = FriendshipController.class.getMethod("getOutgoingRequests", Member.class);

        GetMapping get = m.getAnnotation(GetMapping.class);
        assertNotNull(get, "getOutgoingRequests should have @GetMapping");
        assertTrue(pathHas(get.value(), "/requests/outgoing") || pathHas(get.path(), "/requests/outgoing"),
                "Path should be \"/requests/outgoing\"");

        Annotation[][] paramAnns = m.getParameterAnnotations();
        assertTrue(hasAnnotation(paramAnns[0], AuthenticationPrincipal.class));
    }

    // -------------------- Helpers --------------------

    private static boolean pathHas(String[] arr, String expected) {
        return arr != null && Arrays.asList(arr).contains(expected);
    }

    private static boolean hasAnnotation(Annotation[] anns, Class<? extends Annotation> target) {
        for (Annotation a : anns) {
            if (a.annotationType().equals(target)) return true;
        }
        return false;
    }
}