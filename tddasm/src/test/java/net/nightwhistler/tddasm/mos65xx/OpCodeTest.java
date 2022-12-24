package net.nightwhistler.tddasm.mos65xx;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;


import static io.vavr.control.Option.some;
import static net.nightwhistler.tddasm.mos65xx.OpCode.JMP;
import static net.nightwhistler.tddasm.mos65xx.OpCode.LDA;
import static org.junit.jupiter.api.Assertions.*;

class OpCodeTest {

    private List<OpCode> findCodesForByte(int byteValue) {
        return List.of(OpCode.values())
                .flatMap(oc -> Try.of(() -> oc.addressingModeMappings()).getOrElse(List.empty())
                        .map(m -> new Tuple2<>(oc, m)))
                .filter(t -> t._2.code() == (byte) byteValue)
                .map(Tuple2::_1);
    }

    /**
     * Sanity check test: each byte-code should
     * map to a unique opcode.
     */
    @Test
    public void testUniqueCodes() {
        for (int i=0x00; i <= 0xFF; i++) {
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

    @Test
    public void testFindCode() {
       byte codeLDA = (byte) 0xA9;
       byte codeJMP = (byte) 0x4C;

       assertEquals(some(new OpCode.AdressingModeMapping(LDA, AddressingMode.Value, codeLDA)),
               OpCode.findByByteValue(codeLDA));

        assertEquals(some(new OpCode.AdressingModeMapping(JMP, AddressingMode.AbsoluteAddress, codeJMP)),
                OpCode.findByByteValue(codeJMP));
    }

}
