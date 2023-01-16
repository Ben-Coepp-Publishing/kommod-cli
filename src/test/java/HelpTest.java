import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.bencoepp.Honnet;
import org.junit.jupiter.api.Test;

/**
 * Test the Help command of honnet and if it is callable
 */
public class HelpTest {
    /**
     * This test opens honnet --help and sees if it is possible to run
     */
    @Test
    public void shouldAnswerWithTrue() {
        try {
            Honnet honnet = new Honnet();
            assertTrue( true );
        }catch (Exception e){
            assertTrue(false);
        }
    }
}
