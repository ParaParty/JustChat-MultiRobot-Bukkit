package cn.endymx.multirobot.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Pattern;

public class MessageTools {
    static String Base64Decode(String s){
        return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8);
    }

    public static String Base64Encode(String s){
        return Base64.getEncoder().encodeToString(s.getBytes(StandardCharsets.UTF_8));
    }

    public static String getEncode(){
        if (Pattern.matches("Linux.*", System.getProperty("os.name"))) {
            return "UTF-8";
        }else {
            return "GBK";
        }
    }
}
