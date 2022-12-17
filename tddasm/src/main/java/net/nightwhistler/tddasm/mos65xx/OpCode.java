package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import io.vavr.control.Option;

import static net.nightwhistler.tddasm.mos65xx.AddressingMode.AbsoluteAddress;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.AbsoluteAddressX;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.AbsoluteAddressY;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.AbsoluteIndirect;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.Implied;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.IndexedIndirectX;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.IndirectIndexedY;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.Value;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.ZeroPageAddress;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.ZeroPageAddressX;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.ZeroPageAddressY;

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
    BNE {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(AddressingMode.Relative, 0xD0));
        }
    },
    BPL,
    BRK,
    BVC,
    BVS,
    CLC,
    CLD,
    CLI,
    CLV,
    CMP {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(Value, 0xC9),
                    mode(AbsoluteAddress, 0xCD),
                    mode(AbsoluteAddressX, 0xDD),
                    mode(AbsoluteAddressY, 0xD9),
                    mode(ZeroPageAddress, 0xC5),
                    mode(ZeroPageAddressX, 0xD5),
                    mode(IndexedIndirectX, 0xC1),
                    mode(IndirectIndexedY, 0xD1)
            );
        }
    },
    CPX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(Value, 0xE0),
                    mode(AbsoluteAddress, 0xEC),
                    mode(ZeroPageAddress, 0xE4)
            );
        }
    },
    CPY,
    DCP,
    DEC,
    DEX,
    DEY,
    EOR,
    INC,
    INX {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(Implied, 0xE8));
        }
    },
    INY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(Implied, 0xC8));
        }
    },
    ISC,
    JAM,
    JMP {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(AbsoluteAddress, 0x4C),
                    mode(AbsoluteIndirect, 0x6C)
            );
        }
    },
    JSR {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(AbsoluteAddress, 0x20));
        }
    },
    LAS,
    LAX,
    LDA {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(Value, 0xA9),
                    mode(AbsoluteAddress, 0xAD),
                    mode(AbsoluteAddressX, 0xBD),
                    mode(AbsoluteAddressY, 0xB9),
                    mode(ZeroPageAddress, 0xA5),
                    mode(ZeroPageAddressX, 0xB5),
                    mode(IndexedIndirectX, 0xA1),
                    mode(IndirectIndexedY, 0xB1)
            );
        }
    },
    LDX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(Value, 0xA2),
                    mode(AbsoluteAddress, 0xAE),
                    mode(AbsoluteAddressY, 0xBE),
                    mode(ZeroPageAddress, 0xA6),
                    mode(ZeroPageAddressY, 0xB6)
            );
        }
    },
    LDY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(Value, 0xA0),
                    mode(AbsoluteAddress, 0xAC),
                    mode(AbsoluteAddressX, 0xBC),
                    mode(ZeroPageAddress, 0xA4),
                    mode(ZeroPageAddressX, 0xB4)
            );
        }
    },
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
    RTS {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(Implied, 0x60));
        }
    },
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
    STX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(AbsoluteAddress, 0x8E),
                    mode(ZeroPageAddress, 0x86),
                    mode(ZeroPageAddressY, 0x96)
            );
        }
    },

    STY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(AbsoluteAddress, 0x8C),
                    mode(ZeroPageAddress, 0x84),
                    mode(ZeroPageAddressX, 0x94)
            );
        }
    },
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

    public boolean supportAddressingMode(AddressingMode addressingMode) {
        return addressingModeMappings().map(AdressingModeMapping::addressingMode)
                .contains(addressingMode);
    }

    private static AdressingModeMapping mode(AddressingMode addressingMode, int byteValue) {
        return new AdressingModeMapping(addressingMode, (byte) byteValue);
    }

    public List<AdressingModeMapping> addressingModeMappings() {
        throw new UnsupportedOperationException(String.format("OpCode %s has no addressing modes. This means it's either illegal or not yet implemented.", this));
    }

    record AdressingModeMapping(AddressingMode addressingMode, byte code){}
}
