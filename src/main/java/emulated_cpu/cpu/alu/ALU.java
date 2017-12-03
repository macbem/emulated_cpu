package emulated_cpu.cpu.alu;

import emulated_cpu.OpCode;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * ALU class is used with ControlUnit class to perform all mathematical operations.
 */
public final class ALU {
    private Registers registers;

    /**
     * ALU_OP_CODES is used as lookup table for getting desired operation.
     */
    private final ArrayList<OpCode> ALU_OP_CODES = new ArrayList<>(Arrays.asList(
        new OpCode((x, y) -> ++x, 1),   //INC
        new OpCode((x, y) -> --x, 1),   //DEC
        new OpCode((x, y) -> x + y, 2), //ADD
        new OpCode((x, y) -> x - y, 2), //DEC
        new OpCode((x, y) -> x * y, 2), //MUL
        new OpCode((x, y) -> x / y, 2), //DIV
        new OpCode((x, y) -> x & y, 2), //AND
        new OpCode((x, y) -> x | y, 2), //OR
        new OpCode((x, y) -> x ^ y, 2), //XOR
        new OpCode((x, y) -> ~x, 1),    //NOT
        new OpCode((x, y) -> x >> y, 2),//RSHFT
        new OpCode((x, y) -> x >> y, 2),//LSHFT
        new OpCode((x, y) -> {                          //CMP
            int diff = x - y;
            registers.getStatusRegister()
                     .setZeroFlagState(false); //reset flags
            registers.getStatusRegister()
                     .setCarryFlagState(false);
            registers.getStatusRegister()
                     .setNegativeFlagState(false);
            if (diff < 0)
                registers.getStatusRegister()
                         .setNegativeFlagState(true);
            else if (diff > 0)
                registers.getStatusRegister()
                         .setCarryFlagState(true);
            else
                registers.getStatusRegister()
                         .setZeroFlagState(true);
            return null;
        }, 2)
    ));

    /**
     * Creates new ALU object with specified number of registers inside.
     *
     * @param registerNumber number of registers, can't be negative, with 0 value only StatusRegister is created.
     */
    ALU(int registerNumber) {
        registers = new Registers(registerNumber);
    }

    /**
     * Creates new ALU object with default number of registers defined by variable in Registers class.
     */
    ALU() {
        registers = new Registers();
    }

    /**
     * This method computes passed values with operation specified by OP code.
     *
     * @param opCode specifies which operation should be performed
     * @param arg1   value of first argument. Shouldn't be null
     * @param arg2   value of second argument. Shouldn't be null
     * @return result of operation or null if operation doesn't return anything
     */
    Integer compute(int opCode, Integer arg1, Integer arg2) {
        checkIfOPCodeExists(opCode);

        int requiredArgumentCount = ALU_OP_CODES.get(opCode)
                                                .getRequiredArguments();
        int argumentCount = 0;
        if (arg1 != null) argumentCount++;
        if (arg2 != null) argumentCount++;
        checkArgumentsCountForOpCode(requiredArgumentCount, argumentCount, opCode);

        return ALU_OP_CODES.get(opCode)
                           .getOperation()
                           .apply(arg1, arg2);
    }

    /**
     * Reloaded method with second argument empty.
     *
     * @param opCode specifies which operation should be performed
     * @param arg1   value of argument. Shouldn't be null
     * @return result of operation or null if operation doesn't return anything
     */
    Integer compute(int opCode, Integer arg1) {
        return compute(opCode, arg1, null);
    }

    /**
     * Checks if passed OP code exist in lookup table.
     *
     * @param opCode OP code to be checked
     */
    private void checkIfOPCodeExists(int opCode) {
        if (opCode > ALU_OP_CODES.size() - 1)
            throw new IndexOutOfBoundsException("OP code " + opCode + " doesn't exist");
    }

    /**
     * Checks if passed arguments count match required argument count.
     *
     * @param required number of required arguments
     * @param actual   actual number of arguments
     * @param opCode   OP code value, used to throw exception with valuable message
     */
    private void checkArgumentsCountForOpCode(int required, int actual, int opCode) {
        if (required != actual)
            throw new IllegalArgumentException("Passed arguments don't match arguments count for " + opCode + " op code");
    }

    /**
     * Gets register set from ALU.
     *
     * @return registers contained in this ALU
     */
    Registers getRegisters() {
        return registers;
    }

    /**
     * Sets specifed registers value.
     *
     * @param address address of modified register
     * @param value   new value of register
     */
    void setRegister(int address, int value) {
        this.registers.write(address, value);
    }
}