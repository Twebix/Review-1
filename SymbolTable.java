import java.util.HashMap;

public class SymbolTable {
    HashMap<String, Integer> table = new HashMap<>();

    public void set(String key, int value) {
        table.put(key, value);
    }

    public int get(String key) {
        return table.getOrDefault(key, 0);
    }

    public void printTable() {
        System.out.println("\n--- SYMBOL TABLE ---");
        for (String key : table.keySet()) {
            System.out.println(key + " = " + table.get(key));
        }
    }
}