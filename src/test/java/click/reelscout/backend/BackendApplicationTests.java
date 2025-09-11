package click.reelscout.backend;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This class is used to run all tests in the "click.reelscout.backend" package.
 * The @SuppressWarnings annotation is used to ignore the warning about no test methods.
 */
@SuppressWarnings("java:S2187")
@Suite
@SelectPackages("click.reelscout.backend")
@SpringBootTest
class BackendApplicationTests {
}
