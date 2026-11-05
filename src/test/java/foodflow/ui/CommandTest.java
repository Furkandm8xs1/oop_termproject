package foodflow.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class CommandTest {

    @Test
    void nameIsCaseInsensitiveAndArgumentsAreKept() {
        Command command = Command.parse("  ADD R01-01   2 ");
        assertEquals("add", command.name());
        assertEquals(List.of("R01-01", "2"), command.arguments());
        assertEquals(2, command.intArgument(1, "quantity"));
    }

    @Test
    void optionValuesMaySpanSeveralWords() {
        Command command = Command.parse("restaurants --cuisine Turkish --district Kadıköy Moda");
        assertEquals(Optional.of("Turkish"), command.option("cuisine"));
        assertEquals(Optional.of("Kadıköy Moda"), command.option("district"));
        assertTrue(command.arguments().isEmpty());
    }

    @Test
    void blankLineMissingArgumentAndNonNumberGiveFriendlyErrors_BR9() {
        assertThrows(InvalidInputException.class, () -> Command.parse("   "));
        assertThrows(InvalidInputException.class, () -> Command.parse("add R01-01").intArgument(1, "quantity"));
        assertThrows(InvalidInputException.class, () -> Command.parse("add R01-01 two").intArgument(1, "quantity"));
    }

    @Test
    void optionWithoutValueAndUnknownOptionAreRejected_BR9() {
        assertThrows(InvalidInputException.class, () -> Command.parse("restaurants --cuisine"));
        Command typo = Command.parse("restaurants --cusine Turkish");
        assertThrows(InvalidInputException.class, () -> typo.allowOnlyOptions("cuisine", "district"));
    }
}
