package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import io.vavr.control.Option;

import static net.nightwhistler.tddasm.mos65xx.Operand.address;

public class Processor {
    private byte accumulator;
    private byte xRegister;
    private byte yRegister;

    private boolean zeroFlag;
    private boolean negativeFlag;
    private boolean carryFlag;

    private Operand.TwoByteAddress programCounter = address(0x00);

    //64kb of memory, C64 style.
    private static int MEMORY_SIZE = (int) Math.pow(2, 16);
    private byte[] memory = new byte[MEMORY_SIZE];

    private Program currentProgram = null;

    public void performOperation(Operation operation) {
       byte value = value(operation.operand());

        switch (operation.opCode()) {
            //Load Acumulator
            case LDA -> setFlag(accumulator = value);

            //Store Accumulator
            case STA -> pokeValue(location((Operand.AddressOperand) operation.operand()), accumulator);

            //Load X register
            case LDX -> setFlag(xRegister = value);

            //Store X register
            case STX -> pokeValue(location((Operand.AddressOperand) operation.operand()), xRegister);

            //Load y register
            case LDY -> setFlag(yRegister = value);

            //Store y register
            case STY -> pokeValue(location((Operand.AddressOperand) operation.operand()), yRegister);

            //Clear carry flag
            case CLC -> carryFlag = false;
            default -> throw new UnsupportedOperationException("Not yet implemented: " + operation.opCode());
        }
    }

    private void setFlag(byte result) {
        this.zeroFlag = result == 0;
        this.negativeFlag = result < 0;
    }

    private int resolveLabel(Operand.LabelOperand labelOperand) {
        return  Option.of(currentProgram)
                .flatMap(p -> p.resolveLabel(labelOperand, this.programCounter))
                .map( addressOperand -> switch (addressOperand.addressingMode()) {
                    case AbsoluteAddress -> ((Operand.TwoByteAddress) addressOperand).toInt();
                    case AbsoluteAddressX -> ((Operand.TwoByteAddress) addressOperand).toInt() + xRegister;
                    case AbsoluteAddressY -> ((Operand.TwoByteAddress) addressOperand).toInt() + yRegister;
                    case Relative -> ((Operand.OneByteAddress) addressOperand).byteValue() + this.programCounter.toInt();
                    default -> throw new UnsupportedOperationException(String.format("Can't use AddressingMode %s for labels"));
                }) .getOrElseThrow(() -> new IllegalArgumentException("Can't resolve label " + labelOperand.label()));
    }

    private int location(Operand.AddressOperand addressOperand) {
        return switch (addressOperand) {
            case Operand.LabelOperand labelOperand -> resolveLabel(labelOperand);
            case Operand.OneByteAddress oneByteAddress -> {

                byte byteValue = oneByteAddress.byteValue();

                yield switch (oneByteAddress.addressingMode()) {
                    case ZeroPageAddress -> byteValue;
                    case ZeroPageAddressX -> byteValue + xRegister;
                    case ZeroPageAddressY -> byteValue + yRegister;
                    case IndirectIndexedY -> {
                        //Must be 0-paged
                        int address = byteValue;
                        var lowByte = peekValue(address);
                        var highByte = peekValue(address+1);

                        yield toInt(lowByte, highByte) + yRegister;
                    }
                    case IndexedIndirectX -> {
                        int address = byteValue;
                        var lowByte = peekValue(address + xRegister);
                        var highByte = peekValue(address+ xRegister +1);

                        yield toInt(lowByte, highByte);
                    }

                    default -> throw new IllegalArgumentException(
                            String.format("Can't use %s for OneByteAddress", oneByteAddress.addressingMode())
                    );
                };
            }
            case Operand.TwoByteAddress twoByteAddress -> {
                var baseAddress = toInt(twoByteAddress.lowByte(), twoByteAddress.highByte());
                yield switch (twoByteAddress.addressingMode()) {
                    case AbsoluteAddress -> baseAddress;
                    case AbsoluteAddressX -> baseAddress + xRegister;
                    case AbsoluteAddressY -> baseAddress + yRegister;
                    default -> throw new IllegalArgumentException(
                            String.format("Can't use %s for TwoByteAddress", twoByteAddress.addressingMode())
                    );
                };
            }
        };
    }

    private byte value(Operand operand) {
        return switch (operand) {
            case Operand.ByteValue byteValue ->  byteValue.value();
            case Operand.AddressOperand addressOperand ->  peekValue(location(addressOperand));
            case default -> throw new IllegalArgumentException("Illegal Operand: " + operand);
        };
    }

    private int toInt(byte lowByte, byte highByte) {
        return lowByte + (highByte << 8);
    }

    public void pokeValue(int location, byte value) {
        int offset = (location & 0x00FF);
        memory[offset] = value;
    }

    public byte peekValue(int location) {
        int offset = (location & 0x00FF);
        return memory[offset];
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

    /*
        Doing a "step"
         - Read program counter
         - Find ProgramElement at that offset if Program present (might be more than 1 if labels are present)
         - Read the right amount of bytes from memory at that offset
         - Verify against the ProgramElement, decide if execution should proceed
         - Update the Program counter
         - Execute the ProgramElement if it is an Operation

     */

    public void step() {
        if ( this.currentProgram == null ) {
            return;
        }

        List<ProgramElement> elements = this.currentProgram.elementsForLocation(this.programCounter);
        Option<Operation> op = elements.find(pe -> pe instanceof Operation)
                .map(pe -> (Operation) pe);

        op.forEach(operation -> {
            programCounter = programCounter.plus(operation.length());

            //todo verify operation

            performOperation(operation);
        });

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
        return carryFlag;
    }

    public boolean isNegativeFlagSet() {
        return negativeFlag;
    }

    public boolean isZeroFlagSet() {
        return zeroFlag;
    }

    public void setProgramCounter(Operand.TwoByteAddress address) {
        this.programCounter = address;
    }
}
