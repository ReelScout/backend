package click.reelscout.backend.factory.implementation;

import click.reelscout.backend.builder.definition.ProductionCompanyBuilder;
import click.reelscout.backend.dto.request.ProductionCompanyRequestDTO;
import click.reelscout.backend.dto.request.UserRequestDTO;
import click.reelscout.backend.dto.response.ProductionCompanyResponseDTO;
import click.reelscout.backend.dto.response.UserResponseDTO;
import click.reelscout.backend.mapper.definition.UserMapper;
import click.reelscout.backend.mapper.implemetation.ProductionCompanyMapperImplementation;
import click.reelscout.backend.model.jpa.ProductionCompany;
import click.reelscout.backend.model.jpa.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link ProductionCompanyMapperFactory}.
 */
@ExtendWith(MockitoExtension.class)
class ProductionCompanyMapperFactoryTest {

    @Mock
    private ProductionCompanyBuilder builder;

    @Mock
    private PasswordEncoder passwordEncoder;

    private ProductionCompanyMapperFactory factory;

    @BeforeEach
    void setUp() {
        factory = new ProductionCompanyMapperFactory(builder, passwordEncoder);
    }

    /** Tests for the supports() method */
    @Test
    @DisplayName("supports(UserRequestDTO) -> true for ProductionCompanyRequestDTO")
    void supports_requestDto_true() {
        assertTrue(factory.supports(new ProductionCompanyRequestDTO()));
    }

    /** Tests for the supports() method */
    @Test
    @DisplayName("supports(UserRequestDTO) -> false for non-ProductionCompany request DTO")
    void supports_requestDto_falseForOthers() {
        // Anonymous concrete subclass representing a non-production-company request
        UserRequestDTO other = new UserRequestDTO() {};
        assertFalse(factory.supports(other));
    }

    /** Tests for the supports() method */
    @Test
    @DisplayName("supports(UserResponseDTO) -> true for ProductionCompanyResponseDTO")
    void supports_responseDto_true() {
        assertTrue(factory.supports(new ProductionCompanyResponseDTO()));
    }

    /** Tests for the supports() method */
    @Test
    @DisplayName("supports(UserResponseDTO) -> false for non-ProductionCompany response DTO")
    void supports_responseDto_falseForOthers() {
        // Use a plain response DTO (or anonymous subclass) to represent a different type
        UserResponseDTO other = new UserResponseDTO();
        assertFalse(factory.supports(other));
    }

    /** Tests for the supports() method */
    @Test
    @DisplayName("supports(User) -> true for ProductionCompany")
    void supports_user_true() {
        // If ProductionCompany were abstract, use mock(ProductionCompany.class)
        assertTrue(factory.supports(new ProductionCompany()));
    }

    /** Tests for the supports() method */
    @Test
    @DisplayName("supports(User) -> false for non-ProductionCompany User")
    void supports_user_falseForOthers() {
        User someoneElse = mock(User.class);
        assertFalse(factory.supports(someoneElse));
    }

    /** Tests for the createMapper() method */
    @Test
    @DisplayName("createMapper() returns ProductionCompanyMapperImplementation")
    void createMapper_returnsCorrectType() {
        UserMapper<?, ?, ?, ?> mapper = factory.createMapper();
        assertNotNull(mapper, "createMapper must not return null");
        assertInstanceOf(ProductionCompanyMapperImplementation.class, mapper, "createMapper must return ProductionCompanyMapperImplementation");
    }
}