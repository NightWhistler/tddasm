package net.nightwhistler.tddasm.mos65xx;

import io.vavr.collection.List;
import net.nightwhistler.tddasm.c64.screen.ScreenCode;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static net.nightwhistler.tddasm.mos65xx.AddressingMode.Relative;
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

    public ProgramBuilder ldx(Operand operand) {
        return withOperation(OpCode.LDX, operand);
    }

    public ProgramBuilder stx(Operand operand) {
        return withOperation(OpCode.STX, operand);
    }
    public ProgramBuilder dex() {
        return withOperation(OpCode.DEX, noValue());
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

    public ProgramBuilder jsr(Operand.TwoByteAddress operand) {
        return withOperation(OpCode.JSR, operand);
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

    private ProgramBuilder withOperation(OpCode opCode, Operand operand) {
        return withElement(new OperationProvider(opCode, operand));
    }

    private ProgramBuilder withElement(ProgramElement element) {
        return new ProgramBuilder(programElements.append(element));
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
}
