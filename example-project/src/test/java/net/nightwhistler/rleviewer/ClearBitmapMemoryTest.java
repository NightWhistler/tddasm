package net.nightwhistler.rleviewer;

import net.nightwhistler.tddasm.mos65xx.Processor;
import net.nightwhistler.tddasm.mos65xx.ProcessorEvent;
import net.nightwhistler.tddasm.mos65xx.Program;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static net.nightwhistler.tddasm.mos65xx.OpCode.STA;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClearBitmapMemoryTest {

    @Test
    public void testRun() {
        Processor processor = new Processor();
        Program clearBitMap = ClearBitmapMemory.clearBitmapMemory().buildProgram();
        List<ProcessorEvent> operationPerformedEvents = new ArrayList<>();

        processor.registerEventListener(operationPerformedEvents::add);

        processor.load(clearBitMap);
        processor.run(clearBitMap.startAddress());

        assertEquals(8000, operationPerformedEvents.stream()
                .filter(o -> o instanceof  ProcessorEvent.OperationPerformed op && op.operation().opCode() == STA).count());

    }
}
