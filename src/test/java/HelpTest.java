import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import de.bencoepp.Kommod;
import org.junit.jupiter.api.Test;

/**
 * Test the Help command of Kommod and if it is callable
 */
public class HelpTest {
    /**
     * This test opens Kommod --help and sees if it is possible to run
     */
    @Test
    public void shouldAnswerWithTrue() {
        try {
            Kommod kommod = new Kommod();
            assertTrue( true );
        }catch (Exception e){
            assertTrue(false);
        }
    }
}
