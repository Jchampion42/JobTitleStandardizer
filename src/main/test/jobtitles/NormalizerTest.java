package jobtitles;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class NormalizerTest {

    private final Normalizer normalizer = new Normalizer();

    @ParameterizedTest
    @CsvSource({
            "Java engineer,Software engineer",
            "C# engineer,Software engineer",
            "Accountant,Accountant",
            "Chief Accountant,Accountant",
            "acountant,Accountant",
            "sofware enginer,Software engineer"
    })
    public void mapsWordBasedTitles(String input, String expected) {
        String result = normalizer.normalize(input);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            "acountant,Accountant",
            "sofware enginer,Software engineer",
            "C# enginer,Software engineer"
    })
    public void mapsTypos(String input, String expected) {
        String result = normalizer.normalize(input);
        assertEquals(expected, result);
    }

    @ParameterizedTest
    @CsvSource({
            "''",
            "' '",
            "asdfghjkl",
            "./#';]",
            "123456"
    })
    public void doesntMapInvalidInput(String input) {
        String result = normalizer.normalize(input);
        assertEquals("Unknown", result);
    }
}