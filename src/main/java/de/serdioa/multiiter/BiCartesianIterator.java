package de.serdioa.multiiter;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;


public class BiCartesianIterator<T, U> implements Iterator<List<Object>>  {

    private final Collection<T> first;
    private final Collection<U> second;
    
    private Iterator<T> firstIterator;
    private Iterator<U> secondIterator;
    
    private boolean hasNext = true;
    private T firstValue;
    private U secondValue;
    
    public BiCartesianIterator(Collection<T> first, Collection<U> second) {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
        
        this.advanceIterator();
    }
    
    @Override
    public boolean hasNext() {
        return this.hasNext;
    }


    @Override
    public List<Object> next() {
        if (!this.hasNext) {
            throw new NoSuchElementException();
        }
        
        final List<Object> next = List.of(this.firstValue, this.secondValue);
        this.advanceIterator();
        
        return next;
    }
    
    
    private void advanceIterator() {
        while (this.hasNext) {
            if (this.firstIterator == null) {
                this.firstIterator = this.first.iterator();
                this.hasNext = this.advanceFirstIterator();
            } else if (this.secondIterator == null) {
                this.secondIterator = this.second.iterator();
                this.hasNext = this.advanceSecondIterator();
                return;
            } else if (this.advanceSecondIterator()) {
                return;
            } else if (this.advanceFirstIterator()) {
                this.secondIterator = null;
            } else {
                // Cannot advance the first iterator anymore.
                this.hasNext = false;
            }
        }
    }
    
    
    private boolean advanceFirstIterator() {
        boolean iterHasNext = this.firstIterator.hasNext();
        if (iterHasNext) {
            this.firstValue = this.firstIterator.next();
        }
        return iterHasNext;
    }
    
    
    private boolean advanceSecondIterator() {
        boolean iterHasNext = this.secondIterator.hasNext();
        if (iterHasNext) {
            this.secondValue = this.secondIterator.next();
        }
        return iterHasNext;
    }
    
    public static void main(String [] args) {
        System.out.println("==========");
        test(List.of("aaa", "bbb", "ccc"), List.of(10, 20, 30, 40));
        System.out.println("==========");
        test(List.of("aaa"), List.of(10, 20, 30, 40));
        System.out.println("==========");
        test(List.of("aaa", "bbb", "ccc"), List.of(10));
        System.out.println("==========");
        test(Collections.emptyList(), List.of(10, 20, 30, 40));
        System.out.println("==========");
        test(List.of("aaa", "bbb", "ccc"), Collections.emptyList());
        System.out.println("==========");
    }
    
    public static void test(Collection<?> first, Collection<?> second) {
        BiCartesianIterator<?, ?> iter = new BiCartesianIterator<>(first, second);
        Stream<List<Object>> stream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iter, 0), false);
        stream.forEach(lst -> System.out.println(lst));
    }
}
