package de.rwth.swc.coffee4j.engine.process.util;

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;

/**
 * Simple view of a list which reverses the order of the elements. Can be used to iterate over a list in reverse order.
 *
 * @param <E> the type of the list elements
 */
public class ReversedUnmodifiableListView<E> extends AbstractList<E> {
    
    private final List<E> delegate;
    
    private ReversedUnmodifiableListView(List<E> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }
    
    /**
     * Creates a new unmodifiable view of the given list with a reversed order.
     *
     * @param delegate to which all calls are delegated with a reversed index
     * @param <E> the type of the list elements
     * @return the view
     */
    public static <E> ReversedUnmodifiableListView<E> of(List<E> delegate) {
        return new ReversedUnmodifiableListView<>(delegate);
    }
    
    @Override
    public E get(int index) {
        return delegate.get(delegate.size() - 1 - index);
    }
    
    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
