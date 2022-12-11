package net.nightwhistler.tddasm.mos65xx;

import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class OpCodeTest {

    private List<OpCode> findCodesForByte(int byteValue) {
        return Arrays.asList(OpCode.values())
                .stream()
                .flatMap(oc ->
                    oc.addressingModeMappings().map( m -> new Tuple2<>(oc, m))
                            .toJavaStream()
                ).filter(t -> t._2.code() == (byte) byteValue)
                .map(Tuple2::_1)
                .collect(Collectors.toList());
    }

    /**
     * Sanity check test: each byte-code should
     * map to a unique opcode.
     */
    @Test
    public void testUniqueCodes() {
        for (int i=0x00; i < 0xFF; i++) {
            List<OpCode> codes = findCodesForByte(i);
            assertTrue(codes.size() <= 1,
                    String.format("Found %d OpCodes for byte-value %s: %s",
                            codes.size(),
                            Integer.toHexString(i),
                            codes
                    )
            );
        }
    }

}
