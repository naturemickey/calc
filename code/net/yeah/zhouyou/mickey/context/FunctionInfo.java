package net.yeah.zhouyou.mickey.context;

import net.yeah.zhouyou.mickey.execute.ASM;

import java.util.HashMap;
import java.util.Map;

public class FunctionInfo {

    private static Map<String, Integer> map = new HashMap<>();

    static {
        map.put(ASM.pow, 2);
        map.put(ASM.sqrt, 1);
    }

    public static boolean isFunctionName(String str) {
        return map.containsKey(str);
    }

    public static int getParamsCount(String functionName) {
        return map.get(functionName);
    }
}
