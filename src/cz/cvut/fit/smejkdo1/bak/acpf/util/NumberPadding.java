package cz.cvut.fit.smejkdo1.bak.acpf.util;

public class NumberPadding {
    public static String intPadding (int num, int length){
        return NumberPadding.stringPadding(String.valueOf(num), length);
    }

    public static String stringPadding (String num, int length){
        StringBuilder s = new StringBuilder();
        while(s.length() + num.length() < length){
            s.append("0");
        }
        s.append(num);
        return s.toString();
    }
}
