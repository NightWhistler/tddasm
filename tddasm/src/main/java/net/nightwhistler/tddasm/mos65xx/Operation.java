package net.nightwhistler.tddasm.mos65xx;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Try;

import static net.nightwhistler.ByteUtils.JAVA_BYTE_0_MASK;
import static net.nightwhistler.ByteUtils.JAVA_BYTE_1_MASK;


public record Operation(OpCode opCode, Operand operand) implements ProgramElement {

    public AddressingMode addressingMode() {
        return operand.addressingMode();
    }

    public Operation {
        if (!opCode.supportAddressingMode(operand.addressingMode())) {
            throw new IllegalArgumentException(
                    String.format("Opcode %s does not support AddressingMode %s", opCode, operand.addressingMode()));
        }
    }

    public static Operation operation(OpCode opCode) {
        return new Operation(opCode, Operand.noValue());
    }

    public static Operation operation(OpCode opCode, String label) {
        return new Operation(opCode, new Operand.LabelOperand(label));
    }

    public static Operation operation(OpCode opCode, Operand operand) {
        return new Operation(opCode, operand);
    }

    public byte[] toBytes(AddressingMode addressingMode, byte... value) {
        byte firstByte = opCode.codeForAddressingMode(addressingMode).getOrElseThrow(
                () -> new IllegalArgumentException("Unsupported addressingmode: " + addressingMode + " for opcode " + opCode)
        );

        byte[] result = new byte[value.length+1];
        result[0] = firstByte;
        System.arraycopy(value, 0, result, 1, value.length);

        return result;
    }

    public static Operation fromBytes(byte... bytes) {
        byte firstByte = bytes[0];
        var maybeMapping= List.of(OpCode.values())
                .flatMap(o -> Try.of(() -> o.addressingModeMappings()).getOrElse(List.empty()).map(m -> new Tuple2<>(o, m)))
                .filter(t -> t._2.code() == firstByte);

        return maybeMapping.map(tuple -> {
            OpCode opCode = tuple._1;
            AddressingMode addressingMode = tuple._2.addressingMode();
            if (bytes.length == 2) {
              return new Operation(opCode, new Operand.OneByteAddress(addressingMode, bytes[1]));
            } else {
               return new Operation(opCode, new Operand.TwoByteAddress(addressingMode, bytes[1], bytes[2]));
            }
        }).getOrElseThrow(() -> new UnsupportedOperationException("Cannot map byte-value: " + Integer.toHexString(firstByte)));

    }
}
