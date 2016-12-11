
package Model;

public enum InstructionCategory
{
    ALU,
    Jump,
    Memory,
    None;
    
    public static InstructionCategory getCategoryOfInstruction(Instruction i)
    {
        if (i == Instruction.OR || i == Instruction.DSUBU ||
            i == Instruction.SLT || i == Instruction.DADDIU)
            return ALU;
        else if (i == Instruction.BNE || i == Instruction.J)
            return Jump;
        else if (i == Instruction.LD || i == Instruction.SD)
            return Memory;
        else
            return None;
    }
}