
package Model;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

public class Processor
{
    // Memory-related variables
    public InternalRegisters irs;
    public Database db;
    public Map<Integer, Opcode> opcodes; //Instruction opcodes retrieved from db
    
    // Management variables
    public int currInstructionNumber; //Instruction indexing
    public Map<Integer, ArrayList<Integer>> registerToUsers; //Registers and the instructions currently updating them
    public Map<Integer, InstructionPipeline> pipeline;
    public int clockCycle;
    
    // Stall-related variables
    private int bufferStall; //Instruction which will stall
    private int currentStall; //Which instruction is stalling
    public ArrayList<Integer> stallWaitingOn; //What instructions are being waited on by stalling instruction
    
    // List of errors if there are any
    private ArrayList<ErrorLogData> errors;
    
    public Processor()
    {
        irs = new InternalRegisters();
        irs.setPC(0x1000);
        db = Database.getInstance();
        opcodes = db.getInstructions();
        
        currInstructionNumber = 1;
        registerToUsers = new TreeMap<>();
        for (Object key : db.getRegisters().keySet())
        {
            registerToUsers.put((int)key, new ArrayList<>());
        }
        pipeline = new TreeMap<>();
        clockCycle = 1;
        
        bufferStall = -1;
        currentStall = -1;
        stallWaitingOn = new ArrayList<>();
        
        errors = new ArrayList<>();
    }
    
    public Boolean singleStep()
    {   
        Boolean success = false;
        
        updateStall();
        
        // Add instruction only if there's no stall and there is still an instruction to fetch
        if (currentStall < 0 && opcodes.containsKey((int)irs.getPC()) && opcodes.get((int)irs.getPC()) != null)
        {
            pipeline.put(currInstructionNumber, new InstructionPipeline(currInstructionNumber, this));
            currInstructionNumber++;
        }
        
        // Iterate through pipeline
        for (Map.Entry<Integer, InstructionPipeline> entry : pipeline.entrySet())
        {
            // Check if there is no stall or it is an instruction before the stalling instruction
            if (currentStall < 0 || currentStall >= 0 && entry.getKey() < currentStall)
            {
                success = entry.getValue().nextStep();
                if (!success && entry.getValue().getError() != null)
                {
                    errors.add(entry.getValue().getError());
                }
            }
            if (currentStall == entry.getKey())
            {
                success = true;
            }
        }
        
        clockCycle++;
        return success;
    }
    
    private void updateStall()
    {
        if (bufferStall >= 0)
        {
            currentStall = bufferStall;
            bufferStall = -1;
        }
        
        System.out.print("Stall Update: ");
        for (int i : stallWaitingOn)
        {
            System.out.print(i + ", ");
        }
        System.out.println();
        
        // Remove stall if there's no pending
        if (stallWaitingOn.size() > 0)
        {
            for (int i = stallWaitingOn.size() - 1; i >= 0; i--)
            {
                ArrayList<Integer> users = registerToUsers.get(stallWaitingOn.get(i));
                if (users.size() > 0)
                {
                    int lastUser = users.get(users.size() - 1);
                    if (lastUser == currentStall)
                    {
                        if (users.size() > 1 && pipeline.containsKey(users.get(users.size() - 2)))
                        {
                            lastUser = users.get(users.size() - 2);
                        }
                        else stallWaitingOn.remove(i);
                    }
                    InstructionPipeline inst = pipeline.get(lastUser);
                    if (inst.resultAvailableNextStep)
                    {
                        stallWaitingOn.remove(i);
                    }
                }
                else
                {
                    stallWaitingOn.remove(i);
                }
            }
        }

        if (stallWaitingOn.size() <= 0) 
            currentStall = -1;
    }
    
    public ArrayList<ErrorLogData> getErrors()
    {
        return errors;
    }
    
    public int getCurrentStall()
    {
        return currentStall;
    }
    
    public void setStallingInstruction(int stall)
    {
        bufferStall = stall;
    }
}