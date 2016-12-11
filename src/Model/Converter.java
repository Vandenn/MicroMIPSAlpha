
package Model;

import java.math.BigInteger;
import org.apache.commons.lang.StringUtils;

public class Converter
{
    public static int hexToInt(String hex)
    {
        return (int) Long.parseLong(hex, 16);
    }
    
    public static long hexToLong(String hex)
    {
        return new BigInteger(hex, 16).longValue();
    }
    
    public static byte hexToByte(String hex)
    {
        return (byte) Integer.parseInt(hex, 16);
    }
    
    public static String intToHex(int i, int digits)
    {
        String hex = Integer.toHexString(i);
        return (StringUtils.repeat("0", digits - hex.length()) + hex).toUpperCase();
    }
    
    public static String shortToHex(short s, int digits)
    {
        String hex = Integer.toHexString(s & 0xFFFF);
        hex = (StringUtils.repeat("0", digits - hex.length()) + hex).toUpperCase();
        hex = hex.substring(hex.length() - 4, hex.length());
        return hex;
    }
    
    public static String longToHex(long l, int digits)
    {
        String hex = Long.toHexString(l);
        return (StringUtils.repeat("0", digits - hex.length()) + hex).toUpperCase();
    }
    
    public static String byteToHex(byte b, int digits)
    {
        return String.format("%02X", b);
    }
    
    public static String intToBinary(int number, int digits)
    {
        String raw = Integer.toBinaryString(number);
        return StringUtils.repeat("0", digits - raw.length()) + raw;
    }
    
    public static String binaryToHex(String binaryCode, int digits)
    {
        BigInteger decimalOpcode = new BigInteger(binaryCode, 2);
        String hexOpcode = decimalOpcode.toString(16);
        return StringUtils.repeat("0", digits - hexOpcode.length()) + hexOpcode;
    }
    
    public static int binaryToInt(String binaryCode)
    {
        BigInteger decimal = new BigInteger(binaryCode, 2);
        return decimal.intValue();
    }
    
    public static short binaryToShort(String binaryCode)
    {
        BigInteger decimal = new BigInteger(binaryCode, 2);
        return decimal.shortValue();
    }
}