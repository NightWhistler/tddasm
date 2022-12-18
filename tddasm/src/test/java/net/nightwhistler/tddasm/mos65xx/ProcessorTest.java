package net.nightwhistler.tddasm.mos65xx;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static net.nightwhistler.tddasm.mos65xx.Label.label;
import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operand.noValue;
import static net.nightwhistler.tddasm.mos65xx.Operand.value;
import static net.nightwhistler.tddasm.mos65xx.Operation.operation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class ProcessorTest {

    /**
     * Very basic test: load a value in the Accumulator and then check it.
     */
    @Test
    public void testLDAValue() {
        var operation = operation(OpCode.LDA, value(0x03));
        var processor = new Processor();

        processor.performOperation(operation);
        assertEquals((byte) 0x03, processor.getAccumulatorValue());
    }

    @Test
    public void testLDXValue() {
        var operation = operation(OpCode.LDX, value(0x03));
        var processor = new Processor();

        processor.performOperation(operation);
        assertEquals((byte) 0x03, processor.getXRegisterValue());
    }

    @Test
    public void testLDYValue() {
        var operation = operation(OpCode.LDY, value(0x03));
        var processor = new Processor();

        processor.performOperation(operation);
        assertEquals((byte) 0x03, processor.getYRegisterValue());
    }

    @Test
    public void testSTAValue() {
        var processor = new Processor();
        var ldaOperation = operation(OpCode.LDA, value(0x03));
        var staOperation = operation(OpCode.STA, address(0x0C69));

        processor.performOperation(ldaOperation);
        processor.performOperation(staOperation);

        assertEquals(0x03, processor.peekValue(0x0c69));
    }


    @Test
    public void testLDAAddress() {

        //Load the accumulator from $4030
        var staOperation = operation(OpCode.LDA, address(0x4030));
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
        var staOperation = operation(OpCode.LDY, value(0x03));
        var processor = new Processor();

        processor.performOperation(staOperation);
        assertEquals((byte) 0x03, processor.getYRegisterValue());

        assertFalse(processor.isZeroFlagSet());

        //Read from an empty address
        processor.performOperation(operation(OpCode.LDY, address(0x1122)));
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
                new ProcessorEvent.OperationPerformed(miniProg.startAddress(), new Operation(OpCode.LDA, value(0x03)))
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
        processor.setProgramCounter(miniProg.startAddress());

        //Take 50 steps
        for (int i = 0; i < 50; i++) {
            processor.step();
        }

        var ldaEvents = eventLog.stream()
                .filter(l -> (l instanceof ProcessorEvent.OperationPerformed o && o.operation().opCode() == OpCode.LDA));

        //The central LDA operation should be performed 3 times
        assertEquals(3, ldaEvents.toList().size());

    }

}
