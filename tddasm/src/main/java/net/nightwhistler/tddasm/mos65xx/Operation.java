package net.nightwhistler.tddasm.mos65xx;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import net.nightwhistler.ByteUtils;

import static net.nightwhistler.ByteUtils.JAVA_BYTE_0_MASK;
import static net.nightwhistler.ByteUtils.JAVA_BYTE_1_MASK;


public record Operation(OpCode opCode, AddressingMode addressingMode, byte... values) implements ProgramElement {

    public Operation {
        if (!opCode.supportAddressingMode(addressingMode)) {
            throw new IllegalArgumentException(
                    String.format("Opcode %s does not support AddressingMode %s", opCode, addressingMode));
        }
    }

    public static Operation operation(OpCode opCode) {
        return new Operation(opCode, AddressingMode.Implied, new byte[0]);
    }

    public static Operation operation(OpCode opCode, AddressingMode addressingMode, int value) {
        //Single byte
        if ( (value & JAVA_BYTE_0_MASK) == value) {
            return new Operation(opCode, addressingMode, new byte[] { (byte) value});
        } else {
            byte lowByte = (byte) (value & JAVA_BYTE_0_MASK);
            byte highByte = (byte) (value & JAVA_BYTE_1_MASK);

            return new Operation(opCode, addressingMode, new byte[] { lowByte, highByte });
        }
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
                .flatMap(o -> o.addressingModeMappings().map(m -> new Tuple2<>(o, m)))
                .filter(t -> t._2.code() == firstByte);

        return maybeMapping.map(tuple -> {
            OpCode opCode = tuple._1;
            AddressingMode addressingMode = tuple._2.addressingMode();
            if (bytes.length == 2) {
              return new Operation(opCode, addressingMode, bytes[1]);
            } else {
               return new Operation(opCode, addressingMode, bytes[1], bytes[2]);
            }
        }).getOrElseThrow(() -> new UnsupportedOperationException("Cannot map byte-value: " + Integer.toHexString(firstByte)));

    }
}
