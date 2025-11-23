
public interface ConsumerOnEDT<T> {
    void accept(T t);
}
