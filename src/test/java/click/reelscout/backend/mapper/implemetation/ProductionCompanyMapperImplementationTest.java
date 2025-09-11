package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.ProductionCompanyBuilder;
import click.reelscout.backend.dto.request.ProductionCompanyRequestDTO;
import click.reelscout.backend.dto.response.ProductionCompanyResponseDTO;
import click.reelscout.backend.model.elasticsearch.ProductionCompanyDoc;
import click.reelscout.backend.model.jpa.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.withSettings;

/**
 * Unit tests for {@link ProductionCompanyMapperImplementation}.
 * Uses Mockito to mock dependencies and verify interactions.
 * Covers mapping between entity, DTOs, and document.
 */
class ProductionCompanyMapperImplementationTest {

    private ProductionCompanyBuilder builder;          // chainable mock
    private PasswordEncoder passwordEncoder;           // mock
    private ProductionCompanyMapperImplementation mapper; // unit under test

    @BeforeEach
    void setUp() {
        builder = mock(ProductionCompanyBuilder.class, withSettings().defaultAnswer(Answers.RETURNS_SELF));
        passwordEncoder = mock(PasswordEncoder.class);
        mapper = new ProductionCompanyMapperImplementation(builder, passwordEncoder);
    }

    /**
     * Tests that toDto correctly maps all fields from ProductionCompany entity
     * and includes the provided base64 image string in the response DTO.
     */
    @Test
    @DisplayName("toDto maps ProductionCompany + base64 image to response DTO")
    void toDto_mapsAllFields() {
        // Arrange
        ProductionCompany pc = mock(ProductionCompany.class);
        Location loc = mock(Location.class);
        List<Owner> owners = List.of(mock(Owner.class), mock(Owner.class));

        when(pc.getId()).thenReturn(99L);
        when(pc.getName()).thenReturn("Pixar");
        when(pc.getLocation()).thenReturn(loc);
        when(pc.getWebsite()).thenReturn("https://pixar.com");
        when(pc.getOwners()).thenReturn(owners);
        when(pc.getUsername()).thenReturn("pixar-user");
        when(pc.getEmail()).thenReturn("contact@pixar.com");
        when(pc.getRole()).thenReturn(Role.PRODUCTION_COMPANY);

        String base64 = "img==";

        // Act
        ProductionCompanyResponseDTO dto = mapper.toDto(pc, base64);

        // Assert
        assertNotNull(dto);
        assertEquals(99L, dto.getId());
        assertEquals("Pixar", dto.getName());
        assertSame(loc, dto.getLocation());
        assertEquals("https://pixar.com", dto.getWebsite());
        assertSame(owners, dto.getOwners());
        assertEquals("pixar-user", dto.getUsername());
        assertEquals("contact@pixar.com", dto.getEmail());
        assertEquals(Role.PRODUCTION_COMPANY, dto.getRole());
        assertEquals(base64, dto.getBase64Image());
    }

    /**
     * Tests that toBuilder correctly forwards all fields from the ProductionCompany entity
     * to the ProductionCompanyBuilder for further building or modification.
     */
    @Test
    @DisplayName("toBuilder forwards all entity fields to the fluent builder")
    void toBuilder_populatesBuilder() {
        // Arrange
        ProductionCompany pc = mock(ProductionCompany.class);
        Location loc = mock(Location.class);
        List<Owner> owners = List.of(mock(Owner.class));
        List<?> contents = List.of(new Object());

        when(pc.getId()).thenReturn(7L);
        when(pc.getName()).thenReturn("DreamWorks");
        when(pc.getLocation()).thenReturn(loc);
        when(pc.getWebsite()).thenReturn("https://dreamworks.com");
        when(pc.getOwners()).thenReturn(owners);
        when(pc.getUsername()).thenReturn("dw-user");
        when(pc.getEmail()).thenReturn("info@dreamworks.com");
        when(pc.getPassword()).thenReturn("hashed");
        when(pc.getRole()).thenReturn(Role.PRODUCTION_COMPANY);
        when(pc.getS3ImageKey()).thenReturn("s3/logo");
        //noinspection unchecked
        when(pc.getContents()).thenReturn((List<Content>) contents);

        // Act
        ProductionCompanyBuilder returned = mapper.toBuilder(pc);

        // Assert: fluent chain should return the same builder mock
        assertSame(builder, returned);

        // Verify each field is forwarded to builder
        verify(builder).id(7L);
        verify(builder).name("DreamWorks");
        verify(builder).location(loc);
        verify(builder).website("https://dreamworks.com");
        verify(builder).owners(owners);
        verify(builder).username("dw-user");
        verify(builder).email("info@dreamworks.com");
        verify(builder).password("hashed");
        verify(builder).role(Role.PRODUCTION_COMPANY);
        verify(builder).s3ImageKey("s3/logo");
        //noinspection unchecked
        verify(builder).contents((List<Content>) contents);
    }

    /**
     * Tests that toEntity correctly builds a ProductionCompany entity from the request DTO,
     * encoding the password and setting the appropriate role and S3 image key.
     */
    @Test
    @DisplayName("toEntity builds a ProductionCompany with encoded password and proper role")
    void toEntity_buildsEntityWithEncodedPassword() {
        // Arrange request DTO
        ProductionCompanyRequestDTO req = mock(ProductionCompanyRequestDTO.class);
        Location loc = mock(Location.class);
        List<Owner> owners = List.of(mock(Owner.class));

        when(req.getName()).thenReturn("A24");
        when(req.getLocation()).thenReturn(loc);
        when(req.getWebsite()).thenReturn("https://a24films.com");
        when(req.getOwners()).thenReturn(owners);
        when(req.getUsername()).thenReturn("a24-user");
        when(req.getEmail()).thenReturn("hello@a24.com");
        when(req.getPassword()).thenReturn("raw");

        when(passwordEncoder.encode("raw")).thenReturn("ENC(raw)");

        ProductionCompany built = mock(ProductionCompany.class);
        when(builder.build()).thenReturn(built);

        String s3Key = "s3/a24";

        // Act
        ProductionCompany result = mapper.toEntity(req, s3Key);

        // Assert
        assertSame(built, result);
        verify(builder).name("A24");
        verify(builder).location(loc);
        verify(builder).website("https://a24films.com");
        verify(builder).owners(owners);
        verify(builder).username("a24-user");
        verify(builder).email("hello@a24.com");
        verify(passwordEncoder).encode("raw");
        verify(builder).password("ENC(raw)");
        verify(builder).role(Role.PRODUCTION_COMPANY);
        verify(builder).s3ImageKey("s3/a24");
        verify(builder).build();
    }

    /**
     * Tests that toDoc correctly wraps a ProductionCompany entity into a ProductionCompanyDoc.
     */
    @Test
    @DisplayName("toDoc wraps entity into ProductionCompanyDoc")
    void toDoc_wrapsEntity() {
        ProductionCompany pc = mock(ProductionCompany.class);

        ProductionCompanyDoc doc = mapper.toDoc(pc);

        assertNotNull(doc);
    }
}