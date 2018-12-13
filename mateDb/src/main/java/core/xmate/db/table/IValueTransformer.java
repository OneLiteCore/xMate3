package core.xmate.db.table;

/**
 * @author DrkCore
 * @since 12/12/18
 */
public interface IValueTransformer {
    String toVal(String raw);

    String toRaw(String val);

    class SIMPLE implements IValueTransformer {

        @Override
        public String toVal(String raw) {
            return raw;
        }

        @Override
        public String toRaw(String val) {
            return val;
        }
    }
}
