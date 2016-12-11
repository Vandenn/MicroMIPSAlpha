
package Model;

public class DecodedInstruction
{
    public Instruction type; // Used by all instructions
    public int rs; // Used by OR, DSUBU, SLT, BNE, LD, SD, and DADDIU
    public int rt; // Used by OR, DSUBU, SLT, and BNE
    public int rd; // Used by OR, DSUBU, SLT, LD, SD, DADDIU
    public short immediate; // Used by BNE, LD, SD, DADDIU
    public int memory; // Used by J
    
    public static DecodedInstruction decodeInstruction(Opcode opcode)
    {
        DecodedInstruction result = new DecodedInstruction(); 
        if (opcode.getHex().equals("00000000"))
        {
            result.type = Instruction.NOP;
        }
        else if (opcode.getIR0_5().equals("000000"))
        {
            if (opcode.getIR16_31().endsWith("100101")) result.type = Instruction.OR;
            else if (opcode.getIR16_31().endsWith("101111")) result.type = Instruction.DSUBU;
            else if (opcode.getIR16_31().endsWith("101010")) result.type = Instruction.SLT;
            result.rt = Converter.binaryToInt(opcode.getIR11_15());
            result.rd = Converter.binaryToInt(opcode.getIR16_31().substring(0, 5));
            result.rs = Converter.binaryToInt(opcode.getIR6_10());
        }
        else if (opcode.getIR0_5().equals("000101"))
        {
            result.type = Instruction.BNE;
            result.rs = Converter.binaryToInt(opcode.getIR6_10());
            result.rt = Converter.binaryToInt(opcode.getIR11_15());
            result.immediate = Converter.binaryToShort(opcode.getIR16_31());
        }
        else if (opcode.getIR0_5().equals("110111") || opcode.getIR0_5().equals("111111"))
        {
            result.type = opcode.getIR0_5().equals("110111") ? Instruction.LD : Instruction.SD;
            result.rs = Converter.binaryToInt(opcode.getIR6_10());
            result.rd = Converter.binaryToInt(opcode.getIR11_15());
            result.immediate = Converter.binaryToShort(opcode.getIR16_31());
        }
        else if (opcode.getIR0_5().equals("011001"))
        {
            result.type = Instruction.DADDIU;
            result.rs = Converter.binaryToInt(opcode.getIR6_10());
            result.rd = Converter.binaryToInt(opcode.getIR11_15());
            result.immediate = Converter.binaryToShort(opcode.getIR16_31());
        }
        else if (opcode.getIR0_5().equals("000010"))
        {
            result.type = Instruction.J;
            result.memory = Converter.binaryToInt(opcode.getIR16_31());
        }
        return result;
    }
}