package net.yeah.zhouyou.mickey.context;

import net.yeah.zhouyou.mickey.util.DbgInfo;

import java.util.HashMap;
import java.util.Map;

public class Context {
    private static Context ourInstance = new Context();

    public static Context getInstance() {
        return ourInstance;
    }

    private Context() {
    }

    private Map<String, Double> map = new HashMap<>();

    public Double getValue(String key) {
        Double d = map.get(key);
        return d == null ? Double.NaN : d;
    }

    private static final String leftPattern = "^(_|[a-zA-Z])\\w*$";
    private static final String rightPattern = "^(\\d+([.]\\d*)?)|([.]\\d+)$";

    public void parseSet(String line) {
        String[] ss = splitLine(line);
        for (String s : ss)
            parseValue(s);

        DbgInfo.println("CONTEXT VAR: ", this.map);
    }

    private void parseValue(String s) {
        String[] vs = s.split("=");
        if (vs.length == 1 && "clear".equalsIgnoreCase(vs[0].trim())) {
            map.clear();
            return;
        }
        if (vs.length != 2)
            throw new RuntimeException("ERROR[SET] SYNTAX: " + s);
        String vn = vs[0].trim();
        String v = vs[1].trim();
        if (!vn.matches(leftPattern))
            throw new RuntimeException("ERROR[SET] VAR NAME: " + vn);

        if (!v.matches(rightPattern))
            throw new RuntimeException("ERROR[SET] VALUE: " + v);

        if (FunctionInfo.isFunctionName(vn))
            throw new RuntimeException("ERROR[SET] VAR NAME: " + vn + " IS A FUNCTION");

        this.map.put(vn, Double.valueOf(v));
    }

    private String[] splitLine(String line) {
        if (line == null)
            throw new RuntimeException();
        if (!line.startsWith("set "))
            throw new RuntimeException();
        String[] ss = line.substring(4).split(";");
        if (ss.length == 0)
            throw new RuntimeException();
        return ss;
    }
}
