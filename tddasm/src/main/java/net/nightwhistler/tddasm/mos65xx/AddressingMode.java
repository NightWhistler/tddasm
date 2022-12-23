package net.nightwhistler.tddasm.mos65xx;

import java.util.function.Function;
import java.util.function.Supplier;

import static net.nightwhistler.tddasm.mos65xx.Operand.noValue;

public enum AddressingMode {
    Implied(0) {
        @Override
        public Operand.ConcreteOperand toOperand(byte... values) {
            return createOperand(values, size(), (b) -> Operand.noValue());
        };
    },
    Value(1) {
        @Override
        public Operand.ConcreteOperand toOperand(byte... values) {
            return createOperand(values, size(), (b) -> Operand.value(b[0]));
        };
    },
    AbsoluteAddress(2) {
        @Override
        public Operand.ConcreteOperand toOperand(byte... values) {
            return createOperand(values, size(), (b) -> new Operand.TwoByteAddress(this, b[0], b[1]));
        };
    },
    AbsoluteAddressX(2) {
        public Operand.ConcreteOperand toOperand(byte... values) {
            return createOperand(values, size(), (b) -> new Operand.TwoByteAddress(this, b[0], b[1]));
        };
    },
    AbsoluteAddressY(2) {
        public Operand.ConcreteOperand toOperand(byte... values) {
            return createOperand(values, size(), (b) -> new Operand.TwoByteAddress(this, b[0], b[1]));
        };
    },
    AbsoluteIndirect(2) {
        public Operand.ConcreteOperand toOperand(byte... values) {
            return createOperand(values, size(), (b) -> new Operand.TwoByteAddress(this, b[0], b[1]));
        };
    },
    ZeroPageAddress(1) {
        public Operand.ConcreteOperand toOperand(byte... values) {
            return createOperand(values, size(), (b) -> new Operand.OneByteAddress(this, b[0]));
        };
    },
    ZeroPageAddressX(1) {
        public Operand.ConcreteOperand toOperand(byte... values) {
            return createOperand(values, size(), (b) -> new Operand.OneByteAddress(this, b[0]));
        };
    },
    ZeroPageAddressY(1) {
        public Operand.ConcreteOperand toOperand(byte... values) {
            return createOperand(values, size(), (b) -> new Operand.OneByteAddress(this, b[0]));
        };
    },
    IndexedIndirectX(1) {
        public Operand.ConcreteOperand toOperand(byte... values) {
            return createOperand(values, size(), (b) -> new Operand.OneByteAddress(this, b[0]));
        };
    },
    IndirectIndexedY(1) {
        public Operand.ConcreteOperand toOperand(byte... values) {
            return createOperand(values, size(), (b) -> new Operand.OneByteAddress(this, b[0]));
        };
    },
    Relative(1) {
        public Operand.ConcreteOperand toOperand(byte... values) {
            return createOperand(values, size(), (b) -> new Operand.OneByteAddress(this, b[0]));
        };
    };

    private int size;

    private AddressingMode(int size) {
        this.size = size;
    }

    public abstract Operand.ConcreteOperand toOperand(byte... values);

    private static Operand.ConcreteOperand createOperand(byte[] values, int size, Function<byte[], Operand.ConcreteOperand> supplier) {
        if (values.length != size) {
            throw new IllegalArgumentException(
                    String.format("Mismatch in argument length and size. Expected %d, got %d",
                            size, values.length)
            );
        }

        return supplier.apply(values);
    }

    /**
     * How many bytes to read for an Operation of this mode
     * @return
     */
    public int size() {
        return size;
    }
}
