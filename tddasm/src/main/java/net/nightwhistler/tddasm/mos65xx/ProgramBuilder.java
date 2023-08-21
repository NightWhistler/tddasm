package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import net.nightwhistler.tddasm.c64.screen.ScreenCode;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;

import static net.nightwhistler.tddasm.mos65xx.AddressingMode.Accumulator;
import static net.nightwhistler.tddasm.mos65xx.AddressingMode.Relative;
import static net.nightwhistler.tddasm.mos65xx.Operand.accumulator;
import static net.nightwhistler.tddasm.mos65xx.Operand.implied;
import static net.nightwhistler.tddasm.mos65xx.Operand.noValue;

public class ProgramBuilder {
    private List<ProgramElement> programElements = List.empty();

    private ProgramBuilder(List<ProgramElement> elements) {
        this.programElements = elements;
    }

    public ProgramBuilder() {

    }

    /**
     * Adds all the elements from another source, like another builder
     * @param elements
     * @return
     */
    public ProgramBuilder include(List<ProgramElement> elements) {
        return new ProgramBuilder(this.programElements.appendAll(elements));
    }

    public ProgramBuilder include(ProgramBuilder other) {
        return include(other.buildElements());
    }

    public ProgramBuilder label(String label) {
        return withElement(new Label(label));
    }

    public ProgramBuilder text(String text) {
        return data(text.getBytes(Charset.forName("ASCII")));
    }

    public ProgramBuilder screenCodes(String text) {
        byte[] ascii = text.getBytes(StandardCharsets.US_ASCII);
        return data(ScreenCode.toScreenCodes(ascii));
    }

    public ProgramBuilder adc(Operand operand) {
        return withOperation(OpCode.ADC, operand);
    }

    public ProgramBuilder sei() {
        return withOperation(OpCode.SEI, noValue());
    }

    public ProgramBuilder cli() {
        return withOperation(OpCode.CLI, noValue());
    }


    public ProgramBuilder data(byte[] data) {
        return withElement(new Data(data));
    }

    public ProgramBuilder lda(Operand operand) {
        return withOperation(OpCode.LDA, operand);
    }

    public ProgramBuilder and(Operand operand) {
        return withOperation(OpCode.AND, operand);
    }

    public ProgramBuilder ora(Operand operand) {
        return withOperation(OpCode.ORA, operand);
    }

    public ProgramBuilder ldx(Operand operand) {
        return withOperation(OpCode.LDX, operand);
    }

    public ProgramBuilder stx(Operand operand) {
        return withOperation(OpCode.STX, operand);
    }

    public ProgramBuilder asl(Operand operand) {
        return withOperation(OpCode.ASL, operand);
    }

    public ProgramBuilder asl() {
        return asl(accumulator());
    }

    public ProgramBuilder bcc(Operand operand) {
        return withOperation(OpCode.BCC, operand);
    }

    public ProgramBuilder bcs(Operand operand) {
        return withOperation(OpCode.BCS, operand);
    }
    public ProgramBuilder bit(Operand operand) {
        return withOperation(OpCode.BIT, operand);
    }

    public ProgramBuilder bmi(Operand operand) {
        return withOperation(OpCode.BMI, operand);
    }

    public ProgramBuilder bpl(Operand operand) {
        return withOperation(OpCode.BPL, operand);
    }

    public ProgramBuilder brk() {
        return withOperation(OpCode.BRK, noValue());
    }

    public ProgramBuilder bvc(Operand operand) {
        return withOperation(OpCode.BVC, operand);
    }

    public ProgramBuilder bvs(Operand operand) {
        return withOperation(OpCode.BVS, operand);
    }

    public ProgramBuilder clc() {
        return withOperation(OpCode.CLC, noValue());
    }

    public ProgramBuilder cld() {
        return withOperation(OpCode.CLD, noValue());
    }

    public ProgramBuilder clv() {
        return withOperation(OpCode.CLV, noValue());
    }

    public ProgramBuilder dec(Operand operand) {
        return withOperation(OpCode.DEC, operand);
    }

    public ProgramBuilder nop() {
        return withOperation(OpCode.NOP, noValue());
    }

    public ProgramBuilder dey() {
        return withOperation(OpCode.DEY, noValue());
    }

    public ProgramBuilder dex() {
        return withOperation(OpCode.DEX, noValue());
    }

    public ProgramBuilder txa() {
        return withOperation(OpCode.TXA, noValue());
    }

    public ProgramBuilder sta(Operand operand) {
        return withOperation(OpCode.STA, operand);
    }

    public ProgramBuilder rts() {
        return withOperation(OpCode.RTS, noValue());
    }

    public ProgramBuilder rti() {
        return withOperation(OpCode.RTI, noValue());
    }

    public ProgramBuilder pha() {
        return withOperation(OpCode.PHA, noValue());
    }

    public ProgramBuilder php() {
        return withOperation(OpCode.PHP, noValue());
    }

    public ProgramBuilder pla() {
        return withOperation(OpCode.PLA, noValue());
    }

    public ProgramBuilder plp() {
        return withOperation(OpCode.PLP, noValue());
    }

    public ProgramBuilder rol() {
        return rol(accumulator());
    }

    public ProgramBuilder rol(Operand operand) {
        return withOperation(OpCode.ROL, operand);
    }

    public ProgramBuilder ror() {
        return ror(accumulator());
    }

    public ProgramBuilder ror(Operand operand) {
        return withOperation(OpCode.ROR, operand);
    }

    public ProgramBuilder sbc(Operand operand) {
        return withOperation(OpCode.SBC, operand);
    }

    public ProgramBuilder sec() {
        return withOperation(OpCode.SEC, implied());
    }

    public ProgramBuilder sed() {
        return withOperation(OpCode.SED, implied());
    }

    public ProgramBuilder tax() {
        return withOperation(OpCode.TAX, implied());
    }

    public ProgramBuilder tay() {
        return withOperation(OpCode.TAY, implied());
    }

    public ProgramBuilder tsx() {
        return withOperation(OpCode.TSX, implied());
    }

    public ProgramBuilder txs() {
        return withOperation(OpCode.TXS, implied());
    }

    public ProgramBuilder tya() {
        return withOperation(OpCode.TYA, implied());
    }

    public ProgramBuilder sty(Operand operand) {
        return withOperation(OpCode.STY, operand);
    }

    public ProgramBuilder iny() {
        return withOperation(OpCode.INY, noValue());
    }

    public ProgramBuilder inx() {
        return withOperation(OpCode.INX, noValue());
    }

    public ProgramBuilder ldy(Operand operand) {
        return withOperation(OpCode.LDY, operand);
    }

    public ProgramBuilder jmp(String label) {
        return withOperation(OpCode.JMP, new Operand.LabelOperand(label));
    }

    public ProgramBuilder jmp(Operand.AddressOperand operand) {
        return withOperation(OpCode.JMP, operand);
    }

    public ProgramBuilder jsr(String label) {
        return withOperation(OpCode.JSR, new Operand.LabelOperand(label));
    }

    public ProgramBuilder jsr(Operand operand) {
        return withOperation(OpCode.JSR, operand);
    }

    public ProgramBuilder eor(Operand.TwoByteAddress operand) {
        return withOperation(OpCode.EOR, operand);
    }

    public ProgramBuilder lsr() {
       return lsr(new Operand.NoValue(Accumulator));
    }

    public ProgramBuilder lsr(Operand operand) {
        return withOperation(OpCode.LSR, operand);
    }

    public ProgramBuilder beq(String label) {
        return withOperation(OpCode.BEQ, new Operand.LabelOperand(label, Relative));
    }

    public ProgramBuilder bne(String label) {
        return withOperation(OpCode.BNE, new Operand.LabelOperand(label, Relative));
    }

    public ProgramBuilder inc(Operand operand) {
        return withOperation(OpCode.INC, operand);
    }

    public ProgramBuilder cpx(Operand operand) {
        return withOperation(OpCode.CPX, operand);
    }

    public ProgramBuilder cpy(Operand operand) {
        return withOperation(OpCode.CPY, operand);
    }

    public ProgramBuilder cmp(Operand operand) {
        return withOperation(OpCode.CMP, operand);
    }

    public ProgramBuilder comment(String comment) {
        return withElement(new Comment(comment));
    }

    private ProgramBuilder withOperation(OpCode opCode, Operand operand) {
        return withElement(new OperationProvider(opCode, operand));
    }

    private ProgramBuilder withElement(ProgramElement element) {
        return new ProgramBuilder(programElements.append(element));
    }


    /**
     * Registers a piece of Java code to be executed if the processor
     * reaches this point in the program. This works by registering a
     * Java routine for this address and inserting a NOP opcode in the
     * compiled code at this point.
     *
     * @param javaRoutine A lambda that takes a Processor as an argument
     * @return
     */
    public ProgramBuilder java(Consumer<Processor> javaRoutine) {
        return withElement(new JavaElement(javaRoutine));
    }

    public List<ProgramElement> buildElements() {
        return programElements;
    }

    public Program buildProgram() {
        return new Program(buildElements());
    }

    public Program buildProgram(Operand.TwoByteAddress startAddress) {
        return new Program(startAddress, buildElements());
    }

    public static ProgramBuilder buildProgram(Function<ProgramBuilder, ProgramBuilder> addActions) {
        return addActions.apply(new ProgramBuilder());
    }
}
