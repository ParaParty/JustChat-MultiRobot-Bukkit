package cn.endymx.multirobot.util;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.regex.Pattern;

public class MessageTools {
    static String Base64Decode(String s){
        return new String(Base64.getDecoder().decode(s), Charset.forName("UTF-8"));
    }

    public static String Base64Encode(String s){
        return Base64.getEncoder().encodeToString(s.getBytes(Charset.forName("UTF-8")));
    }

    public static String getEncode(){
        if (Pattern.matches("Linux.*", System.getProperty("os.name"))) {
            return "UTF-8";
        }else {
            return "GBK";
        }
    }
}
