
package Model;

public class Opcode
{
    private String IR0_5;
    private String IR6_10;
    private String IR11_15;
    private String IR16_31;

    public Opcode(String IR) {
        this.IR0_5 = IR.substring(0, 6);
        this.IR6_10 = IR.substring(6, 11);
        this.IR11_15 = IR.substring(11, 16);
        this.IR16_31 = IR.substring(16, 32);
    }

    public String getIR0_5() {
        return IR0_5;
    }

    public String getIR6_10() {
        return IR6_10;
    }

    public String getIR11_15() {
        return IR11_15;
    }

    public String getIR16_31() {
        return IR16_31;
    }
    
    public String toString()
    {
        return IR0_5 + IR6_10 + IR11_15 + IR16_31;
    }
    
    public String getHex()
    {
        return Converter.binaryToHex(toString(), 8);
    }
}