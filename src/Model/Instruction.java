
package Model;

import java.util.HashMap;
import org.apache.commons.lang.StringUtils;

public enum Instruction
{
    OR("^or r[0-9]{1,2},[ ]{0,1}r[0-9]{1,2},[ ]{0,1}r[0-9]{1,2}$"), 
    DSUBU("^dsubu r[0-9]{1,2},[ ]{0,1}r[0-9]{1,2},[ ]{0,1}r[0-9]{1,2}$"), 
    SLT("^slt r[0-9]{1,2},[ ]{0,1}r[0-9]{1,2},[ ]{0,1}r[0-9]{1,2}$"), 
    NOP("^nop$"),
    BNE("^bne r[0-9]{1,2},[ ]{0,1}r[0-9]{1,2},[ ]{0,1}(0x[0-9a-f]{4}|[a-z0-9_]+)$"),
    LD("^ld r[0-9]{1,2},[ ]{0,1}[0-9a-f]{4}\\(r[0-9]{1,2}\\)$"),
    SD("^sd r[0-9]{1,2},[ ]{0,1}[0-9a-f]{4}\\(r[0-9]{1,2}\\)$"),
    DADDIU("^daddiu r[0-9]{1,2},[ ]{0,1}r[0-9]{1,2},[ ]{0,1}0x[0-9a-f]{4}$"),
    J("^j [a-z0-9_]+$")
    ;
    
    private final String stringValue;
    private Instruction(final String s) { stringValue = s; }
    public String getRegex() { return stringValue; }
    public String toString()
    {
        switch(this)
        {
            case OR: return "or";
            case DSUBU: return "dsubu";
            case SLT: return "slt";
            case NOP: return "nop";
            case BNE: return "bne";
            case LD: return "ld";
            case SD: return "sd";
            case DADDIU: return "daddiu";
            case J: return "j";
            default: return "";
        }
    }
    public String generateOpcode(int codeLine, String code, HashMap<String, Integer> labels)
    {
        String opcode = "";
        
        switch(this)
        {
            case OR:
            case DSUBU:
            case SLT:
                opcode = generateROpcode(code);
                break;
            case NOP:
                opcode = "00000000000000000000000000000000";
                break;
            case BNE:
                opcode = generateBNEOpcode(codeLine, code, labels);
                break;
            case LD:
            case SD:
                opcode = generateLoadStoreOpcode(codeLine, code, labels);
                break;
            case DADDIU:
                opcode = generateDADDIUOpcode(codeLine, code, labels);
                break;
            case J:
                opcode = generateJOpcode(codeLine, code, labels);
                break;
        }
        
        return opcode;
    }
    
    private String generateROpcode(String code)
    {
        String[] registers = getDataPart(code).split(",");
        
        int firstRegister = Integer.parseInt(registers[0].substring(1));
        int secondRegister = Integer.parseInt(registers[1].substring(1));
        int thirdRegister = Integer.parseInt(registers[2].substring(1));
        
        if (firstRegister > 31 || secondRegister > 31 || thirdRegister > 31) return "";
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("000000");
        sb.append(Converter.intToBinary(secondRegister, 5));
        sb.append(Converter.intToBinary(thirdRegister, 5));
        sb.append(Converter.intToBinary(firstRegister, 5));
        sb.append("00000");
        switch (this)
        {
            case OR:
                sb.append("100101");
                break;
            case DSUBU:
                sb.append("101111");
                break;
            case SLT:
                sb.append("101010");
                break;
        }
        
        return sb.toString();
    }
    
    private String generateBNEOpcode(int codeLine, String code, HashMap<String, Integer> labels)
    {
        String hexRegex = "^0x[0-9a-f]{4}$";
        String[] data = getDataPart(code).split(",");
        
        int firstRegister = Integer.parseInt(data[0].substring(1));
        int secondRegister = Integer.parseInt(data[1].substring(1));
        int immediate = 0;
        
        if (data[2].matches(hexRegex))
        {
            immediate = Integer.parseInt(data[2].substring(2), 16);
        }
        else
        {
            if (!labels.containsKey(data[2])) return "";
            immediate = (labels.get(data[2]) - (codeLine + 0x0004)) / 0x0004;
        }
        
        if (firstRegister > 31 || secondRegister > 31) return "";
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("000101");
        sb.append(Converter.intToBinary(firstRegister, 5));
        sb.append(Converter.intToBinary(secondRegister, 5));
        sb.append(Converter.intToBinary(immediate, 16));
        
        return sb.toString();
    }
    
    private String generateLoadStoreOpcode(int codeLine, String code, HashMap<String, Integer> labels)
    {
        String[] data = getDataPart(code).split(",");
        
        int destinationRegister = Integer.parseInt(data[0].substring(1));
        int baseRegister = Integer.parseInt(data[1].substring(data[1].indexOf("r") + 1, data[1].indexOf(")")));
        int offset = Integer.parseInt(data[1].substring(0, data[1].indexOf("(")), 16);
        
        if (destinationRegister > 31 || baseRegister > 31) return "";
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(this == Instruction.LD ? "110111" : "111111");
        sb.append(Converter.intToBinary(baseRegister, 5));
        sb.append(Converter.intToBinary(destinationRegister, 5));
        sb.append(Converter.intToBinary(offset, 16));
        
        return sb.toString();
    }
    
    private String generateDADDIUOpcode(int codeLine, String code, HashMap<String, Integer> labels)
    {
        String[] data = getDataPart(code).split(",");
        
        int firstRegister = Integer.parseInt(data[0].substring(1));
        int secondRegister = Integer.parseInt(data[1].substring(1));
        int immediate = Integer.parseInt(data[2].substring(2), 16);
        
        if (firstRegister > 31 || secondRegister > 31) return "";
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("011001");
        sb.append(Converter.intToBinary(secondRegister, 5));
        sb.append(Converter.intToBinary(firstRegister, 5));
        sb.append(Converter.intToBinary(immediate, 16));
        
        return sb.toString();
    }
    
    private String generateJOpcode(int codeLine, String code, HashMap<String, Integer> labels)
    {
        String label = getDataPart(code);
        
        if (!labels.containsKey(label)) return "";
        
        int index = labels.get(label) / 0x0004;
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("000010");
        sb.append(Converter.intToBinary(index, 26));
        
        return sb.toString();
    }
    
    private String getDataPart(String code)
    {
        String registersString = code.split(" ",2)[1];
        return registersString.replaceAll("\\s+","");
    }
}