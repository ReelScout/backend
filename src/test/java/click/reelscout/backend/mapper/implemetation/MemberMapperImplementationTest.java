package click.reelscout.backend.mapper.implemetation;

import click.reelscout.backend.builder.definition.MemberBuilder;
import click.reelscout.backend.dto.request.MemberRequestDTO;
import click.reelscout.backend.dto.response.MemberResponseDTO;
import click.reelscout.backend.model.elasticsearch.MemberDoc;
import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.withSettings;

/**
 * Unit tests for MemberMapperImplementation.
 * Uses Mockito to mock dependencies and verify interactions.
 * Focuses on mapping logic without involving Spring context or actual database.
 */
class MemberMapperImplementationTest {

    private MemberBuilder memberBuilder;         // chainable mock
    private PasswordEncoder passwordEncoder;     // mock
    private MemberMapperImplementation mapper;   // unit under test

    @BeforeEach
    void setUp() {
        memberBuilder = mock(MemberBuilder.class, withSettings().defaultAnswer(Answers.RETURNS_SELF));
        passwordEncoder = mock(PasswordEncoder.class);
        mapper = new MemberMapperImplementation(memberBuilder, passwordEncoder);
    }

    /**
     * Tests that toDto correctly maps all fields from Member and base64 image string
     */
    @Test
    @DisplayName("toDto maps Member and base64 image into MemberResponseDTO")
    void toDto_mapsAllFields() {
        // Arrange
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(1L);
        when(member.getFirstName()).thenReturn("John");
        when(member.getLastName()).thenReturn("Doe");
        when(member.getBirthDate()).thenReturn(LocalDate.of(1990, 5, 20));
        when(member.getUsername()).thenReturn("john.doe");
        when(member.getEmail()).thenReturn("john.doe@mail.com");
        when(member.getRole()).thenReturn(Role.MEMBER);

        String base64 = "img==";

        // Act
        MemberResponseDTO dto = mapper.toDto(member, base64);

        // Assert
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Doe", dto.getLastName());
        assertEquals(LocalDate.of(1990, 5, 20), dto.getBirthDate());
        assertEquals("john.doe", dto.getUsername());
        assertEquals("john.doe@mail.com", dto.getEmail());
        assertEquals(Role.MEMBER, dto.getRole());
        assertEquals(base64, dto.getBase64Image());
    }

    /**
     * Tests that toBuilder populates MemberBuilder with all fields from Member
     */
    @Test
    @DisplayName("toBuilder copies all fields from Member onto the fluent MemberBuilder")
    void toBuilder_populatesBuilder() {
        // Arrange
        Member m = mock(Member.class);
        when(m.getId()).thenReturn(7L);
        when(m.getFirstName()).thenReturn("Alice");
        when(m.getLastName()).thenReturn("Smith");
        when(m.getBirthDate()).thenReturn(LocalDate.of(1985, 1, 15));
        when(m.getUsername()).thenReturn("alice");
        when(m.getEmail()).thenReturn("alice@mail.com");
        when(m.getPassword()).thenReturn("hashed");
        when(m.getS3ImageKey()).thenReturn("s3/key");
        when(m.getRole()).thenReturn(Role.MEMBER);

        // Act
        MemberBuilder returned = mapper.toBuilder(m);

        // Assert: the fluent chain should return the same builder mock
        assertSame(memberBuilder, returned);
        // Verify the essential field forwarding
        verify(memberBuilder).id(7L);
        verify(memberBuilder).firstName("Alice");
        verify(memberBuilder).lastName("Smith");
        verify(memberBuilder).birthDate(LocalDate.of(1985, 1, 15));
        verify(memberBuilder).username("alice");
        verify(memberBuilder).email("alice@mail.com");
        verify(memberBuilder).password("hashed");
        verify(memberBuilder).s3ImageKey("s3/key");
        verify(memberBuilder).role(Role.MEMBER);
    }

    /**
     * Tests that toEntity builds a Member from MemberRequestDTO,
     * encoding the password and setting Role.MEMBER by default.
     */
    @Test
    @DisplayName("toEntity builds a Member using encoded password and Role.MEMBER")
    void toEntity_buildsMemberWithEncodedPasswordAndRole() {
        // Arrange
        MemberRequestDTO req = mock(MemberRequestDTO.class);
        when(req.getFirstName()).thenReturn("Bob");
        when(req.getLastName()).thenReturn("Brown");
        when(req.getBirthDate()).thenReturn(LocalDate.of(1995, 12, 2));
        when(req.getUsername()).thenReturn("bob");
        when(req.getEmail()).thenReturn("bob@mail.com");
        when(req.getPassword()).thenReturn("raw-pwd");

        when(passwordEncoder.encode("raw-pwd")).thenReturn("ENC(raw-pwd)");

        Member built = mock(Member.class);
        when(memberBuilder.build()).thenReturn(built);

        String s3 = "img/key";

        // Act
        Member result = mapper.toEntity(req, s3);

        // Assert
        assertSame(built, result, "toEntity should return the instance produced by builder.build()");
        // Verify fluent population (including encoded password and role)
        verify(memberBuilder).firstName("Bob");
        verify(memberBuilder).lastName("Brown");
        verify(memberBuilder).birthDate(LocalDate.of(1995, 12, 2));
        verify(memberBuilder).username("bob");
        verify(memberBuilder).email("bob@mail.com");
        verify(passwordEncoder).encode("raw-pwd");
        verify(memberBuilder).password("ENC(raw-pwd)");
        verify(memberBuilder).role(Role.MEMBER);
        verify(memberBuilder).s3ImageKey("img/key");
        verify(memberBuilder).build();
    }

    /**
     * Tests that toDoc wraps a Member into a MemberDoc.
     * No assumptions about MemberDoc internals in unit scope.
     */
    @Test
    @DisplayName("toDoc wraps Member into MemberDoc")
    void toDoc_wrapsMember() {
        Member m = mock(Member.class);

        MemberDoc doc = mapper.toDoc(m);

        assertNotNull(doc, "MemberDoc must not be null");
        // No further assumptions about MemberDoc internals in unit scope
    }
}