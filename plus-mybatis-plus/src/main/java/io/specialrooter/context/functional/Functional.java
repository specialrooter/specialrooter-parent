package io.specialrooter.context.functional;

public class Functional {
    @FunctionalInterface
    public interface Callback<T> {
        void doWith(T t);
    }
}
