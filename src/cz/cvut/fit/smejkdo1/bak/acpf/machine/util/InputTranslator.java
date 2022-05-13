package cz.cvut.fit.smejkdo1.bak.acpf.machine.util;

import java.util.ArrayList;
import java.util.List;

public class InputTranslator {

    public static List<Boolean> intToBool(int number, int bit){
        long upperBound = 1<<bit;
        if (upperBound < number)
            number = (int)(upperBound - 1);

        List<Boolean> result = new ArrayList<>();
        String str = Integer.toBinaryString(number);
        for (int i = str.length() - bit; i < str.length(); i++)
            if (i < 0 || str.charAt(i) == '0')
                result.add(false);
            else if (str.charAt(i) == '1')
                result.add(true);
            else
                throw new UnsupportedOperationException();
        return result;
    }

    public static int BoolToInt(List<Boolean> list) {
        int result = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i))
                result += (1 << (list.size() - i - 1));
        }
        return result;
    }
}
