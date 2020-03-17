package com.dimas.rumahmakan.base;

public class BaseContract {
    public interface Presenter<T> {
        void subscribe();
        void unsubscribe();
        void attach(T view);
    }

    public interface View {}
}
