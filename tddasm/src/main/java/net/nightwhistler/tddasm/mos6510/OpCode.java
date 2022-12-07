package net.nightwhistler.tddasm.mos6510;

import io.vavr.Tuple2;
import io.vavr.collection.List;

import static net.nightwhistler.tddasm.mos6510.AddressingMode.AbsoluteAddressX;
import static net.nightwhistler.tddasm.mos6510.AddressingMode.AbsoluteAddressY;
import static net.nightwhistler.tddasm.mos6510.AddressingMode.IndexedIndirectX;
import static net.nightwhistler.tddasm.mos6510.AddressingMode.IndirectIndexedY;
import static net.nightwhistler.tddasm.mos6510.AddressingMode.ZeroPageAddress;
import static net.nightwhistler.tddasm.mos6510.AddressingMode.ZeroPageAddressX;

/**
 * All the MOS6510 Opcodes, including illegal codes
 *
 */
public enum OpCode {
    ADC,
    ALR,
    ANC,
    AND,
    ANE,
    ARR,
    ASL,
    BCC,
    BCS,
    BEQ,
    BIT,
    BMI,
    BNE,
    BPL,
    BRK,
    BVC,
    BVS,
    CLC,
    CLD,
    CLI,
    CLV,
    CMP,
    CPX,
    CPY,
    DCP,
    DEC,
    DEX,
    DEY,
    EOR,
    INC,
    INX,
    INY,
    ISC,
    JAM,
    JMP,
    JSR,
    LAS,
    LAX,
    LDA,
    LDX,
    LDY,
    LSR,
    NOP,
    ORA,
    PHA,
    PHP,
    PLA,
    PLP,
    RLA,
    ROL,
    ROR,
    RRA,
    RTI,
    RTS,
    SAX,
    SBC,
    SBX,
    SEC,
    SED,
    SEI,
    SHA,
    SHX,
    SHY,
    SLO,
    SRE,
    STA {
        @Override
        public List<Tuple2<AddressingMode, Byte>> addressingModeMappings() {
            return List.of(
                    mode(AddressingMode.AbsoluteAddress, 0x0D),
                    mode(AbsoluteAddressX,0x9D),
                    mode(AbsoluteAddressY, 0x99),
                    mode(ZeroPageAddress , 0x85),
                    mode(ZeroPageAddressX , 0x95),
                    mode(IndexedIndirectX , 0x81),
                    mode(IndirectIndexedY , 0x91)
            );
        }
    },
    STX,
    STY,
    TAS,
    TAX,
    TAY,
    TSX,
    TXA,
    TXS,
    TYA;

    public byte[] toBytes(AddressingMode addressingMode, byte... value) {
        return new byte[0];
    }

    private static Tuple2<AddressingMode, Byte> mode(AddressingMode addressingMode, int byteValue) {
        return new Tuple2<>(addressingMode, (byte) byteValue);
    }

    public List<Tuple2<AddressingMode, Byte>> addressingModeMappings() {
        return List.empty();
    }
}
