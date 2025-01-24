package semanticAnalyzer.Symbols;
import java.util.Objects;


public class Symbol {
        private String id;
        private SymbolType type;

        public Symbol(String type, String id) {
            this.type = SymbolType.valueOf(type);
            this.id = id;

        }

        public String getId() {
            return this.id;
        }

        public void setId(String id) {
            this.id = id;
        }


        public String getType() {
            return this.type.toString();
        }

        public String toString() {
            return this.type + ":" + this.id;
        }

    // Sovrascrittura del metodo equals
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Symbol symbol = (Symbol) obj;
        return  id.equals(symbol.getId()) &&
                type.equals(symbol.type);
    }

    // Sovrascrittura del metodo hashCode
    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }
}

enum SymbolType {
    VAR,
    FUNCTION
}
