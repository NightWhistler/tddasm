package net.nightwhistler.tddasm.mos65xx;

import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;

import static net.nightwhistler.tddasm.mos65xx.AddressingMode.AbsoluteAddress;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.AbsoluteAddressX;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.AbsoluteAddressY;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.IndexedIndirectX;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.IndirectIndexedY;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.ZeroPageAddress;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.ZeroPageAddressX;

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
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(AbsoluteAddress, 0x8D),
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

    public Option<Byte> codeForAddressingMode(AddressingMode addressingMode) {
        return addressingModeMappings()
                .find(m -> m.addressingMode == addressingMode)
                .map(AdressingModeMapping::code);
    }

    private static AdressingModeMapping mode(AddressingMode addressingMode, int byteValue) {
        return new AdressingModeMapping(addressingMode, (byte) byteValue);
    }

    public List<AdressingModeMapping> addressingModeMappings() {
        return List.empty();
    }

    record AdressingModeMapping(AddressingMode addressingMode, byte code){}
}
