
package Model;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Database 
{
    public static final Long DEFAULT_REGISTER_VALUE = 0L;
    public static final Long MINIMUM_REGISTER_VALUE = 0x8000000000000000L;
    public static final Long MAXIMUM_REGISTER_VALUE = 0x7FFFFFFFFFFFFFFFL;
    public static final Byte DEFAULT_MEMORY_VALUE = 0;
    public static final Byte MINIMUM_MEMORY_VALUE = (byte) 0x80;
    public static final Byte MAXIMUM_MEMORY_VALUE = 0x7F;
    
    private static Database _instance;
    
    private Map<Integer, Long> registerDB;
    private Map<Integer, Opcode> instructionDB;
    private Map<Integer, Byte> memoryDB;
    
    private String registerValuePattern;
    private String memoryValuePattern;
    private String instructionValuePattern;
    
    private Database() 
    {
        registerDB = new TreeMap<>();
        instructionDB = new TreeMap<>();
        memoryDB = new TreeMap<>();
        initializeRegisters();
        initializeInstructions();
        initializeMemory();

        instructionValuePattern = "^[01]{32}$";
    }
    
    private void initializeRegisters()
    {
        for (int i = 1; i <= 31; i++)
        {
            registerDB.put(i, DEFAULT_REGISTER_VALUE);
        }
    }
    
    private void initializeInstructions()
    {
        for (int i = 0x1000; i <= 0x2FFF; i += 0x0004)
        {
            instructionDB.put(i, null);
        }
    }
    
    private void initializeMemory()
    {
        for (int i = 0x3000; i <= 0x4FFF; i += 0x0001)
        {
            memoryDB.put(i, DEFAULT_MEMORY_VALUE);
        }
    }
    
    public long getRegister(int register)
    {
        if (registerDB.containsKey(register))
        {
            return registerDB.get(register);
        }
        return DEFAULT_REGISTER_VALUE;
    }
    
    public Boolean setRegister(int register, long value)
    {
        if (registerDB.containsKey(register))
        {
            registerDB.replace(register, value);
            return true;
        }
        return false;
    }
    
    public Map getRegisters()
    {
        return registerDB;
    }
    
    public Map getMemory()
    {
        return memoryDB;
    }
    
    public Map getInstructions()
    {
        return instructionDB;
    }
    
    public Boolean instructionExists(int memoryLocation)
    {
        return instructionDB.containsKey(memoryLocation) && instructionDB.get(memoryLocation) != null;
    }
    
    public Boolean memoryExists(int memoryLocation)
    {
        return memoryDB.containsKey(memoryLocation);
    }
    
    public Boolean editRegister(int key, long value)
    {
        if (registerDB.containsKey(key) && value >= MINIMUM_REGISTER_VALUE && value <= MAXIMUM_REGISTER_VALUE)
        {
            registerDB.replace(key, value);
            return true;
        }
        return false;
    }
    
    public byte getMemory(int location)
    {
        if (memoryDB.containsKey(location))
        {
            return memoryDB.get(location);
        }
        return DEFAULT_MEMORY_VALUE;
    }
    
    public long getMemoryDouble(int location)
    {
        String hex = "";
        if (memoryDB.containsKey(location) && memoryDB.containsKey(location + 0x0007))
        {
            for (int i = 0; i < 8; i++)
            {
                hex = Converter.byteToHex(memoryDB.get(location + i), 2) + hex;
            }
        }
        return Converter.hexToLong(hex);
    }
    
    public Boolean editMemory(int key, byte value)
    {
        if (memoryDB.containsKey(key) && value >= MINIMUM_REGISTER_VALUE && value <= MAXIMUM_MEMORY_VALUE)
        {
            memoryDB.replace(key, value);
            return true;
        }
        return false;
    }
    
    public Boolean editMemoryDouble(int key, long value)
    {
        Boolean success = false;
        String hex;
        byte single;
        if (memoryDB.containsKey(key) && memoryDB.containsKey(key + 0x0007))
        {
            hex = Converter.longToHex(value, 16);
            for (int i = 0; i < 8; i++)
            {
                success = editMemory(key + i, Converter.hexToByte(hex.substring(i * 2, i * 2 + 2)));
            }
        }
        return success;
    }
    
    public Boolean addInstruction(int key, String value)
    {
        if (instructionDB.containsKey(key) && value.matches(instructionValuePattern))
        {
            instructionDB.replace(key, new Opcode(value));
            return true;
        }
        return false;
    }
    
    public void clearInstructions()
    {
        initializeInstructions();
    }
    
    public static Database getInstance()
    {
        if(_instance == null)
        {
            _instance = new Database();
        }
        return _instance;
    }
}
