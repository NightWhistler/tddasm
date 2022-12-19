package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;

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
    BEQ {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,AddressingMode.Relative, 0xF0));
        }
    },
    BIT,
    BMI,
    BNE {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,AddressingMode.Relative, 0xD0));
        }
    },
    BPL,
    BRK {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this, Implied, 0x00));
        }
    },
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
                    mode(this,Value, 0xC9),
                    mode(this,AbsoluteAddress, 0xCD),
                    mode(this,AbsoluteAddressX, 0xDD),
                    mode(this,AbsoluteAddressY, 0xD9),
                    mode(this,ZeroPageAddress, 0xC5),
                    mode(this,ZeroPageAddressX, 0xD5),
                    mode(this,IndexedIndirectX, 0xC1),
                    mode(this,IndirectIndexedY, 0xD1)
            );
        }
    },
    CPX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,Value, 0xE0),
                    mode(this,AbsoluteAddress, 0xEC),
                    mode(this,ZeroPageAddress, 0xE4)
            );
        }
    },
    CPY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,AddressingMode.Value, 0xC0),
                    mode(this, AbsoluteAddress, 0xCC),
                    mode(this, ZeroPageAddress, 0xC4)
                );
        }
    },
    DCP,
    DEC,
    DEX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,Implied, 0xCA));
        }
    },
    DEY,
    EOR,
    INC {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,AbsoluteAddress, 0xEE),
                    mode(this,AbsoluteAddressX, 0xFE),
                    mode(this,ZeroPageAddress, 0xE6),
                    mode(this,ZeroPageAddressX, 0xF6)
            );
        }
    },
    INX {
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,Implied, 0xE8));
        }
    },
    INY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,Implied, 0xC8));
        }
    },
    ISC,
    JAM,
    JMP {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,AbsoluteAddress, 0x4C),
                    mode(this,AbsoluteIndirect, 0x6C)
            );
        }
    },
    JSR {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(mode(this,AbsoluteAddress, 0x20));
        }
    },
    LAS,
    LAX,
    LDA {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,Value, 0xA9),
                    mode(this,AbsoluteAddress, 0xAD),
                    mode(this,AbsoluteAddressX, 0xBD),
                    mode(this,AbsoluteAddressY, 0xB9),
                    mode(this,ZeroPageAddress, 0xA5),
                    mode(this,ZeroPageAddressX, 0xB5),
                    mode(this,IndexedIndirectX, 0xA1),
                    mode(this,IndirectIndexedY, 0xB1)
            );
        }
    },
    LDX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,Value, 0xA2),
                    mode(this,AbsoluteAddress, 0xAE),
                    mode(this,AbsoluteAddressY, 0xBE),
                    mode(this,ZeroPageAddress, 0xA6),
                    mode(this,ZeroPageAddressY, 0xB6)
            );
        }
    },
    LDY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,Value, 0xA0),
                    mode(this,AbsoluteAddress, 0xAC),
                    mode(this,AbsoluteAddressX, 0xBC),
                    mode(this,ZeroPageAddress, 0xA4),
                    mode(this,ZeroPageAddressX, 0xB4)
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
            return List.of(mode(this,Implied, 0x60));
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
                    mode(this,AbsoluteAddress, 0x8D),
                    mode(this,AbsoluteAddressX,0x9D),
                    mode(this,AbsoluteAddressY, 0x99),
                    mode(this,ZeroPageAddress , 0x85),
                    mode(this,ZeroPageAddressX , 0x95),
                    mode(this,IndexedIndirectX , 0x81),
                    mode(this,IndirectIndexedY , 0x91)
            );
        }
    },
    STX {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,AbsoluteAddress, 0x8E),
                    mode(this,ZeroPageAddress, 0x86),
                    mode(this,ZeroPageAddressY, 0x96)
            );
        }
    },

    STY {
        @Override
        public List<AdressingModeMapping> addressingModeMappings() {
            return List.of(
                    mode(this,AbsoluteAddress, 0x8C),
                    mode(this,ZeroPageAddress, 0x84),
                    mode(this,ZeroPageAddressX, 0x94)
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

    public Option<AdressingModeMapping> findByAddressingMode(AddressingMode addressingMode) {
        return addressingModeMappings()
                .find(m -> m.addressingMode == addressingMode);
    }

    private static java.util.HashMap<Byte, OpCode.AdressingModeMapping> mappings = null;

    public static Option<AdressingModeMapping> findByByteValue(byte value) {
        if (mappings == null) {
            mappings = new java.util.HashMap<>();
            List.of(values()).flatMap(opCode ->
                Try.of(() -> opCode.addressingModeMappings()).getOrElse(List.empty())).forEach( m ->
                    mappings.put(m.code, m)
            );
        }

        return Option.of(mappings.get(value));
    }

    public boolean supportAddressingMode(AddressingMode addressingMode) {
        return addressingModeMappings().map(AdressingModeMapping::addressingMode)
                .contains(addressingMode);
    }

    private static AdressingModeMapping mode(OpCode opCode, AddressingMode addressingMode, int byteValue) {
        return new AdressingModeMapping(opCode, addressingMode, (byte) byteValue);
    }

    public List<AdressingModeMapping> addressingModeMappings() {
        throw new UnsupportedOperationException(String.format("OpCode %s has no addressing modes. This means it's either illegal or not yet implemented.", this));
    }

    record AdressingModeMapping(OpCode opCode, AddressingMode addressingMode, byte code){}
}
