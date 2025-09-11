package click.reelscout.backend.builder.implementation;

import click.reelscout.backend.builder.definition.ProductionCompanyBuilder;
import click.reelscout.backend.model.jpa.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProductionCompanyBuilderImplementation.
 * These are pure unit tests (no Spring, no DB, no HTTP).
 * They verify that the builder correctly maps all fields into ProductionCompany
 * and that the fluent API behaves as expected.
 */
class ProductionCompanyBuilderImplementationTest {

    private ProductionCompanyBuilder newBuilder() {
        return new ProductionCompanyBuilderImplementation();
    }

    /** Test that build() never returns null, even with no fields set. */
    @Test
    void build_shouldNotBeNull() {
        // Act: build an empty ProductionCompany
        ProductionCompany pc = newBuilder().build();

        // Assert: build() must always return a non-null instance
        assertNotNull(pc, "build() must never return null");
    }

    /** Test that all fields set in the builder are correctly transferred to the ProductionCompany entity. */
    @Test
    void allFields_shouldBePropagated_includingContents() {
        // Arrange: prepare related entities
        Location location = new Location();
        List<Owner> owners = List.of(new Owner(), new Owner());
        Content contentItem = new ContentBuilderImplementation()
                .title("Item")
                .contentType(new ContentType("MOVIE"))
                .build();
        List<Content> contents = List.of(contentItem);

        // Act: build ProductionCompany with all fields set
        ProductionCompany pc = newBuilder()
                .id(100L)
                .username("studio")
                .email("studio@example.com")
                .password("pwd123")
                .role(Role.PRODUCTION_COMPANY)
                .s3ImageKey("logo-key")
                .name("DreamWorks")
                .location(location)
                .website("https://dreamworks.com")
                .owners(owners)
                .contents(contents)
                .build();

        // Assert: all values are propagated correctly
        assertEquals(100L, pc.getId());
        assertEquals("studio", pc.getUsername());
        assertEquals("studio@example.com", pc.getEmail());
        assertEquals("pwd123", pc.getPassword());
        assertEquals(Role.PRODUCTION_COMPANY, pc.getRole());
        assertEquals("logo-key", pc.getS3ImageKey());
        assertEquals("DreamWorks", pc.getName());
        assertEquals(location, pc.getLocation());
        assertEquals("https://dreamworks.com", pc.getWebsite());
        assertEquals(owners, pc.getOwners());
        assertEquals(contents, pc.getContents());
    }

    /** Test that all setter methods return the same builder instance for fluent chaining. */
    @Test
    void setters_shouldBeFluent() {
        // Arrange: create a new builder
        var b = newBuilder();

        // Act: chain setter calls
        var chained = b.id(1L)
                .username("u")
                .email("e")
                .password("p")
                .role(Role.ADMIN)
                .s3ImageKey("k")
                .name("Pixar")
                .website("pixar.com");

        // Assert: all setters must return the same builder instance
        assertSame(b, chained, "all setters should return this for chaining");
    }

    /** Test that reusing the builder does not mutate previously built instances. */
    @Test
    void builderReuse_shouldNotMutatePreviouslyBuiltInstances() {
        // Arrange & Act: build two different instances with the same builder
        var b = newBuilder().name("FirstName");
        var first = b.build();
        b.name("SecondName");
        var second = b.build();

        // Assert: the first instance remains unchanged
        assertEquals("FirstName", first.getName(), "the first built instance should not change");
        // Assert: the second instance reflects the updated state
        assertEquals("SecondName", second.getName(), "the second built instance should reflect the new value");
    }

    /** Test to cover Lombok-generated getters on the builder class for full code coverage. */
    @Test
    void builderLombokGetters_areCovered() {
        // This test only exists to cover Lombok-generated getters on the builder class.
        var location = new Location();
        var owners = List.of(new Owner());
        var contentItem = new ContentBuilderImplementation()
                .title("Item")
                .contentType(new ContentType("MOVIE"))
                .build();
        var contents = List.of(contentItem);

        var b = new ProductionCompanyBuilderImplementation()
                .name("Pixar")
                .location(location)
                .website("pixar.com")
                .owners(owners)
                .contents(contents);

        // Touch Lombok getters on the builder (not on the entity) for full coverage
        assertEquals("Pixar", ((ProductionCompanyBuilderImplementation) b).getName());
        assertEquals(location, ((ProductionCompanyBuilderImplementation) b).getLocation());
        assertEquals("pixar.com", ((ProductionCompanyBuilderImplementation) b).getWebsite());
        assertEquals(owners, ((ProductionCompanyBuilderImplementation) b).getOwners());
        assertEquals(contents, ((ProductionCompanyBuilderImplementation) b).getContents());
    }
}