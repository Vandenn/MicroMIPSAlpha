
package Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import org.apache.commons.lang.StringUtils;

public class Parser
{
    private Database db;
    private ArrayList<ErrorLogData> errors;
    
    public Parser()
    {
        db = Database.getInstance();
        errors = new ArrayList<>();
    }
    
    public Boolean parseCode(String code)
    {
        Scanner sc = new Scanner(code);
        
        HashMap<Integer, String> rawCode = new HashMap<>();
        HashMap<String, Integer> labels = new HashMap<>();
        int counter = 0x1000;
        String labelRegex = "^[0-9a-z_]+[:]{1}.*$";
        String curr = "";
        String label = "";
        String currOpcode = "";
        
        while (sc.hasNextLine())
        {
            curr = sc.nextLine().toLowerCase();
            if (!curr.isEmpty())
            {
                if (curr.matches(labelRegex))
                {
                    label = curr.substring(0, curr.indexOf(':'));
                    if (labels.containsKey(label))
                    {
                        addError((counter - 0x1000) / 0x0004 + 1, "Duplicate label!");
                        return false;
                    }
                    labels.put(label, counter);
                    curr = curr.substring(Math.min(curr.indexOf(':') + 1, curr.length() - 1)).trim();
                }
                rawCode.put(counter, curr);
            }
            counter += 0x0004;
        }
        
        for (Map.Entry<Integer, String> codeLine : rawCode.entrySet())
        {
            currOpcode = generateOpcode(codeLine.getKey(), codeLine.getValue(), labels);
            if (currOpcode.isEmpty())
            {
                addError((codeLine.getKey() - 0x1000) / 0x0004 + 1, "Syntax error!"); 
            }
            else
            {
                if (!db.addInstruction(codeLine.getKey(), currOpcode))
                {
                    addError((codeLine.getKey() - 0x1000) / 0x0004 + 1, "Error adding instruction opcode!");
                }
            }
            
            if (errors.size() > 0)
            {
                db.clearInstructions();
                return false;
            }
        }
        
        return true;
    }
    
    private String generateOpcode(int codeLine, String code, HashMap<String, Integer> labels)
    {
        for (Instruction inst : Instruction.values())
        {
            if (code.matches(inst.getRegex()))
            {
                return inst.generateOpcode(codeLine, code, labels);
            }
        }
        return "";
    } 
    
    public String getOpcodeText()
    {
        StringBuilder sb = new StringBuilder();
        for (Object value : db.getInstructions().values())
        {
            if (value == null) break;
            sb.append(Converter.binaryToHex(value.toString(), 8));
            sb.append("\n");
        }
        return sb.toString();
    }
    
    public ArrayList<ErrorLogData> getErrors()
    {
        return errors;
    }
    
    private void addError(int line, String message)
    {
        errors.add(new ErrorLogData(line, message));
    }
}