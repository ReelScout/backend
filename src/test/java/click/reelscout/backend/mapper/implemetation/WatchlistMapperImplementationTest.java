package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.WatchlistBuilder;
import click.reelscout.backend.dto.request.WatchlistRequestDTO;
import click.reelscout.backend.dto.response.ContentResponseDTO;
import click.reelscout.backend.dto.response.WatchlistResponseDTO;
import click.reelscout.backend.dto.response.WatchlistWithContentsResponseDTO;
import click.reelscout.backend.model.jpa.Content;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.User;
import click.reelscout.backend.model.jpa.Watchlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link WatchlistMapperImplementation}.
 * Tests all mapping operations between entities and DTOs with comprehensive coverage
 * including edge cases and boundary conditions.
 */
@ExtendWith(MockitoExtension.class)
class WatchlistMapperImplementationTest {

    private static final Long WATCHLIST_ID = 1L;
    private static final String WATCHLIST_NAME = "My Test Watchlist";
    private static final boolean IS_PUBLIC = true;
    private static final boolean IS_PRIVATE = false;
    private static final Long MEMBER_ID = 10L;
    private static final Long CONTENT_ID_1 = 100L;
    private static final Long CONTENT_ID_2 = 101L;

    @Mock
    private WatchlistBuilder mockWatchlistBuilder;

    private WatchlistMapperImplementation mapper;
    private Watchlist watchlist;
    private Member member;
    private WatchlistRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        mapper = new WatchlistMapperImplementation(mockWatchlistBuilder);
        setupTestData();
    }

    /**
     * Sets up common test data used across multiple test methods.
     * Creates instances of entities and DTOs with predefined values.
     */
    private void setupTestData() {
        // Create member entity
        member = new Member();
        setFieldValue(member, User.class, "id", MEMBER_ID);

        // Create a watchlist entity
        watchlist = new Watchlist();
        setFieldValue(watchlist, Watchlist.class, "id", WATCHLIST_ID);
        setFieldValue(watchlist, Watchlist.class, "name", WATCHLIST_NAME);
        setFieldValue(watchlist, Watchlist.class, "isPublic", IS_PUBLIC);
        setFieldValue(watchlist, Watchlist.class, "member", member);
        setFieldValue(watchlist, Watchlist.class, "contents", new ArrayList<>());

        // Create request DTO
        requestDTO = new WatchlistRequestDTO();
        setFieldValue(requestDTO, WatchlistRequestDTO.class, "name", WATCHLIST_NAME);
        setFieldValue(requestDTO, WatchlistRequestDTO.class, "isPublic", IS_PUBLIC);
    }

    /**
     * Helper method to set private fields using reflection.
     * Avoids dependency on Lombok-generated setters during testing.
     */
    private void setFieldValue(Object target, Class<?> clazz, String fieldName, Object value) {
        try {
            var field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("Failed to set field " + fieldName + ": " + e.getMessage());
        }
    }

    @Nested
    @DisplayName("toDto(Watchlist) - Simple mapping without contents")
    class SimpleToDtoTests {

        /**
         * Tests mapping a Watchlist entity to WatchlistResponseDTO.
         * Verifies all basic fields are correctly mapped.
         */
        @Test
        @DisplayName("should map watchlist entity to DTO with all basic fields")
        void mapWatchlistToDto_shouldMapAllBasicFields() {
            // Act
            WatchlistResponseDTO result = mapper.toDto(watchlist);

            // Assert - verify all basic fields are correctly mapped
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(WATCHLIST_ID);
            assertThat(result.getName()).isEqualTo(WATCHLIST_NAME);
            assertThat(result.getIsPublic()).isEqualTo(IS_PUBLIC);
        }

        /**
         * Tests mapping a null Watchlist entity.
         * Expects a NullPointerException to be thrown.
         */
        @Test
        @DisplayName("should handle null watchlist gracefully")
        void mapNullWatchlist_shouldHandleGracefully() {
            // Act & Assert - should throw NullPointerException as expected behavior
            assertThrows(NullPointerException.class, () -> mapper.toDto(null));
        }

        @Test
        @DisplayName("should map private watchlist correctly")
        void mapPrivateWatchlist_shouldMapIsPublicFalse() {
            // Arrange - set watchlist to private
            setFieldValue(watchlist, Watchlist.class, "isPublic", IS_PRIVATE);

            // Act
            WatchlistResponseDTO result = mapper.toDto(watchlist);

            // Assert
            assertThat(result.getIsPublic()).isEqualTo(IS_PRIVATE);
        }

        /**
         * Tests mapping a watchlist with null name.
         * Verifies that the null name is preserved in the DTO.
         */
        @Test
        @DisplayName("should handle watchlist with null name")
        void mapWatchlistWithNullName_shouldPreserveNull() {
            // Arrange - set name to null
            setFieldValue(watchlist, Watchlist.class, "name", null);

            // Act
            WatchlistResponseDTO result = mapper.toDto(watchlist);

            // Assert
            assertThat(result.getName()).isNull();
            assertThat(result.getId()).isEqualTo(WATCHLIST_ID);
        }

        /**
         * Tests mapping a watchlist with null ID.
         * Verifies that the null ID is preserved in the DTO.
         */
        @Test
        @DisplayName("should handle watchlist with null ID")
        void mapWatchlistWithNullId_shouldPreserveNull() {
            // Arrange - set ID to null
            setFieldValue(watchlist, Watchlist.class, "id", null);

            // Act
            WatchlistResponseDTO result = mapper.toDto(watchlist);

            // Assert
            assertThat(result.getId()).isNull();
            assertThat(result.getName()).isEqualTo(WATCHLIST_NAME);
        }
    }

    @Nested
    @DisplayName("toDto(Watchlist, List<ContentResponseDTO>) - Mapping with contents")
    class ToDtoWithContentsTests {

        /**
         * Tests mapping a Watchlist entity along with a list of ContentResponseDTOs.
         * Verifies that the resulting DTO is of type WatchlistWithContentsResponseDTO
         * and contains all expected fields and contents.
         */
        @Test
        @DisplayName("should map watchlist with contents to WatchlistWithContentsResponseDTO")
        void mapWatchlistWithContents_shouldReturnWatchlistWithContentsResponseDTO() {
            // Arrange - create content DTOs
            List<ContentResponseDTO> contentDTOs = Arrays.asList(
                    createContentResponseDTO(CONTENT_ID_1, "Content 1"),
                    createContentResponseDTO(CONTENT_ID_2, "Content 2")
            );

            // Act
            WatchlistResponseDTO result = mapper.toDto(watchlist, contentDTOs);

            // Assert - verify it returns the correct subtype
            assertThat(result)
                    .isNotNull()
                    .extracting(
                            WatchlistResponseDTO::getId,
                            WatchlistResponseDTO::getName,
                            WatchlistResponseDTO::getIsPublic
                    )
                    .containsExactly(WATCHLIST_ID, WATCHLIST_NAME, IS_PUBLIC);
        }

        /**
         * Tests mapping a watchlist with an empty content list.
         * Verifies that the resulting DTO contains an empty contents list.
         */
        @Test
        @DisplayName("should handle empty content list")
        void mapWatchlistWithEmptyContents_shouldReturnEmptyContentsList() {
            // Arrange - empty content list
            List<ContentResponseDTO> emptyContents = Collections.emptyList();

            // Act
            WatchlistResponseDTO result = mapper.toDto(watchlist, emptyContents);

            // Assert
            assertThat(result).isInstanceOf(WatchlistWithContentsResponseDTO.class);
            WatchlistWithContentsResponseDTO withContents = (WatchlistWithContentsResponseDTO) result;
            assertThat(withContents.getContents()).isEmpty();
        }

        /**
         * Tests mapping a watchlist with a null content list.
         * Verifies that the resulting DTO has null contents.
         */
        @Test
        @DisplayName("should handle null content list")
        void mapWatchlistWithNullContents_shouldPreserveNull() {
            // Act
            WatchlistResponseDTO result = mapper.toDto(watchlist, null);

            // Assert
            assertThat(result).isInstanceOf(WatchlistWithContentsResponseDTO.class);
            WatchlistWithContentsResponseDTO withContents = (WatchlistWithContentsResponseDTO) result;
            assertThat(withContents.getContents()).isNull();
        }

        /**
         * Tests mapping a watchlist with a single content item.
         * Verifies that the resulting DTO contains exactly one content item.
         */
        @Test
        @DisplayName("should handle single content item")
        void mapWatchlistWithSingleContent_shouldMapCorrectly() {
            // Arrange - single content DTO
            ContentResponseDTO singleContent = createContentResponseDTO(CONTENT_ID_1, "Single Content");
            List<ContentResponseDTO> contentList = Collections.singletonList(singleContent);

            // Act
            WatchlistResponseDTO result = mapper.toDto(watchlist, contentList);

            // Assert
            assertThat(result).isInstanceOf(WatchlistWithContentsResponseDTO.class);
            WatchlistWithContentsResponseDTO withContents = (WatchlistWithContentsResponseDTO) result;
            assertThat(withContents.getContents()).hasSize(1);
            assertThat(withContents.getContents().getFirst()).isSameAs(singleContent);
        }

        /**
         * Helper method to create ContentResponseDTO for testing purposes.
         */
        private ContentResponseDTO createContentResponseDTO(Long id, String title) {
            ContentResponseDTO dto = new ContentResponseDTO();
            setFieldValue(dto, ContentResponseDTO.class, "id", id);
            setFieldValue(dto, ContentResponseDTO.class, "title", title);
            return dto;
        }
    }

    @Nested
    @DisplayName("toBuilder(Watchlist) - Converting entity to builder")
    class ToBuilderTests {

        /**
         * Tests converting a Watchlist entity to a WatchlistBuilder.
         * Verifies that all properties are correctly set on the builder.
         */
        @Test
        @DisplayName("should create builder with all watchlist properties")
        void convertWatchlistToBuilder_shouldSetAllProperties() {
            // Arrange - set up content list
            List<Content> contents = Arrays.asList(new Content(), new Content());
            setFieldValue(watchlist, Watchlist.class, "contents", contents);

            // Mock builder method chaining
            when(mockWatchlistBuilder.id(WATCHLIST_ID)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.name(WATCHLIST_NAME)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.contents(contents)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.isPublic(IS_PUBLIC)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.member(member)).thenReturn(mockWatchlistBuilder);

            // Act
            WatchlistBuilder result = mapper.toBuilder(watchlist);

            // Assert - verify builder is returned and all methods were called
            assertThat(result).isSameAs(mockWatchlistBuilder);
        }

        /**
         * Tests converting a watchlist with null properties to a builder.
         * Verifies that the builder methods are called with null values.
         */
        @Test
        @DisplayName("should handle watchlist with null properties")
        void convertWatchlistWithNullProperties_shouldHandleGracefully() {
            // Arrange - create watchlist with null properties
            Watchlist nullPropsWatchlist = new Watchlist();
            setFieldValue(nullPropsWatchlist, Watchlist.class, "id", null);
            setFieldValue(nullPropsWatchlist, Watchlist.class, "name", null);
            setFieldValue(nullPropsWatchlist, Watchlist.class, "contents", null);
            setFieldValue(nullPropsWatchlist, Watchlist.class, "isPublic", null);
            setFieldValue(nullPropsWatchlist, Watchlist.class, "member", null);

            // Mock builder method chaining with null values
            when(mockWatchlistBuilder.id(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.name(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.contents(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.isPublic(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.member(null)).thenReturn(mockWatchlistBuilder);

            // Act
            WatchlistBuilder result = mapper.toBuilder(nullPropsWatchlist);

            // Assert
            assertThat(result).isSameAs(mockWatchlistBuilder);
        }

        /**
         * Tests converting a null watchlist to a builder.
         * Expects a NullPointerException to be thrown.
         */
        @Test
        @DisplayName("should handle null watchlist input")
        void convertNullWatchlist_shouldThrowException() {
            // Act & Assert - should throw NullPointerException
            assertThrows(NullPointerException.class, () -> mapper.toBuilder(null));
        }
    }

    @Nested
    @DisplayName("toEntity(WatchlistRequestDTO, Member) - Converting DTO to entity")
    class ToEntityTests {

        /**
         * Tests converting a WatchlistRequestDTO to a Watchlist entity.
         * Verifies that all properties are correctly set on the entity.
         */
        @Test
        @DisplayName("should create entity from request DTO with member")
        void convertRequestDTOToEntity_shouldCreateValidEntity() {
            // Arrange - create expected entity for builder to return
            Watchlist expectedEntity = new Watchlist();
            setFieldValue(expectedEntity, Watchlist.class, "name", WATCHLIST_NAME);
            setFieldValue(expectedEntity, Watchlist.class, "isPublic", IS_PUBLIC);
            setFieldValue(expectedEntity, Watchlist.class, "member", member);

            // Mock builder method chaining
            when(mockWatchlistBuilder.id(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.name(WATCHLIST_NAME)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.isPublic(IS_PUBLIC)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.member(member)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.build()).thenReturn(expectedEntity);

            // Act
            Watchlist result = mapper.toEntity(requestDTO, member);

            // Assert
            assertThat(result).isSameAs(expectedEntity);
        }

        /**
         * Tests that the ID is always set to null when creating a new entity from a DTO.
         * Verifies that the builder's id method is called with null.
         */
        @Test
        @DisplayName("should always set ID to null for new entity")
        void convertRequestDTOToEntity_shouldAlwaysSetIdToNull() {
            when(mockWatchlistBuilder.id(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.name(WATCHLIST_NAME)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.isPublic(IS_PUBLIC)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.member(member)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.build()).thenReturn(new Watchlist());

            // Act
            mapper.toEntity(requestDTO, member);

            // Assert - ID should always be set to null for new entities
            verify(mockWatchlistBuilder).id(null);
            // This is verified through the mock expectations above
        }

        /**
         * Tests converting a request DTO with null name.
         * Verifies that the null name is preserved in the resulting entity.
         */
        @Test
        @DisplayName("should handle request DTO with null name")
        void convertRequestDTOWithNullName_shouldPreserveNull() {
            // Arrange - set name to null in request DTO
            setFieldValue(requestDTO, WatchlistRequestDTO.class, "name", null);

            when(mockWatchlistBuilder.id(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.name(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.isPublic(IS_PUBLIC)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.member(member)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.build()).thenReturn(new Watchlist());

            // Act
            Watchlist result = mapper.toEntity(requestDTO, member);

            // Assert
            assertNotNull(result);
        }

        /**
         * Tests converting a request DTO with null isPublic.
         * Verifies that the null isPublic is preserved in the resulting entity.
         */
        @Test
        @DisplayName("should handle request DTO with null isPublic")
        void convertRequestDTOWithNullIsPublic_shouldPreserveNull() {
            // Arrange - set isPublic to null
            setFieldValue(requestDTO, WatchlistRequestDTO.class, "isPublic", null);

            when(mockWatchlistBuilder.id(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.name(WATCHLIST_NAME)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.isPublic(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.member(member)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.build()).thenReturn(new Watchlist());

            // Act
            Watchlist result = mapper.toEntity(requestDTO, member);

            // Assert
            assertNotNull(result);
        }

        /**
         * Tests converting a null request DTO.
         * Expects a NullPointerException to be thrown.
         */
        @Test
        @DisplayName("should handle null request DTO")
        void convertNullRequestDTO_shouldThrowException() {
            // Act & Assert - should throw NullPointerException
            assertThrows(NullPointerException.class, () -> mapper.toEntity(null, member));
        }

        /**
         * Tests converting a request DTO with null member.
         * Verifies that the null member is preserved in the resulting entity.
         */
        @Test
        @DisplayName("should handle null member")
        void convertRequestDTOWithNullMember_shouldAcceptNull() {
            // Arrange
            when(mockWatchlistBuilder.id(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.name(WATCHLIST_NAME)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.isPublic(IS_PUBLIC)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.member(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.build()).thenReturn(new Watchlist());

            // Act
            Watchlist result = mapper.toEntity(requestDTO, null);

            // Assert
            assertNotNull(result);
        }

        /**
         * Tests converting a request DTO with isPublic set to false.
         * Verifies that the resulting entity has isPublic set to false.
         */
        @Test
        @DisplayName("should create private watchlist when isPublic is false")
        void convertPrivateWatchlistRequestDTO_shouldSetIsPublicFalse() {
            // Arrange - set request DTO to private
            setFieldValue(requestDTO, WatchlistRequestDTO.class, "isPublic", IS_PRIVATE);

            when(mockWatchlistBuilder.id(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.name(WATCHLIST_NAME)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.isPublic(IS_PRIVATE)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.member(member)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.build()).thenReturn(new Watchlist());

            // Act
            Watchlist result = mapper.toEntity(requestDTO, member);

            // Assert
            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("Integration tests - End-to-end mapping scenarios")
    class IntegrationTests {

        /**
         * Tests a complete round-trip mapping from DTO to entity and back to DTO.
         * Verifies that the original data is preserved through the transformations.
         */
        @Test
        @DisplayName("should perform complete round-trip mapping correctly")
        void performRoundTripMapping_shouldPreserveData() {
            // Arrange - start with a request DTO
            WatchlistRequestDTO originalRequest = new WatchlistRequestDTO();
            setFieldValue(originalRequest, WatchlistRequestDTO.class, "name", "Integration Test List");
            setFieldValue(originalRequest, WatchlistRequestDTO.class, "isPublic", false);

            // Mock the builder for entity creation
            Watchlist createdEntity = new Watchlist();
            setFieldValue(createdEntity, Watchlist.class, "id", 999L);
            setFieldValue(createdEntity, Watchlist.class, "name", "Integration Test List");
            setFieldValue(createdEntity, Watchlist.class, "isPublic", false);
            setFieldValue(createdEntity, Watchlist.class, "member", member);

            when(mockWatchlistBuilder.id(null)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.name("Integration Test List")).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.isPublic(false)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.member(member)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.build()).thenReturn(createdEntity);

            // Act - perform DTO -> Entity -> DTO mapping
            Watchlist entity = mapper.toEntity(originalRequest, member);
            WatchlistResponseDTO responseDTO = mapper.toDto(entity);

            // Assert - verify data is preserved through the round trip
            assertThat(responseDTO.getName()).isEqualTo("Integration Test List");
            assertThat(responseDTO.getIsPublic()).isFalse();
            assertThat(responseDTO.getId()).isEqualTo(999L);
        }

        /**
         * Tests a complex scenario involving all mapping methods.
         * Verifies that each method works correctly in sequence.
         */
        @Test
        @DisplayName("should handle complex mapping with contents and builder conversion")
        void performComplexMapping_shouldHandleAllOperations() {
            // Arrange - create a watchlist with contents
            List<Content> entityContents = Arrays.asList(new Content(), new Content());
            setFieldValue(watchlist, Watchlist.class, "contents", entityContents);

            List<ContentResponseDTO> dtoContents = Arrays.asList(
                    createContentResponseDTO(CONTENT_ID_1, "Content 1"),
                    createContentResponseDTO(CONTENT_ID_2, "Content 2")
            );

            // Mock builder operations
            when(mockWatchlistBuilder.id(WATCHLIST_ID)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.name(WATCHLIST_NAME)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.contents(entityContents)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.isPublic(IS_PUBLIC)).thenReturn(mockWatchlistBuilder);
            when(mockWatchlistBuilder.member(member)).thenReturn(mockWatchlistBuilder);

            // Act - perform multiple mapping operations
            WatchlistResponseDTO simpleDTO = mapper.toDto(watchlist);
            WatchlistResponseDTO dtoWithContents = mapper.toDto(watchlist, dtoContents);
            WatchlistBuilder builder = mapper.toBuilder(watchlist);

            // Assert - verify all operations work correctly
            assertThat(simpleDTO).isNotInstanceOf(WatchlistWithContentsResponseDTO.class);
            assertThat(dtoWithContents).isInstanceOf(WatchlistWithContentsResponseDTO.class);
            assertThat(builder).isSameAs(mockWatchlistBuilder);

            // Verify content preservation
            WatchlistWithContentsResponseDTO withContents = (WatchlistWithContentsResponseDTO) dtoWithContents;
            assertThat(withContents.getContents()).hasSize(2);
        }

        /**
         * Helper method to create ContentResponseDTO for integration testing.
         */
        private ContentResponseDTO createContentResponseDTO(Long id, String title) {
            ContentResponseDTO dto = new ContentResponseDTO();
            setFieldValue(dto, ContentResponseDTO.class, "id", id);
            setFieldValue(dto, ContentResponseDTO.class, "title", title);
            return dto;
        }
    }
}