package net.nightwhistler.tddasm.mos65xx;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;

import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

public record Program(Operand.TwoByteAddress startAddress, List<ProgramElement> elements) {
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
            int diff = a.toInt() - toAddress.toInt();
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

    /**
     * Returns the ProgramElements associated with a specific memory location (if any)
     * @param location
     * @return
     */
    public List<ProgramElement> elementsForLocation(Operand.TwoByteAddress location) {
        return offsets().filter(t -> t._1.equals(location))
                .map(Tuple2::_2);
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
            if ( element instanceof Operation op && op.operand() instanceof Operand.LabelOperand lo) {
                OpCode opCode = op.opCode();
                var resolvedAddress = resolveLabel(lo, absoluteOffset);
                elementData = resolvedAddress
                        .map(a -> new Operation(opCode, a).bytes())
                        .getOrElseThrow(
                                () -> new IllegalStateException("Cannot resolve label " + lo.label())
                        );

            } else {
                elementData = element.bytes();
            }

            int offsetInternal = absoluteOffset.toInt() - startAddress.toInt();
            System.arraycopy(elementData, 0, result, offsetInternal, elementData.length);
        }

        return result;
    }
}
