package net.nightwhistler.tddasm.asmparser;

import io.vavr.collection.List;
import net.nightwhistler.tddasm.mos65xx.OpCode;
import net.nightwhistler.tddasm.mos65xx.Operand;
import net.nightwhistler.tddasm.mos65xx.OperationProvider;
import net.nightwhistler.tddasm.mos65xx.ProgramElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsmFileParser {

//    private static final Pattern pattern = Pattern.compile("\s*([a-zA-Z]{0,3})\s.*([#$0-9A-Fa-f].*) (;\\w.*)");
   private static final Pattern pattern = Pattern.compile("\s*([a-zA-Z]{0,3})\s*([#\\$0-9].*)");

    public List<ProgramElement> parseLine(String line) {
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            String opCodeAsString = matcher.group(1);
            String operandString = matcher.group(2);

            return List.of(
                    new OperationProvider(
                            OpCode.valueOf(opCodeAsString.toUpperCase()),
                            parseOperand(operandString)
                    )
            );
        } else {
            throw new IllegalArgumentException("Doesn't match");
        }
    }

    private Operand parseOperand(String value) {
        if (value.startsWith("#")) {
            return Operand.value(parseByte(value.substring(1)));
        } else {
            int intValue = parseByte(value);
            if (intValue > 0xFF) {
                return Operand.address(intValue);
            } else {
                return Operand.zeroPage(intValue);
            }
        }
//        throw new UnsupportedOperationException("Not supported yet: " + value);
    }

    private int parseByte(String value) {
        if (value.startsWith("$")) {
            return Integer.parseInt(value.substring(1), 16);
        } else {
            return Integer.parseInt(value);
        }
    }
}
