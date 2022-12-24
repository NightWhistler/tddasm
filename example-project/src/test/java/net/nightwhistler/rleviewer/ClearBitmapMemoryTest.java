package net.nightwhistler.rleviewer;

import net.nightwhistler.tddasm.mos65xx.Processor;
import net.nightwhistler.tddasm.mos65xx.ProcessorEvent;
import net.nightwhistler.tddasm.mos65xx.Program;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static net.nightwhistler.tddasm.mos65xx.OpCode.STA;
import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ClearBitmapMemoryTest {

    @Test
    public void testRun() {
        Processor processor = new Processor();
        Program clearBitMap = ClearBitmapMemory.clearBitmapMemory().buildProgram();
        List<ProcessorEvent.OperationPerformed> operationPerformedEvents = new ArrayList<>();
        List<ProcessorEvent.MemoryLocationChanged> memoryLocationChangeds = new ArrayList<>();

        processor.registerEventListener(ProcessorEvent.OperationPerformed.class, operationPerformedEvents::add);
        processor.registerEventListener(ProcessorEvent.MemoryLocationChanged.class, memoryLocationChangeds::add);

        processor.load(clearBitMap);
        processor.run(clearBitMap.startAddress());

        assertEquals(8000, operationPerformedEvents.stream()
                .filter(o -> o.operation().opCode() == STA).count());

        //Make sure the last location changed is 0x3F3F
        assertEquals(address(0x3F3F), memoryLocationChangeds.get(memoryLocationChangeds.size()-1).atLocation());
    }
}
