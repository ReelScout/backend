package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.model.jpa.Member;
import click.reelscout.backend.model.jpa.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link MemberBuilderImplementation}.
 * Ensures that all fields are correctly set in the built Member entity
 * and that the fluent API returns the same builder instance.
 */
class MemberBuilderImplementationTest {

    /** Test that all fields set in the builder are correctly transferred to the Member entity. */
    @Test
    void build_WithAllFields_PopulatesMember() {
        // Arrange & Act: build a Member using the builder and set all fields
        Member member = new MemberBuilderImplementation()
                .id(1L)
                .username("john.doe")
                .email("john.doe@mail.com")
                .password("secret")
                .role(Role.MEMBER)
                .s3ImageKey("img/key")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 5, 20))
                .build();

        // Assert: all values are correctly transferred to the Member entity
        assertNotNull(member);
        assertEquals(1L, member.getId());
        assertEquals("john.doe", member.getUsername());
        assertEquals("john.doe@mail.com", member.getEmail());
        assertEquals("secret", member.getPassword());
        assertEquals(Role.MEMBER, member.getRole());
        assertEquals("img/key", member.getS3ImageKey());
        assertEquals("John", member.getFirstName());
        assertEquals("Doe", member.getLastName());
        assertEquals(LocalDate.of(1990, 5, 20), member.getBirthDate());
    }

    /** Test that the fluent API returns the same builder instance for method chaining. */
    @Test
    void fluentApi_ReturnsSameBuilder() {
        // Arrange: create a new builder
        MemberBuilderImplementation builder = new MemberBuilderImplementation();

        // Act: chain setter methods
        var same = builder.firstName("Alice").lastName("Smith");

        // Assert: fluent API must return the same builder instance
        assertSame(builder, same);
    }
}