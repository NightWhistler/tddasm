package net.nightwhistler.tddasm.mos65xx;

import static net.nightwhistler.ByteUtils.toInt;

public class StatusRegister {

    StatusRegister() {}

    private StatusRegister(StatusRegister other) {
        this.carryFlag = other.carryFlag;
        this.zeroFlag = other.zeroFlag;
        this.interruptDisableFlag = other.interruptDisableFlag;
        this.decimalModeFlag = other.decimalModeFlag;
        this.breakCommandFlag = other.breakCommandFlag;
        this.overFlowFlag = other.overFlowFlag;
        this.negativeFlag = other.negativeFlag;
    }

    private boolean carryFlag;
    private boolean zeroFlag;
    private boolean interruptDisableFlag;
    private boolean decimalModeFlag;

    private boolean breakCommandFlag;
    private boolean overFlowFlag;
    private boolean negativeFlag;

    public byte toByte() {
        int[] asArray = new int[] {
                toInt(carryFlag),  //0
                toInt(zeroFlag),   //1
                toInt(interruptDisableFlag),  //2
                toInt(decimalModeFlag), //3
                toInt(breakCommandFlag), //4
                1,  //Unused, so always 1  - 5
                toInt(overFlowFlag),  //6
                toInt(negativeFlag)   //7
        };

        byte value = 0;

        for (int i=0; i < 8; i++) {
           value = (byte) (value | (asArray[i] << i));
        }

        return value;
    }

    public StatusRegister copy() {
        return new StatusRegister(this);
    }

    public void setFrom(byte value) {
        carryFlag = (value & 0b1) > 0;
        zeroFlag = (value & 0b10) > 0;
        interruptDisableFlag = (value & 0b100) > 0;
        decimalModeFlag = (value & 0b1000) > 0;
        breakCommandFlag = (value & 0b10000) > 0;
        overFlowFlag = (value & 0b1000000) > 0;
        negativeFlag = (value & 0b10000000) > 0;
    }

    public void setCarryFlag(boolean carryFlag) {
        this.carryFlag = carryFlag;
    }

    public void setZeroFlag(boolean zeroFlag) {
        this.zeroFlag = zeroFlag;
    }

    public void setInterruptDisableFlag(boolean interruptDisableFlag) {
        this.interruptDisableFlag = interruptDisableFlag;
    }

    public void setDecimalModeFlag(boolean decimalModeFlag) {
        this.decimalModeFlag = decimalModeFlag;
    }

    public void setBreakCommandFlag(boolean breakCommandFlag) {
        this.breakCommandFlag = breakCommandFlag;
    }

    public void setOverFlowFlag(boolean overFlowFlag) {
        this.overFlowFlag = overFlowFlag;
    }

    public void setNegativeFlag(boolean negativeFlag) {
        this.negativeFlag = negativeFlag;
    }

    public boolean isCarryFlagSet() {
        return carryFlag;
    }

    public boolean isZeroFlagSet() {
        return zeroFlag;
    }

    public boolean isInterruptDisableFlagSet() {
        return interruptDisableFlag;
    }

    public boolean isDecimalModeFlagSet() {
        return decimalModeFlag;
    }

    public boolean isBreakCommandFlagSet() {
        return breakCommandFlag;
    }

    public boolean isOverFlowFlagSet() {
        return overFlowFlag;
    }

    public boolean isNegativeFlagSet() {
        return negativeFlag;
    }

    @Override
    public String toString() {
        return "StatusRegister{" +
                "carryFlag=" + carryFlag +
                ", zeroFlag=" + zeroFlag +
                ", interruptDisableFlag=" + interruptDisableFlag +
                ", decimalModeFlag=" + decimalModeFlag +
                ", breakCommandFlag=" + breakCommandFlag +
                ", overFlowFlag=" + overFlowFlag +
                ", negativeFlag=" + negativeFlag +
                '}';
    }
}
