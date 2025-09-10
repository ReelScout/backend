package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.dto.response.ForumPostResponseDTO;
import click.reelscout.backend.dto.response.ForumThreadResponseDTO;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.ForumPost;
import click.reelscout.backend.model.jpa.ForumThread;
import click.reelscout.backend.model.jpa.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class ForumMapperImplementationTest {
    private final ForumMapperImplementation mapper = new ForumMapperImplementation();

    @Test
    void toThreadDto_mapsFields() {
        Content content = Mockito.mock(Content.class);
        Mockito.when(content.getId()).thenReturn(42L);

        User user = Mockito.mock(User.class);
        Mockito.when(user.getUsername()).thenReturn("alice");

        ForumThread thread = new ForumThread(content, "Hello", user);

        ForumThreadResponseDTO dto = mapper.toThreadDto(thread, 3);

        assertEquals("Hello", dto.getTitle());
        assertEquals(42L, dto.getContentId());
        assertEquals("alice", dto.getCreatedByUsername());
        assertEquals(3, dto.getPostCount());
    }

    @Test
    void toPostDto_mapsFields() {
        ForumThread thread = Mockito.mock(ForumThread.class);
        Mockito.when(thread.getId()).thenReturn(7L);

        User author = Mockito.mock(User.class);
        Mockito.when(author.getUsername()).thenReturn("bob");
        Mockito.when(author.getId()).thenReturn(99L);

        ForumPost post = new ForumPost(thread, author, null, "Body text");

        ForumPostResponseDTO dto = mapper.toPostDto(post);

        assertEquals(7L, dto.getThreadId());
        assertEquals(99L, dto.getAuthorId());
        assertEquals("Body text", dto.getBody());
        assertNull(dto.getParentId());
    }
}
