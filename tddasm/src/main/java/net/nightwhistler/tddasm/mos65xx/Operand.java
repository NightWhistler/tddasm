package net.nightwhistler.tddasm.mos65xx;

import io.vavr.control.Option;
import net.nightwhistler.ByteUtils;

import java.util.function.Function;

import static net.nightwhistler.ByteUtils.highByte;
import static net.nightwhistler.ByteUtils.littleEndianBytesToInt;
import static net.nightwhistler.ByteUtils.lowByte;

public sealed interface Operand {
    AddressingMode addressingMode();

    int length();

    /**
     * A ConcreteOperand is an Operand that has all the needed
     * information to be compiled into bytes.
     */
    sealed interface ConcreteOperand extends Operand {
        default byte[] bytes() {
            return new byte[0];
        }

        @Override
        default int length() {
            return bytes().length;
        }
    }

    /**
     * A VirtualOperand is an Operand that first
     * needs to be reolved into a ConcreteOperand
     * before it can be compiled. This is mostly
     * meant for labels.
     */
    sealed interface VirtualOperand extends Operand {

        /**
         * Resolves this VirtualOperand into a concrete one.
         *
         * This generally means translating a label into an address.
         *
         * @param program the Program to resolve in
         * @param offset the offset inside this program to resolve relative to.
         * @return a ConcreteOperand which can be compiled.
         */
        ConcreteOperand resolve(Program program, TwoByteAddress offset);
    }

    sealed interface AddressOperand extends ConcreteOperand {
        byte highByte();
        byte lowByte();

        AddressOperand plus(int value);

        AddressOperand minus(int value);
    }

    record NoValue(AddressingMode addressingMode) implements ConcreteOperand {
        public NoValue() {
            this(AddressingMode.Implied);
        }

        public String toString() {
            return "";
        }
    }

    record ByteValue(byte value) implements ConcreteOperand {
        @Override
        public AddressingMode addressingMode() {
            return AddressingMode.Value;
        }

        @Override
        public byte[] bytes() {
            return new byte[]{ value };
        }

        @Override
        public String toString() {
            return "#$" + Integer.toHexString(Byte.toUnsignedInt(value));
        }
    }

    record OneByteAddress(AddressingMode addressingMode, byte byteValue) implements AddressOperand {
        public OneByteAddress(byte byteValue) {
            this(AddressingMode.ZeroPageAddress, byteValue);
        }

        public OneByteAddress xIndexed() {
            return new OneByteAddress(AddressingMode.ZeroPageAddressX, byteValue);
        }

        public OneByteAddress yIndexed() {
            return new OneByteAddress(AddressingMode.ZeroPageAddressY, byteValue);
        }

        @Override
        public AddressOperand plus(int value) {
            return new OneByteAddress(addressingMode, (byte) (byteValue + value));
        }

        @Override
        public AddressOperand minus(int value) {
            return new OneByteAddress(addressingMode, (byte) (byteValue - value));
        }

        public OneByteAddress indirectIndexedY() {
            return new OneByteAddress(AddressingMode.IndirectIndexedY, byteValue);
        }

        public byte highByte() {
            return 0;
        }

        @Override
        public byte lowByte() {
            return byteValue;
        }

        @Override
        public byte[] bytes() {
            return new byte[]{ byteValue };
        }

        @Override
        public String toString() {
            String base = "$" + Integer.toHexString(Byte.toUnsignedInt(byteValue));
            return switch (addressingMode) {
                case ZeroPageAddress, Relative -> base;
                case ZeroPageAddressX -> base + ",X";
                case ZeroPageAddressY -> base + ",Y";
                case IndirectIndexedY -> String.format("(%s),Y", base);
                case IndexedIndirectX -> String.format("(%s,X)", base);
                default -> throw new IllegalArgumentException("Unsupported addressingmode " + addressingMode);
            };
        }
    }

    record TwoByteAddress(AddressingMode addressingMode, byte lowByte, byte highByte) implements AddressOperand {
        //FIXME: This is used a lot, maybe make a separate Address type
        //Then have AddressOperand be a record wrapping an Address and an AddressingMode

        public TwoByteAddress(byte lowByte, byte highByte) {
            this(AddressingMode.AbsoluteAddress, lowByte, highByte);
        }

        public TwoByteAddress xIndexed() {
            return withAddressingMode(AddressingMode.AbsoluteAddressX);
        }

        public TwoByteAddress yIndexed() {
            return withAddressingMode(AddressingMode.AbsoluteAddressY);
        }

        public TwoByteAddress indirect() {
            return withAddressingMode(AddressingMode.AbsoluteIndirect);
        }

        public TwoByteAddress withAddressingMode(AddressingMode addressingMode) {
            return new TwoByteAddress(addressingMode, lowByte, highByte);
        }

        @Override
        public byte[] bytes() {
            return new byte[]{ lowByte, highByte };
        }

        public int toInt() {
            return littleEndianBytesToInt(lowByte, highByte);
        }

        public TwoByteAddress plus(int offset) {
            int newAddress = toInt() + offset;
            return new TwoByteAddress(this.addressingMode, ByteUtils.lowByte(newAddress), ByteUtils.highByte(newAddress));
        }

        public TwoByteAddress minus(int offset) {
            int newAddress = toInt() - offset;
            return new TwoByteAddress(this.addressingMode, ByteUtils.lowByte(newAddress), ByteUtils.highByte(newAddress));
        }

        public TwoByteAddress increment() {
            return plus(1);
        }

        @Override
        public String toString() {
            String base = "$" + Integer.toHexString(toInt());
            return switch (addressingMode) {
                case AbsoluteAddress -> base;
                case AbsoluteAddressX -> base + ",X";
                case AbsoluteAddressY -> base + ",Y";
                case AbsoluteIndirect -> String.format("(%s)", base);
                default -> throw new IllegalArgumentException("Unsupported addressingmode " + addressingMode);
            };
        }
    }

    record LabelTransformation(LabelOperand labelOperand, Function<AddressOperand, ConcreteOperand> transformation,
                               int length, String stringRepresentation)
            implements VirtualOperand {

        @Override
        public AddressingMode addressingMode() {
            return labelOperand.addressingMode();
        }

        @Override
        public ConcreteOperand resolve(Program program, TwoByteAddress offset) {
            return transformation.apply(labelOperand.resolve(program, offset));
        }

    }

    record LabelOperand(String label, AddressingMode addressingMode) implements VirtualOperand {
        public LabelOperand(String label) {
            this(label, AddressingMode.AbsoluteAddress);
        }

        public LabelOperand xIndexed() {
            return new LabelOperand(label, AddressingMode.AbsoluteAddressX);
        }

        public LabelOperand yIndexed() {
            return new LabelOperand(label, AddressingMode.AbsoluteAddressY);
        }

        public VirtualOperand lowByte() {
            return new LabelTransformation(this, address -> value(address.lowByte()),
                    1, "#<" + label);
        }

        public VirtualOperand highByte() {
            return new LabelTransformation(this, address -> value(address.highByte()),
                    1, "#>" + label);
        }

        public VirtualOperand plus(int value) {
            return new LabelTransformation(this, address -> address.plus(value),
                    1, "#>" + label);
        }

        public VirtualOperand minus(int value) {
            return new LabelTransformation(this, address -> address.minus(value),
                    1, "#>" + label);
        }


        @Override
        public int length() {
            return switch (addressingMode()) {
                case Relative -> 1;
                default -> 2;
            };
        }

        @Override
        public AddressOperand resolve(Program program, TwoByteAddress offset) {
            return Option.of(program).flatMap(p -> p.resolveLabel(this, offset))
                    .getOrElseThrow(() -> new IllegalArgumentException("Could not find label " + label));
        }

        @Override
        public String toString() {
            return switch (addressingMode) {
                case AbsoluteAddressX -> label + ",X";
                case AbsoluteAddressY -> label + ",Y";
                default -> label + "(" + addressingMode + ")";
            };
        }
    }

    static LabelOperand label(String label) {
        return new LabelOperand(label);
    }

    static ByteValue value(int value) {
        return new ByteValue((byte) value);
    }

    static TwoByteAddress address(int value) {
        byte lowByte = lowByte(value);
        byte highByte = highByte(value);

        return new TwoByteAddress(AddressingMode.AbsoluteAddress, lowByte, highByte);
    }

    static OneByteAddress zeroPage(int value) {
        return new OneByteAddress(AddressingMode.ZeroPageAddress, (byte) value);
    }

    static NoValue noValue() {
        return new NoValue();
    }

    static NoValue noValue(AddressingMode addressingMode) {
        return new NoValue(addressingMode);
    }

    static NoValue implied() {
        return new NoValue(AddressingMode.Implied);
    }

    static NoValue accumulator() {
        return new NoValue(AddressingMode.Accumulator);
    }

}
