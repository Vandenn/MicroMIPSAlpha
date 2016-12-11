
package Model;

public enum Step
{   
    IF(0),
    ID(1),
    EX(2),
    MEM(3),
    WB(4),
    END(5);
    
    private int value;
    
    private Step(final int value) { this.value = value; }
    
    public static Step getEquivalent(int id)
    {
        switch (id)
        {
            case 0: return IF;
            case 1: return ID;
            case 2: return EX;
            case 3: return MEM;
            case 4: return WB;
            case 5: return END;
            default: return null;
        }
    }
    
    public int getValue() { return value; }
    
    public Step getNextStep() { return Step.getEquivalent(value + 1); }
}