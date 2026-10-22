package app.daos;

import java.util.Set;

public interface IDAO <T>{
    T save(T t);
    Set<T> get();
    T getByID(String id);
    T update(T t);
    void delete(T t);
}
