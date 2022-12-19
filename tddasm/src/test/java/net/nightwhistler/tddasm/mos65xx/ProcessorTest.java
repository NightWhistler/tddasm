package net.nightwhistler.tddasm.mos65xx;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static net.nightwhistler.tddasm.mos65xx.OpCode.BRK;
import static net.nightwhistler.tddasm.mos65xx.OpCode.JMP;
import static net.nightwhistler.tddasm.mos65xx.OpCode.JSR;
import static net.nightwhistler.tddasm.mos65xx.OpCode.LDA;
import static net.nightwhistler.tddasm.mos65xx.OpCode.LDX;
import static net.nightwhistler.tddasm.mos65xx.OpCode.LDY;
import static net.nightwhistler.tddasm.mos65xx.OpCode.RTS;
import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;
import static net.nightwhistler.tddasm.mos65xx.Operation.operation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProcessorTest {

    /**
     * Very basic test: load a value in the Accumulator and then check it.
     */
    @Test
    public void testLDAValue() {
        var operation = operation(LDA, value(0x03));
        var processor = new Processor();

        processor.performOperation(operation);
        assertEquals((byte) 0x03, processor.getAccumulatorValue());
    }

    @Test
    public void testLDXValue() {
        var operation = operation(LDX, value(0x03));
        var processor = new Processor();

        processor.performOperation(operation);
        assertEquals((byte) 0x03, processor.getXRegisterValue());
    }

    @Test
    public void testLDYValue() {
        var operation = operation(LDY, value(0x03));
        var processor = new Processor();

        processor.performOperation(operation);
        assertEquals((byte) 0x03, processor.getYRegisterValue());
    }

    @Test
    public void testSTAValue() {
        var processor = new Processor();
        var ldaOperation = operation(LDA, value(0x03));
        var staOperation = operation(OpCode.STA, address(0x0C69));

        processor.performOperation(ldaOperation);
        processor.performOperation(staOperation);

        assertEquals(0x03, processor.peekValue(0x0c69));
    }


    @Test
    public void testLDAAddress() {

        //Load the accumulator from $4030
        var staOperation = operation(LDA, address(0x4030));
        var processor = new Processor();

        //Assure the address was 0
        assertEquals(0, processor.peekValue(0x4030));

        //Manually set the address to the expected value
        processor.pokeValue(0x4030, (byte) 0x99);
        processor.performOperation(staOperation);

        assertEquals(0x99, Byte.toUnsignedInt(processor.getAccumulatorValue()));
    }

    @Test
    public void testZeroFlag() {
        var staOperation = operation(LDY, value(0x03));
        var processor = new Processor();

        processor.performOperation(staOperation);
        assertEquals((byte) 0x03, processor.getYRegisterValue());

        assertFalse(processor.isZeroFlagSet());

        //Read from an empty address
        processor.performOperation(operation(LDY, address(0x1122)));
        assertEquals((byte) 0x00, processor.getYRegisterValue());
        assertTrue(processor.isZeroFlagSet());
    }

    @Test
    public void testMemoryChangedEvents() {
        ProcessorEvent.Listener mockEventListener = Mockito.mock(ProcessorEvent.Listener.class);
        var processor = new Processor();
        processor.registerEventListener(mockEventListener);

        processor.pokeValue(0x3000, (byte) 0x33);
        verify(mockEventListener, times(1)).receiveEvent(
                new ProcessorEvent.MemoryLocationChanged(address(0x3000), (byte) 0x00, (byte) 0x33)
        );
    }

    @Test
    public void testOperationPerformedEvent() {
        ProcessorEvent.Listener mockEventListener = Mockito.mock(ProcessorEvent.Listener.class);
        var processor = new Processor();
        processor.registerEventListener(mockEventListener);

        //Very simple: load a value into the accumulator, then store it in memory.
        Program miniProg = new ProgramBuilder()
                .lda(value(0x03))
                .sta(address(0x3344))
                .buildProgram();

        processor.load(miniProg);
        processor.setProgramCounter(miniProg.startAddress());

        //Perform one operation
        processor.step();

        //LDA
        verify(mockEventListener).receiveEvent(
                new ProcessorEvent.OperationPerformed(miniProg.startAddress(), new Operation(LDA, value(0x03)))
        );

        //Perform the next operation
        processor.step();

        //Memory change event
        verify(mockEventListener).receiveEvent(
                new ProcessorEvent.MemoryLocationChanged(address(0x3344), (byte) 0x00, (byte) 0x03)
        );

        //STA operation performed
        verify(mockEventListener).receiveEvent(
                new ProcessorEvent.OperationPerformed(miniProg.startAddress().plus(2), new Operation(OpCode.STA, address(0x3344)))
        );


    }

    @Test
    public void testBranching() {
        ArrayList<ProcessorEvent> eventLog = new ArrayList<>();
        ProcessorEvent.Listener eventListener = event -> eventLog.add(event);
        var processor = new Processor();
        processor.registerEventListener(eventListener);

        //Very simple: load a value into the accumulator, then store it in memory.
        Program miniProg = new ProgramBuilder()
                .ldx(value(0x03))
                .label("loop")
                .lda(value(0x99))
                .dex()
                .bne("loop")
                .iny()
                .buildProgram();

        processor.load(miniProg);

        //Run until we encounter a BRK (code 0, so empty memory)
        processor.run(miniProg.startAddress());

        var ldaEvents = eventLog.stream()
                .filter(l -> (l instanceof ProcessorEvent.OperationPerformed o && o.operation().opCode() == LDA));

        //The central LDA operation should be performed 3 times
        assertEquals(3, ldaEvents.toList().size());

    }

    @Test
    public void testPushPop() {
        Processor processor = new Processor();
        processor.pushStack((byte) 0x03);
        processor.pushStack((byte) 0x05);

        assertEquals((byte) 0x05, processor.popStack());
        assertEquals((byte) 0x03, processor.popStack());
    }

    @Test
    public void testNoProgramLoaded() {
        Processor processor = new Processor();
        java.util.List<OpCode> events = new ArrayList<>();

        processor.registerEventListener(event -> {
            if (event instanceof ProcessorEvent.OperationPerformed op) {
                events.add(op.operation().opCode());
            }
        });

        processor.run(address(0xC000));

        //The processor will read 1 BRK command from memory (bytevalue 0x00)
        //and then stop.
        assertEquals(java.util.List.of(BRK), events);
    }

    @Test
    public void testSubroutine() {
        Processor processor = new Processor();
        java.util.List<OpCode> events = new ArrayList<>();

        processor.registerEventListener(event -> {
            if (event instanceof ProcessorEvent.OperationPerformed op) {
                events.add(op.operation().opCode());
            }
        });

        Program program = new ProgramBuilder()
                .jmp("main")
                .label("sub_routine")
                .lda(value(0x33))
                .rts()
                .label("main")
                .ldx(value(0x22))
                .jsr("sub_routine")
                .ldy(value(0x88))
                .buildProgram();

        processor.load(program);
        processor.run(program.startAddress());

        //Check that the opcodes were executed in the correct order
        assertEquals(java.util.List.of(JMP, LDX, JSR, LDA, RTS, LDY, BRK), events);
    }

}
