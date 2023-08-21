package net.nightwhistler.tddasm.mos65xx;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;
import net.nightwhistler.ByteUtils;

import java.io.PrintWriter;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static net.nightwhistler.ByteUtils.bytes;
import static net.nightwhistler.tddasm.mos65xx.OpCode.JMP;
import static net.nightwhistler.tddasm.mos65xx.OpCode.JSR;
import static net.nightwhistler.tddasm.mos65xx.Operand.address;

public record Program(Operand.TwoByteAddress startAddress, List<ProgramElement> elements) {

    public Program(List<ProgramElement> elements) {
        this(address(0xC000), elements);
    }

    public Program withBASICStarter() {
        var basicElement = new Data(bytes(
                0x0C,0x08,0x40,0x00,0x9E,0x20,0x32,0x30,0x36,0x32,0x00,0x00,0x00)
        );

        //Fix it into the BASIC space
        return new Program(address(0x801), elements.prepend(basicElement));
    }

    public Option<Operand.TwoByteAddress> resolveLabelAbsolute(String label) {
        int index = elements.indexOf(new Label(label));
        if ( index == -1 ) {
            return none();
        } else {
            return some(addressOfElement(index));
        }
    }

    public Option<Byte> resolveLabelRelativeTo(String label, Operand.TwoByteAddress toAddress) {
        return resolveLabelAbsolute(label).map( a -> {

            int diff = (a.toInt() - toAddress.toInt());
            if ( diff < -128 || diff > 128 ) {
                throw new IllegalArgumentException("Address difference must be between -128 and 128");
            }

            return (byte) diff;
        });
    }

    /**
     * Resolves a label to an address, if it exists
     * @param labelOperand the label to resolve
     * @param offset the location to resolve relative to for Relative Addressing
     * @return The resolved address or none()
     */
    public Option<? extends Operand.AddressOperand> resolveLabel(Operand.LabelOperand labelOperand, Operand.TwoByteAddress offset) {
        return switch (labelOperand.addressingMode()) {

            case AbsoluteAddress -> resolveLabelAbsolute(labelOperand.label());

            case AbsoluteAddressX -> resolveLabelAbsolute(labelOperand.label()).map(Operand.TwoByteAddress::xIndexed);

            case AbsoluteAddressY -> resolveLabelAbsolute(labelOperand.label()).map(Operand.TwoByteAddress::yIndexed);

            case Relative -> resolveLabelRelativeTo(labelOperand.label(), offset)
                    .map(b -> new Operand.OneByteAddress(AddressingMode.Relative, b));

            default -> throw new IllegalStateException(
                    String.format("Invalid use of AddressingMode %s with for label", labelOperand.addressingMode()));
        };

    }

    private List<Tuple2<Operand.TwoByteAddress, ProgramElement>> offsets() {
        if (elements.isEmpty()) {
            return List.empty();
        }

        var headElement = elements.head();
        return elements.tail().foldLeft(List.of(new Tuple2<>(startAddress, headElement)),
                (folded, e) -> {
                    var lastElement = folded.last();
                    var offset = lastElement._1.plus(lastElement._2.length());
                    return folded.append(new Tuple2<>(offset, e));
                });
    }

    public Operand.TwoByteAddress addressOfElement(int elementIndex) {
        return offsets().get(elementIndex)._1;
    }

    public Option<Operand.TwoByteAddress> addressOfElement(ProgramElement programElement) {
        return offsets().find(o -> o._2.equals(programElement)).map(Tuple2::_1);
    }

    /**
     * Returns the ProgramElements associated with a specific memory location (if any)
     * @param location
     * @return
     */
    public List<ProgramElement> elementsForLocation(Operand.TwoByteAddress location) {
        return elementsForLocation(location, offsets());
    }

    private List<ProgramElement> elementsForLocation(Operand.TwoByteAddress location,
                                                     List<Tuple2<Operand.TwoByteAddress, ProgramElement>> offsets ) {
        return offsets.filter(t -> t._1.equals(location))
                .map(Tuple2::_2);
    }

    public void printASM(PrintWriter printWriter, boolean includeAddresses) {
        printWriter.println("*=" + startAddress);
        var offsets = offsets();

        for (var tuple: offsets) {
            var address = tuple._1;
            var element = tuple._2;

            if (includeAddresses) {
                printWriter.print(address + " ");
            }

            if (element instanceof Label l) {
                printWriter.println(l);
            } else if (element instanceof OperationProvider op){
                printWriter.println("  " + op.provide(this, startAddress));
            } else {
                printWriter.println("  " + element);
            }
        }

        printWriter.flush();

    }

    private static boolean shouldAddLabel(Program input, Operand.TwoByteAddress address, List<Tuple2<Operand.TwoByteAddress, ProgramElement>> offsets) {
        var elementsForLocation = input.elementsForLocation(address, offsets);

        if (elementsForLocation.isEmpty()) {
            return false; //Jump outside the program
        }

        return elementsForLocation.filter(e -> e instanceof Label).isEmpty();
    }

    private static final List<OpCode> relativeJumps =
            List.of(OpCode.values())
                    .filter(o -> ! o.isIllegal())
                    .flatMap(o -> o.addressingModeMappings())
                    .filter(m -> m.addressingMode() == AddressingMode.Relative)
                    .map(m -> m.opCode());

    private static Program generateLabels(Program input) {
        Program withLabels = input;
        int labelCounter = 0;
        int subRoutineCounter = 0;

        var offsets = input.offsets();

        for (var offset: offsets) {
            String lastLabel = null;

            if (offset._2 instanceof OperationProvider op) {
                if (op.opCode() == JSR && op.operand() instanceof Operand.TwoByteAddress address) {
                    if (shouldAddLabel(withLabels, address, offsets)) {
                        lastLabel = "subroutine_" + subRoutineCounter++;
                        withLabels = withLabels.addLabel(
                                address,
                                lastLabel,
                                offsets
                                );
                        offsets = withLabels.offsets();
                    }
                } else if (relativeJumps.contains(op.opCode()) && op.operand() instanceof Operand.OneByteAddress address) {
                    Operand.TwoByteAddress dest = offset._1.plus(address.lowByte());
                    if (shouldAddLabel(withLabels, dest, offsets)) {
                        lastLabel = "jump_dest_" + labelCounter++;
                        withLabels = withLabels.addLabel(
                                dest,
                                lastLabel,
                                offsets
                        );
                        offsets = withLabels.offsets();
                    }
                } else if (op.opCode() == JMP && op.operand() instanceof Operand.TwoByteAddress address) {
                    if (shouldAddLabel(withLabels, address, offsets)) {
                        lastLabel = "jump_dest_" + labelCounter++;
                        withLabels = withLabels.addLabel(
                                address,
                                lastLabel,
                                offsets
                        );
                        offsets = withLabels.offsets();
                    }
                }

//                if (lastLabel != null) {
//                    withLabels = new Program(withLabels.startAddress,
//                            withLabels.elements().replace(op, new OperationProvider(op.opCode(), new Operand.LabelOperand(lastLabel, op.operand().addressingMode()))
//                            ));
//                }
            }
        }

        return withLabels;
    }

    public Program generateLabels() {
        return generateLabels(this);
    }

    public Program addLabel(Operand.TwoByteAddress address, String label) {
       return addLabel(address, label, offsets());
    }

    public Program addLabel(Operand.TwoByteAddress address, String label, List<Tuple2<Operand.TwoByteAddress, ProgramElement>> offsets) {
        int index = offsets.indexWhere(o -> o._1.equals(address));
        if (index == -1 ) {
            throw new IllegalArgumentException("Address not found: " + address);
        }

        List<ProgramElement> beforeElement = elements.slice(0, index);
        List<ProgramElement> afterElement = elements.slice(index, elements.length());
        Label labelElement = new Label(label);

        return new Program(startAddress,
                beforeElement.append(labelElement).appendAll(afterElement));
    }

    /**
     * Compiles the program, but doesn't add the startAddress in the first 2 bytes
     * @return
     */
    public byte[] compile() {
        int length = elements.foldLeft(0, (count, e) -> count + e.length());
        byte[] result = new byte[length];

        var offsets = offsets();

        for ( int i=0; i < elements.length(); i++ ) {

            Operand.TwoByteAddress absoluteOffset = offsets.get(i)._1;
            byte[] elementData;

            ProgramElement element = elements.get(i);
            if ( element instanceof OperationProvider operationProvider) {
                //All relative values are calculated with the offset _after_ the instruction
                Operation op = operationProvider.provide(this, absoluteOffset.plus(operationProvider.length()));
                elementData = op.bytes();
            } else if (element instanceof ProgramElement.BytesElement bytesElement){
                elementData = bytesElement.bytes();
            } else {
                elementData = new byte[0];
            }

            int offsetInternal = absoluteOffset.toInt() - startAddress.toInt();
            System.arraycopy(elementData, 0, result, offsetInternal, elementData.length);
        }

        return result;
    }

    public static Program fromBinary(byte[] binary) {
        int startAddress = ByteUtils.littleEndianBytesToInt(binary[0], binary[1]);
        List<ProgramElement> operations = List.empty();

        int counter = 2;

        while (counter < binary.length) {
            byte opCodeByte = binary[counter++];
            var maybeMapping = OpCode.findByByteValue(opCodeByte);

            if (maybeMapping.isDefined()) {
                var mapping = maybeMapping.get();

                int length = mapping.addressingMode().size();
                byte[] bytes = new byte[length];
                System.arraycopy(binary, counter, bytes, 0, bytes.length);
                counter += length;

                operations = operations.append(new OperationProvider(mapping.opCode(), mapping.addressingMode().toOperand(bytes)));
            } else {
                if (!operations.isEmpty() && operations.last() instanceof Data lastData) {
                    byte[] newData = new byte[lastData.bytes().length +1];
                    System.arraycopy(lastData.bytes(), 0, newData, 0, lastData.length());
                    newData[lastData.length()] = opCodeByte;
                    operations = operations.slice(0, operations.length() -1).append(new Data(newData));
                } else {
                    operations = operations.append(new Data(new byte[]{opCodeByte}));
                }
            }
        }

        return generateLabels(
                new ProgramBuilder()
                .include(operations)
                .buildProgram(address(startAddress)
                ));
    }

}
