package click.reelscout.backend.service.implementation;

import click.reelscout.backend.builder.definition.WatchlistBuilder;
import click.reelscout.backend.dto.request.WatchlistRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.CustomResponseDTO;
import click.reelscout.backend.dto.response.WatchlistResponseDTO;
import click.reelscout.backend.exception.custom.EntityCreateException;
import click.reelscout.backend.exception.custom.EntityDeleteException;
import click.reelscout.backend.exception.custom.EntityNotFoundException;
import click.reelscout.backend.exception.custom.EntityUpdateException;
import click.reelscout.backend.mapper.definition.ContentMapper;
import click.reelscout.backend.mapper.definition.WatchlistMapper;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.model.jpa.Watchlist;
import click.reelscout.backend.repository.jpa.ContentRepository;
import click.reelscout.backend.repository.jpa.UserRepository;
import click.reelscout.backend.repository.jpa.WatchlistRepository;
import click.reelscout.backend.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for WatchlistServiceImplementation.
 * We mock all collaborators and verify pure service logic / interactions.
 */
@ExtendWith(MockitoExtension.class)
class WatchlistServiceImplementationTest {

    private static final Long OWNER_ID = 1L;
    private static final Long OTHER_USER_ID = 99L;
    private static final Long WATCHLIST_ID = 10L;
    private static final Long CONTENT_ID = 7L;
    private static final Long MEMBER_ID = 5L;
    private static final Long NOT_FOUND_ID = 123L;
    private static final String S3_IMAGE_KEY = "img/key.png";
    private static final String BASE64_IMAGE_DATA = "base64-image-data";
    private static final String WATCHLIST_NAME = "My List";

    @Mock private WatchlistRepository watchlistRepository;
    @Mock private WatchlistMapper watchlistMapper;
    @Mock private ContentRepository contentRepository;
    @Mock private ContentMapper contentMapper;
    @Mock private S3Service s3Service;
    @Mock private UserRepository<Member> userRepository;
    @Mock private WatchlistBuilder mockBuilder;

    private WatchlistServiceImplementation service;

    // Common dummy data used across tests
    private Member owner;
    private Member otherUser;
    private Watchlist watchlist;
    private WatchlistResponseDTO watchlistDto;
    private WatchlistRequestDTO requestDto;

    @BeforeEach
    void setUp() {
        service = new WatchlistServiceImplementation(
                watchlistRepository, watchlistMapper,
                contentRepository, contentMapper,
                s3Service, userRepository
        );

        owner = new Member();
        // Assume there's a setId(Long) in your entity; if not, adapt builders accordingly.
        setMemberId(owner, OWNER_ID);

        otherUser = new Member();
        setMemberId(otherUser, OTHER_USER_ID);

        watchlist = new Watchlist();
        setWatchlistId(watchlist);
        setWatchlistMember(watchlist, owner);
        setWatchlistName(watchlist);
        setWatchlistPublic(watchlist, false);
        setWatchlistContents(watchlist, new ArrayList<>());

        requestDto = new WatchlistRequestDTO(); // fill fields if needed by your mapper
        watchlistDto = new WatchlistResponseDTO(); // shallow dto used as mapper output
    }

    // --- Helpers to avoid depending on Lombok-generated setters in test compile scope ---
    private void setFieldValue(Object target, Class<?> clazz, String fieldName, Object value) {
        try {
            var field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Failed to set field " + fieldName + ": " + e.getMessage());
        }
    }

    private void setMemberId(Member member, Long id) {
        setFieldValue(member, User.class, "id", id);
    }

    private void setWatchlistId(Watchlist watchlist) {
        setFieldValue(watchlist, Watchlist.class, "id", WatchlistServiceImplementationTest.WATCHLIST_ID);
    }

    private void setWatchlistMember(Watchlist watchlist, Member member) {
        setFieldValue(watchlist, Watchlist.class, "member", member);
    }

    private void setWatchlistName(Watchlist watchlist) {
        setFieldValue(watchlist, Watchlist.class, "name", WatchlistServiceImplementationTest.WATCHLIST_NAME);
    }

    private void setWatchlistPublic(Watchlist watchlist, boolean isPublic) {
        setFieldValue(watchlist, Watchlist.class, "isPublic", isPublic);
    }

    private void setWatchlistContents(Watchlist watchlist, List<Content> contents) {
        setFieldValue(watchlist, Watchlist.class, "contents", contents);
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("should create and map to DTO")
        void create_ok() {
            // Arrange
            Watchlist mappedEntity = new Watchlist();
            setWatchlistMember(mappedEntity, owner);

            when(watchlistMapper.toEntity(requestDto, owner)).thenReturn(mappedEntity);
            when(watchlistMapper.toDto(mappedEntity)).thenReturn(watchlistDto);

            // Act
            WatchlistResponseDTO result = service.create(owner, requestDto);

            // Assert
            assertThat(result).isSameAs(watchlistDto);
            verify(watchlistRepository).save(mappedEntity);
            verify(watchlistMapper).toDto(mappedEntity);
        }

        @Test
        @DisplayName("should wrap repository exception into EntityCreateException")
        void create_wrapsException() {
            Watchlist mappedEntity = new Watchlist();
            setWatchlistMember(mappedEntity, owner);

            when(watchlistMapper.toEntity(requestDto, owner)).thenReturn(mappedEntity);
            doThrow(new RuntimeException("db down")).when(watchlistRepository).save(mappedEntity);

            assertThatThrownBy(() -> service.create(owner, requestDto))
                    .isInstanceOf(EntityCreateException.class);
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("should update when owner matches and map to DTO")
        void update_ok() {
            // Arrange: existing watchlist belongs to owner
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));

            Watchlist mappedFromDto = new Watchlist();
            setWatchlistMember(mappedFromDto, owner);
            when(watchlistMapper.toEntity(requestDto, owner)).thenReturn(mappedFromDto);

            // Builder pattern emulation: toBuilder(...).id(id).build()
            Watchlist built = new Watchlist();
            setWatchlistId(built);
            setWatchlistMember(built, owner);

            // Mock the builder chain: toBuilder(...).id(...).build()
            when(watchlistMapper.toBuilder(mappedFromDto)).thenReturn(mockBuilder);
            when(mockBuilder.id(WATCHLIST_ID)).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(built);

            when(watchlistMapper.toDto(built)).thenReturn(watchlistDto);

            // Act
            WatchlistResponseDTO result = service.update(owner, WATCHLIST_ID, requestDto);

            // Assert
            assertThat(result).isSameAs(watchlistDto);
            verify(watchlistRepository).save(built);
        }

        @Test
        @DisplayName("should throw EntityNotFoundException when ID not found")
        void update_notFound() {
            when(watchlistRepository.findById(NOT_FOUND_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.update(owner, NOT_FOUND_ID, requestDto))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("should throw EntityUpdateException when user is not the owner")
        void update_unauthorized() {
            // Arrange: watchlist belongs to owner, but caller is otherUser
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));

            assertThatThrownBy(() -> service.update(otherUser, WATCHLIST_ID, requestDto))
                    .isInstanceOf(EntityUpdateException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("should wrap repository exception into EntityUpdateException")
        void update_wrapsException() {
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));

            Watchlist mappedFromDto = new Watchlist();
            setWatchlistMember(mappedFromDto, owner);
            when(watchlistMapper.toEntity(requestDto, owner)).thenReturn(mappedFromDto);

            // Emulate final entity from builder
            Watchlist built = new Watchlist();
            setWatchlistId(built);
            setWatchlistMember(built, owner);

            // Mock the builder chain: toBuilder(...).id(...).build()
            when(watchlistMapper.toBuilder(mappedFromDto)).thenReturn(mockBuilder);
            when(mockBuilder.id(WATCHLIST_ID)).thenReturn(mockBuilder);
            when(mockBuilder.build()).thenReturn(built);

            doThrow(new RuntimeException("write failed")).when(watchlistRepository).save(built);

            assertThatThrownBy(() -> service.update(owner, WATCHLIST_ID, requestDto))
                    .isInstanceOf(EntityUpdateException.class);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("should delete when owner matches")
        void delete_ok() {
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));

            CustomResponseDTO res = service.delete(owner, WATCHLIST_ID);

            assertThat(res).isNotNull();
            assertThat(res.getMessage()).containsIgnoringCase("deleted");
            verify(watchlistRepository).delete(watchlist);
        }

        @Test
        @DisplayName("should throw not found if id missing")
        void delete_notFound() {
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.delete(owner, WATCHLIST_ID))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("should throw EntityDeleteException when user not owner")
        void delete_unauthorized() {
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));

            assertThatThrownBy(() -> service.delete(otherUser, WATCHLIST_ID))
                    .isInstanceOf(EntityDeleteException.class)
                    .hasMessageContaining("not authorized");
        }

        @Test
        @DisplayName("should wrap repository exception into EntityDeleteException")
        void delete_wrapsException() {
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));
            doThrow(new RuntimeException("db err")).when(watchlistRepository).delete(watchlist);

            assertThatThrownBy(() -> service.delete(owner, WATCHLIST_ID))
                    .isInstanceOf(EntityDeleteException.class);
        }
    }

    @Test
    @DisplayName("getAllByMember: should map list to DTOs")
    void getAllByMember_ok() {
        when(watchlistRepository.findAllByMember(owner)).thenReturn(List.of(watchlist));
        when(watchlistMapper.toDto(watchlist)).thenReturn(watchlistDto);

        List<WatchlistResponseDTO> result = service.getAllByMember(owner);

        assertThat(result).hasSize(1).first().isSameAs(watchlistDto);
    }

    @Nested
    @DisplayName("addContentToWatchlist")
    class AddContent {

        @Test
        @DisplayName("should add content, save, and map with images")
        void add_ok() {
            // Arrange
            Content content = new Content();
            setContentId(content);
            setContentS3Key(content);

            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));
            when(contentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(content));

            // Mapper for content -> DTO uses s3Service.getFile(key)
            when(s3Service.getFile(S3_IMAGE_KEY)).thenReturn(BASE64_IMAGE_DATA);
            ContentResponseDTO cr = new ContentResponseDTO();
            when(contentMapper.toDto(eq(content), anyString())).thenReturn(cr);

            WatchlistResponseDTO dto = new WatchlistResponseDTO();
            when(watchlistMapper.toDto(eq(watchlist), anyList())).thenReturn(dto);

            // Act
            WatchlistResponseDTO result = service.addContentToWatchlist(owner, WATCHLIST_ID, CONTENT_ID);

            // Assert
            assertThat(result).isSameAs(dto);
            assertThat(watchlist.getContents()).contains(content);
            verify(watchlistRepository).save(watchlist);
            verify(contentMapper).toDto(eq(content), anyString());
            verify(watchlistMapper).toDto(eq(watchlist), argThat(list -> list.size() == 1));
        }

        @Test
        @DisplayName("should throw not found when watchlist id missing")
        void add_watchlistNotFound() {
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.addContentToWatchlist(owner, WATCHLIST_ID, CONTENT_ID))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("should throw not found when content id missing")
        void add_contentNotFound() {
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));
            when(contentRepository.findById(CONTENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.addContentToWatchlist(owner, WATCHLIST_ID, CONTENT_ID))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("should wrap save exception into EntityUpdateException")
        void add_wrapsException() {
            Content content = new Content();
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));
            when(contentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(content));

            doThrow(new RuntimeException("fail")).when(watchlistRepository).save(watchlist);

            assertThatThrownBy(() -> service.addContentToWatchlist(owner, WATCHLIST_ID, CONTENT_ID))
                    .isInstanceOf(EntityUpdateException.class)
                    .hasMessageContaining("Failed to add content");
        }
    }

    @Nested
    @DisplayName("removeContentFromWatchlist")
    class RemoveContent {

        @Test
        @DisplayName("should remove content, save, and map with images")
        void remove_ok() {
            Content content = new Content();
            setContentId(content);
            setContentS3Key(content);
            watchlist.getContents().add(content);

            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));
            when(contentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(content));

            WatchlistResponseDTO dto = new WatchlistResponseDTO();
            when(watchlistMapper.toDto(eq(watchlist), anyList())).thenReturn(dto);

            WatchlistResponseDTO result = service.removeContentFromWatchlist(owner, WATCHLIST_ID, CONTENT_ID);

            assertThat(result).isSameAs(dto);
            assertThat(watchlist.getContents()).doesNotContain(content);
            verify(watchlistRepository).save(watchlist);
        }

        @Test
        @DisplayName("should throw not found when watchlist missing")
        void remove_watchlistNotFound() {
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.removeContentFromWatchlist(owner, WATCHLIST_ID, CONTENT_ID))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("should throw not found when content missing")
        void remove_contentNotFound() {
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));
            when(contentRepository.findById(CONTENT_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.removeContentFromWatchlist(owner, WATCHLIST_ID, CONTENT_ID))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("should wrap save exception into EntityUpdateException")
        void remove_wrapsException() {
            Content content = new Content();
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));
            when(contentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(content));

            doThrow(new RuntimeException("db")).when(watchlistRepository).save(watchlist);

            assertThatThrownBy(() -> service.removeContentFromWatchlist(owner, WATCHLIST_ID, CONTENT_ID))
                    .isInstanceOf(EntityUpdateException.class);
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("should return DTO if owner requests private list")
        void getById_ownerPrivate_ok() {
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));

            // No contents inside: mapper should be called with empty mapped list
            when(watchlistMapper.toDto(eq(watchlist), anyList())).thenReturn(watchlistDto);

            WatchlistResponseDTO result = service.getById(owner, WATCHLIST_ID);

            assertThat(result).isSameAs(watchlistDto);
            verify(watchlistMapper).toDto(eq(watchlist), argThat(List::isEmpty));
        }

        @Test
        @DisplayName("should return DTO if public list requested by other user")
        void getById_public_ok() {
            setWatchlistPublic(watchlist, true);
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));
            when(watchlistMapper.toDto(eq(watchlist), anyList())).thenReturn(watchlistDto);

            WatchlistResponseDTO result = service.getById(otherUser, WATCHLIST_ID);

            assertThat(result).isSameAs(watchlistDto);
        }

        @Test
        @DisplayName("should throw not found when private list requested by non-owner")
        void getById_private_unauthorized() {
            setWatchlistPublic(watchlist, false);
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.of(watchlist));

            assertThatThrownBy(() -> service.getById(otherUser, WATCHLIST_ID))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("should throw not found if id missing")
        void getById_notFound() {
            when(watchlistRepository.findById(WATCHLIST_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(owner, WATCHLIST_ID))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Test
    @DisplayName("getAllByMemberAndContent: should map lists")
    void getAllByMemberAndContent_ok() {
        Content c = new Content();
        when(contentRepository.findById(CONTENT_ID)).thenReturn(Optional.of(c));

        when(watchlistRepository.findAllByMemberAndContentsIsContaining(owner, c))
                .thenReturn(List.of(watchlist));
        when(watchlistMapper.toDto(watchlist)).thenReturn(watchlistDto);

        List<WatchlistResponseDTO> result = service.getAllByMemberAndContent(owner, CONTENT_ID);

        assertThat(result).hasSize(1).first().isSameAs(watchlistDto);
    }

    @Test
    @DisplayName("getAllByMemberAndContent: should throw not found when content ID missing")
    void getAllByMemberAndContent_contentNotFound() {
        when(contentRepository.findById(CONTENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAllByMemberAndContent(owner, CONTENT_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("getAllPublicByMember: should fetch member and map lists")
    void getAllPublicByMember_ok() {
        Member m = new Member();
        setMemberId(m, MEMBER_ID);

        when(userRepository.findById(MEMBER_ID)).thenReturn(Optional.of(m));
        when(watchlistRepository.findAllByMemberAndIsPublic(m, true)).thenReturn(List.of(watchlist));
        when(watchlistMapper.toDto(watchlist)).thenReturn(watchlistDto);

        List<WatchlistResponseDTO> result = service.getAllPublicByMember(MEMBER_ID);

        assertThat(result).hasSize(1).first().isSameAs(watchlistDto);
    }

    @Test
    @DisplayName("getAllPublicByMember: should throw not found when member missing")
    void getAllPublicByMember_memberNotFound() {
        when(userRepository.findById(MEMBER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getAllPublicByMember(MEMBER_ID))
                .isInstanceOf(EntityNotFoundException.class);
    }

    // --- Content helpers ---
    private void setContentId(Content content) {
        setFieldValue(content, Content.class, "id", WatchlistServiceImplementationTest.CONTENT_ID);
    }

    private void setContentS3Key(Content content) {
        setFieldValue(content, Content.class, "s3ImageKey", WatchlistServiceImplementationTest.S3_IMAGE_KEY);
    }
}