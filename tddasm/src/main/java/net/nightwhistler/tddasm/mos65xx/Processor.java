package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import net.nightwhistler.ByteUtils;
import net.nightwhistler.tddasm.c64.kernal.Kernal;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.Byte.toUnsignedInt;
import static net.nightwhistler.ByteUtils.littleEndianBytesToInt;
import static net.nightwhistler.ByteUtils.toInt;
import static net.nightwhistler.tddasm.mos65xx.OpCode.JMP;
import static net.nightwhistler.tddasm.mos65xx.Operand.address;
import static net.nightwhistler.tddasm.mos65xx.Operation.operation;

public class Processor {
    private byte accumulator;
    private byte xRegister;
    private byte yRegister;

    private StatusRegister statusRegister = new StatusRegister();

    private int stackPointer = 0xF3;

    private Operand.TwoByteAddress programCounter = address(0x00);

    //64kb of memory, C64 style.
    private static int MEMORY_SIZE = (int) Math.pow(2, 16);
    private byte[] memory = new byte[MEMORY_SIZE];

    private Program currentProgram = null;

    private Map<Operand.TwoByteAddress, JavaRoutine> kernalRoutines = new HashMap<>();

    private List<ProcessorEvent.Listener> listeners = List.empty();

    private int operationCount = 0;

    public Processor() {
        this(true);
    }

    public Processor(boolean loadKernalRoutines) {
        if (loadKernalRoutines) {
            Kernal.registerKernalRoutines(this);
        }
    }

    public void registerJavaRoutine(JavaRoutine javaRoutine) {
        registerJavaRoutine(javaRoutine.location(), javaRoutine);
    }

    public void registerJavaRoutine(Operand.TwoByteAddress address, JavaRoutine javaRoutine) {
        this.kernalRoutines.put(address, javaRoutine);
        javaRoutine.onLoad(this);
    }

    /**
     * Attempts to perform the Operation provided by the given provider.
     *
     * If the Provider contains a VirtualOperand, it will be resolved
     * against the currently loaded Program, if any.
     * @param operationProvider
     */
    public void performOperation(OperationProvider operationProvider) {
        performOperation(operationProvider.provide(currentProgram, programCounter));
    }

    /**
     * Tells the Processor to process an operation.
     *
     * Since this is not read from memory, it does not
     * affect the Program Counter and won't raise OperationPerformed
     * events. If a memory location is changed, that will raise an event.
     *
     * @param operation
     */
    public void performOperation(Operation operation) {

        this.statusRegister.setBreakCommandFlag(false);

        switch (operation.opCode()) {
            //Load Acumulator
            case LDA -> setFlags(accumulator = value(operation.operand()));

            //Store Accumulator
            case STA -> pokeValue(location((Operand.AddressOperand) operation.operand()), accumulator);

            //Load X register
            case LDX -> setFlags(xRegister = value(operation.operand()));

            //Store X register
            case STX -> pokeValue(location((Operand.AddressOperand) operation.operand()), xRegister);

            //Load y register
            case LDY -> setFlags(yRegister = value(operation.operand()));

            case CPY -> setFlags((byte) (yRegister - value(operation.operand())));

            case CPX -> setFlags((byte) (xRegister - value(operation.operand())));

            case CMP -> setFlags((byte) (accumulator - value(operation.operand())));

            case AND -> setFlags(accumulator = (byte) (accumulator & value(operation.operand())));

            case ORA -> setFlags(accumulator = (byte) (accumulator | value(operation.operand())));

            case EOR -> setFlags(accumulator = (byte) (accumulator ^ value(operation.operand())));

            case TSX -> setFlags(xRegister = (byte) stackPointer);

            case TXS -> stackPointer = toUnsignedInt(xRegister);

            case TAY -> setFlags(yRegister = accumulator);

            case TYA -> setFlags(accumulator = yRegister);

            case TXA -> setFlags(accumulator = xRegister);

            case TAX -> setFlags(xRegister = accumulator);

            case PHA -> pushStack(accumulator);

            case PLA -> setFlags(accumulator = popStack());

            case SEC -> statusRegister.setCarryFlag(true);

            case SBC -> {
                byte oldAcc = accumulator;
                byte carryComplement = (byte) (statusRegister.isCarryFlagSet() ? 0 : 1);
                setFlags(accumulator = (byte) (accumulator - value(operation.operand()) - carryComplement));
                statusRegister.setCarryFlag(!statusRegister.isNegativeFlagSet());
                setOverflow(oldAcc, accumulator);
            }

            case ADC -> {
                byte oldAcc = accumulator;
                int carryValue = statusRegister.isCarryFlagSet() ? 1: 0;
                int result = accumulator + value(operation.operand()) + carryValue;
                setFlags(accumulator = (byte) (result & 0xFFFF));
                statusRegister.setCarryFlag(result > 0xFF);
                setOverflow(oldAcc, accumulator);
             }

            //Store y register
            case STY -> pokeValue(location((Operand.AddressOperand) operation.operand()), yRegister);

            //Clear carry flag
            case CLC -> statusRegister.setCarryFlag(false);

            case JMP -> jump(operation.operand());

            case BNE -> jumpIf(operation.operand(), !statusRegister.isZeroFlagSet());

            case BEQ -> jumpIf(operation.operand(), statusRegister.isZeroFlagSet());

            case BPL -> jumpIf(operation.operand(), !statusRegister.isNegativeFlagSet());

            case BMI -> jumpIf(operation.operand(), statusRegister.isNegativeFlagSet());

            case BCS -> jumpIf(operation.operand(), statusRegister.isCarryFlagSet());

            case BCC -> jumpIf(operation.operand(), !statusRegister.isCarryFlagSet());

            case BVS -> jumpIf(operation.operand(), statusRegister.isOverFlowFlagSet());

            case BVC -> jumpIf(operation.operand(), !statusRegister.isOverFlowFlagSet());

            case INX -> setFlags(++xRegister);

            case DEX -> setFlags(--xRegister);

            case INY -> setFlags(++yRegister);

            case DEY -> setFlags(--yRegister);

            case JSR -> doJsr(operation.operand());

            case RTS -> doRTS();

            case INC -> doModify((Operand.AddressOperand) operation.operand(), b -> ++b);

            case DEC -> doModify((Operand.AddressOperand) operation.operand(), b -> --b);

            case RTI -> doRTI();

            case SEI -> statusRegister.setInterruptDisableFlag(true);

            case CLI -> statusRegister.setInterruptDisableFlag(false);

            case ASL -> doModify(operation.operand(), (value) -> {
                int newValue = value << 1;
                statusRegister.setCarryFlag(newValue > 0xFF);
                return (byte) (newValue & 0xFF);
            });

            case ROL -> doModify(operation.operand(), (value) -> {
                int carryValueAsInt = toInt(statusRegister.isCarryFlagSet());
                int newValue = (value << 1) + carryValueAsInt;
                statusRegister.setCarryFlag(newValue > 0xFF);
                return (byte) (newValue & 0xFF);
            });

            case BIT -> {
                byte value = value(operation.operand());
                setFlags(value);
                statusRegister.setZeroFlag((value & accumulator) == 0);
                //Transfer bit 6 into the overflow flag
                statusRegister.setOverFlowFlag((value & 0b01000000) > 0);
            }

            case BRK -> {
                this.statusRegister.setBreakCommandFlag(true);
                //The BRK instruction takes 1 byte but increments the PC by 2
                //Since the generic handling will only increase it by 1, we
                //add an extra increment here.
                this.programCounter.increment();
                doInterruptHandling();
            }

            case CLD -> statusRegister.setDecimalModeFlag(false);

            case SED -> statusRegister.setDecimalModeFlag(true);

            case CLV -> statusRegister.setOverFlowFlag(false);

            default -> throw new UnsupportedOperationException("Not yet implemented: " + operation.opCode());
        }

        operationCount++;
    }

    private void setOverflow(byte oldValue, byte newValue) {
        //There is probably a way more efficient way to do this
        //but this follows the spec exactly
        boolean bit7Old = oldValue < 0;
        boolean bit6Old = (oldValue & 0x01000000) > 0;

        boolean bit7New = newValue < 0;
        boolean bit6New = (newValue & 0x01000000) > 0;

        //TODO IntelliJ claims the second clause is always true when reached. Why?
        boolean overflowOccurred = bit7Old != bit7New && bit6Old != bit6New;
        statusRegister.setOverFlowFlag(overflowOccurred);
    }

    private void doModify(Operand.ConcreteOperand operand, Function<Byte, Byte> modifier) {

        Consumer<Byte> postOp;

        byte originalValue = switch (operand) {
            case Operand.AddressOperand addressOperand -> {
                int location = location(addressOperand);
                postOp = (n) -> pokeValue(location, n);
                yield peekValue(location);
            }
            case Operand.NoValue nv -> {
                if (nv.addressingMode() == AddressingMode.Accumulator ) {
                    postOp = (n) -> accumulator = n;
                    yield accumulator;
                } else {
                    throw new IllegalArgumentException("Unsupported AddressingMode " + operand.addressingMode());
                }
            }

            default -> throw new IllegalArgumentException("Unsupported AddressingMode " + operand.addressingMode());
        };

        byte newValue = modifier.apply(originalValue);
        setFlags(newValue);
        postOp.accept(newValue);
    }

    private void doJsr(Operand operand) {
        pushStack(programCounter.highByte());
        pushStack(programCounter.lowByte());
        jump(operand);
    }

    private void doRTS() {
        byte lowByte = popStack();
        byte highByte = popStack();
        this.programCounter = new Operand.TwoByteAddress(lowByte, highByte);
    }

    private void jump(Operand operand) {
        jumpIf(operand, true);
    }

    private void jumpIf(Operand operand, boolean condition) {
        if (condition) {
            int jumpTo = switch (operand) {
                case Operand.AddressOperand addressOperand -> location(addressOperand);
                default -> throw new IllegalArgumentException("Unsupported operand type for jumps: " + operand.getClass().getSimpleName());
            };

            this.programCounter = address(jumpTo);
            fireEvent(new ProcessorEvent.JumpedTo(programCounter, findLabelsForLocation(programCounter)));
        }
    }

    private List<Label> findLabelsForLocation(Operand.TwoByteAddress address) {
        if (this.currentProgram == null) {
            return List.empty();
        } else {
            return currentProgram
                    .elementsForLocation(address)
                    .flatMap(element -> element instanceof Label l ? List.of(l) : List.empty());
        }
    }

    private void setFlags(byte newValue) {
        this.statusRegister.setZeroFlag(newValue == 0);
        this.statusRegister.setNegativeFlag(newValue < 0);
    }

    private int location(Operand.AddressOperand addressOperand) {
        return switch (addressOperand) {
            case Operand.OneByteAddress oneByteAddress -> {

                byte byteValue = oneByteAddress.byteValue();
                int unsignedValue = toUnsignedInt(byteValue);

                yield switch (oneByteAddress.addressingMode()) {
                    case ZeroPageAddress -> unsignedValue;
                    case ZeroPageAddressX -> unsignedValue + toUnsignedInt(xRegister);
                    case ZeroPageAddressY -> unsignedValue + toUnsignedInt(yRegister);
                    case IndirectIndexedY -> {
                        //Must be 0-paged
                        int address = unsignedValue;
                        var lowByte = peekValue(address);
                        var highByte = peekValue(address+1);

                        int calculatedOffset = littleEndianBytesToInt(lowByte, highByte);
                        int unsignedY = toUnsignedInt(yRegister);
                        yield calculatedOffset + unsignedY;
                    }
                    case IndexedIndirectX -> {
                        int address = unsignedValue;
                        var lowByte = peekValue(address + toUnsignedInt(xRegister));
                        var highByte = peekValue(address+ toUnsignedInt(xRegister) +1);

                        yield littleEndianBytesToInt(lowByte, highByte);
                    }

                    case Relative -> programCounter.toInt() + byteValue;

                    default -> throw new IllegalArgumentException(
                            String.format("Can't use %s for OneByteAddress", oneByteAddress.addressingMode())
                    );
                };
            }
            case Operand.TwoByteAddress twoByteAddress -> {
                var baseAddress = littleEndianBytesToInt(twoByteAddress.lowByte(), twoByteAddress.highByte());

                yield switch (twoByteAddress.addressingMode()) {
                    case AbsoluteAddress -> baseAddress;
                    case AbsoluteAddressX -> baseAddress + toUnsignedInt(xRegister);
                    case AbsoluteAddressY -> baseAddress + toUnsignedInt(yRegister);
                    case AbsoluteIndirect -> {
                        var lowByte = peekValue(baseAddress);
                        var highByte = peekValue(baseAddress + 1);

                        yield littleEndianBytesToInt(lowByte, highByte);
                    }
                    default -> throw new IllegalArgumentException(
                            String.format("Can't use %s for TwoByteAddress", twoByteAddress.addressingMode())
                    );
                };
            }
        };
    }

    private void fireEvent(ProcessorEvent processorEvent) {
        this.listeners.forEach(l -> l.receiveEvent(processorEvent));
    }

    public void registerEventListener(ProcessorEvent.Listener<?> listener) {
        this.listeners = this.listeners.append(listener);
    }

    public <E extends ProcessorEvent> void registerEventListener(Class<E> clazz, ProcessorEvent.Listener<E> listener) {
        registerEventListener((e) -> {
            if (clazz.isInstance(e)) {
                listener.receiveEvent((E) e);
            }
        });
    }

    private byte value(Operand operand) {
        return switch (operand) {
            case Operand.ByteValue byteValue ->  byteValue.value();
            case Operand.AddressOperand addressOperand ->  peekValue(location(addressOperand));
            case default -> throw new IllegalArgumentException("Illegal Operand: " + operand);
        };
    }

    public void pokeValue(Operand.TwoByteAddress address, byte value) {
        pokeValue(address.toInt(), value);
    }

    public void pokeValue(int location, byte value) {
        int offset = (location & 0xFFFF);
        byte oldValue = memory[offset];
        memory[offset] = value;
        fireEvent(new ProcessorEvent.MemoryLocationChanged(address(location), oldValue, value));
    }

    public byte peekValue(Operand.TwoByteAddress location) {
        return peekValue(location.toInt());
    }

    public byte peekValue(int location) {
        int offset = (location & 0xFFFF);
        return memory[offset];
    }

    public byte[] readMemory(Operand.TwoByteAddress from, Operand.TwoByteAddress to) {
        return readMemory(from.toInt(), to.toInt());
    }

    public byte[] readMemory(int from, int to) {
        int startOffset = (from & 0xFFFF);
        int endOffset = (to & 0xFFFF);

        byte[] result = new byte[endOffset-startOffset];
        System.arraycopy(memory,startOffset,result, 0, result.length);

        return result;
    }

    public void pushStack(byte value) {
        memory[stackPointer] = value;
        stackPointer--;
    }

    public byte popStack() {
        stackPointer++;
        return memory[stackPointer];
    }

    /**
     * Loads the program into the right memory location,
     * and also stores the reference
     * @param program
     */
    public void load(Program program) {
        this.currentProgram = program;
        byte[] programData = program.compile();
        int startLocation = program.startAddress().toInt();

        System.arraycopy(programData, 0, this.memory, startLocation, programData.length);
    }

    public void storeOperationAt(Operand.TwoByteAddress address, Operation operation) {
        int offset = address.toInt();
        byte[] operationBytes = operation.bytes();
        System.arraycopy(operationBytes, 0, memory, offset, operationBytes.length);
    }

    public void loadBinary(byte[] binaryProgram) {
        int startLocation = ByteUtils.littleEndianBytesToInt(binaryProgram[0], binaryProgram[1]);

        System.arraycopy(binaryProgram, 2, this.memory, startLocation, binaryProgram.length - 2);
    }

    /*
        Doing a "step"
         - Read program counter
         - Read the right amount of bytes from memory at that offset
         - Parse to an Operation
         - Execute the Operation
         - Update the Program counter
     */

    public void run(Operand.TwoByteAddress address, int maxOperationCount) {
        setProgramCounter(address);
        while (! statusRegister.isBreakCommandFlagSet() && operationCount <= maxOperationCount) {
            step();
        }
    }


    public void run(Operand.TwoByteAddress address) {
        run(address, Integer.MAX_VALUE);
    }

    public void run() {
        run(programCounter, Integer.MAX_VALUE);
    }


    public void step() {
        if (kernalRoutines.containsKey(this.programCounter)) {
            executeKernalRoutine();
        } else {
            executeOperationFromMemory();
        }
    }

    public void requestInterrupt() {
       if (!statusRegister.isInterruptDisableFlagSet()) {
           doInterruptHandling();
       }
    }

    private void doRTI() {
        statusRegister.setFrom(popStack());
        byte newLowByte = popStack();
        byte newHighByte = popStack();
        setProgramCounter(new Operand.TwoByteAddress(newLowByte, newHighByte));
    }

    private void doInterruptHandling() {
        fireEvent(new ProcessorEvent.InterruptRequest(programCounter));
        pushStack(programCounter.highByte());
        pushStack(programCounter.lowByte());
        pushStack(statusRegister.toByte());
        statusRegister.setInterruptDisableFlag(true);

        if (statusRegister.isBreakCommandFlagSet()) {
            performOperation(operation(JMP, address(0x0316).indirect()));
        } else {
            performOperation(operation(JMP, address(0x0314).indirect()));
        }
    }

    private void executeKernalRoutine() {
        JavaRoutine javaRoutine = this.kernalRoutines.get(programCounter);
        javaRoutine.execute(this);
        performOperation(javaRoutine.endWith());
    }

    private void executeOperationFromMemory() {
        var programCounterBefore = programCounter;

        byte opCode = readByte();
        OpCode.AdressingModeMapping mapping = OpCode.findByByteValue(opCode)
                .getOrElseThrow(() ->
                        new IllegalStateException("Unmappable instruction: $" + Integer.toHexString(toUnsignedInt(opCode)))
                );

        if (mapping.opCode().isIllegal()) {
            throw new IllegalArgumentException("Got illegal OpCode: " + mapping.opCode());
        }

        int bytesToRead = mapping.addressingMode().size();
        byte[] data = switch (bytesToRead) {
            case 0 -> new byte[0];
            case 1 -> new byte[]{readByte()};
            case 2 -> new byte[]{readByte(), readByte()};
            default -> throw new IllegalStateException("Illegal instruction size " + bytesToRead);
        };

        Operation operation = operation(mapping.opCode(), mapping.addressingMode().toOperand(data));
        fireEvent(new ProcessorEvent.OperationPerformed(programCounterBefore, operation));

        performOperation(operation);

        //The StatusRegister is mutable, so we put a copy in the event
        fireEvent(new ProcessorEvent.RegisterStateChangedEvent(programCounter, stackPointer, xRegister, yRegister, accumulator, statusRegister.copy()));
    }

    private byte readByte() {
        byte value = memory[this.programCounter.toInt()];
        this.programCounter = this.programCounter.increment();
        return value;
    }

    public byte getAccumulatorValue() {
        return accumulator;
    }

    public byte getXRegisterValue() {
        return xRegister;
    }

    public byte getYRegisterValue() {
        return yRegister;
    }

    public boolean isCarryFlagSet() {
        return statusRegister.isCarryFlagSet();
    }

    public boolean isNegativeFlagSet() {
        return statusRegister.isNegativeFlagSet();
    }

    public boolean isOverflowFlagSet() {
        return statusRegister.isOverFlowFlagSet();
    }

    public boolean isZeroFlagSet() {
        return statusRegister.isZeroFlagSet();
    }

    public boolean isBreakCommandFlagSet() {
        return statusRegister.isBreakCommandFlagSet();
    }

    public void setProgramCounter(Operand.TwoByteAddress address) {
        this.programCounter = address;
    }
}
