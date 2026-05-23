package compiler;

import java.util.HashMap;
import java.util.Map;


public class SymbolTable {

    private final Map<String, String> table  = new HashMap<>();
    final SymbolTable         parent;

    public SymbolTable(SymbolTable parent) {
        this.parent = parent;
    }

    public void define(String name, String type) {
        table.put(name, type);
    }

    public String lookup(String name) {
        if (table.containsKey(name)) return table.get(name);
        if (parent != null)          return parent.lookup(name);
        return null;
    }

    public boolean isDefined(String name) {
        return lookup(name) != null;
    }

    public boolean isDefinedLocally(String name) {
        return table.containsKey(name);
    }

    @Override
    public String toString() {
        return "SymbolTable" + table;
    }
}
