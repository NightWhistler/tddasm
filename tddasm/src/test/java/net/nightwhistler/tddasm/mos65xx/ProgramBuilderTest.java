package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import io.vavr.control.Option;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static net.nightwhistler.ByteUtils.bytes;
import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;
import static org.junit.jupiter.api.Assertions.*;

class ProgramBuilderTest {

    @Test
    public void testSingleInstruction() {
        var elements = new ProgramBuilder()
                .lda(value(0x03))
                .buildElements();

        assertEquals(1, elements.size());
        assertEquals(new OperationProvider(OpCode.LDA, new Operand.ByteValue((byte) 0x03)), elements.get(0));
    }

    @Test
    public void testProgram() {
        var elements = new ProgramBuilder()
                .lda(value(0x03))
                .label("some_label")
                .iny()
                .jsr("print_y")
                .jmp(address(0x4567))
                .buildElements();

        assertEquals(5, elements.size());
        assertEquals(List.of(
                new OperationProvider(OpCode.LDA, new Operand.ByteValue((byte) 0x03)),
                new Label("some_label"),
                new OperationProvider(OpCode.INY, Operand.noValue()),
                new OperationProvider(OpCode.JSR, new Operand.LabelOperand("print_y")),
                new OperationProvider(OpCode.JMP, new Operand.TwoByteAddress(AddressingMode.AbsoluteAddress, (byte) 0x67, (byte) 0x45))
        ), elements);
    }

    @Test
    public void testText() {
        var elements = new ProgramBuilder()
                .text("ABC!")
                .buildElements();

        var dataElement = (Data) elements.head();
        assertEquals(4, dataElement.length());
        assertArrayEquals(bytes(65, 66, 67, 33), dataElement.bytes());
    }

    @Test
    public void testPETSCII() {
        var elements = new ProgramBuilder()
                .screenCodes("abcABC!")
                .buildElements();

        var dataElement = (Data) elements.head();
        assertEquals(7, dataElement.length());
        assertArrayEquals(bytes(01, 02, 03, 65, 66, 67, 33), dataElement.bytes());
    }

    @Test
    public void testEveryOpCodeShouldHaveABuilderMethod() throws InvocationTargetException, IllegalAccessException {
        List<Method> methods = List.of(ProgramBuilder.class.getMethods());

        for (OpCode opCode: OpCode.values()) {
            if (! opCode.isIllegal()) {
                List<Method> filteredMethods = methods
                        .filter(m -> m.getName().equals(opCode.name().toLowerCase()));

                assertTrue(filteredMethods.size() > 0, "There should be a builder method for OpCode " + opCode);
            }
        }
    }

    @Test
    public void testImpliedAndAccumulatorShouldHaveNoArgsMethod() {
        List<Method> methods = List.of(ProgramBuilder.class.getMethods());

        for (OpCode opCode: OpCode.values()) {
            if ( ! opCode.isIllegal() ) {
                boolean hasImpliedOrAccumulator = opCode.addressingModeMappings()
                        .find(m -> m.addressingMode() == AddressingMode.Implied || m.addressingMode() == AddressingMode.Accumulator)
                        .isDefined();
                if (hasImpliedOrAccumulator) {
                    boolean hasMethod = methods
                            .filter(m -> m.getName().equals(opCode.name().toLowerCase()))
                            .find(m -> m.getParameterTypes().length == 0)
                            .isDefined();
                    assertTrue(hasMethod, "Builder method should have a no-args version for OpCode " + opCode);
                }
            }
        }
    }

}
