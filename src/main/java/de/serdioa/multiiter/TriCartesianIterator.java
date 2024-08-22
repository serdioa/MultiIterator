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


public class TriCartesianIterator<T, U, V> implements Iterator<List<Object>>  {

    private final Collection<T> first;
    private final Collection<U> second;
    private final Collection<V> third;
    
    private Iterator<T> firstIterator;
    private Iterator<U> secondIterator;
    private Iterator<V> thirdIterator;
    
    private boolean hasNext = true;
    private T firstValue;
    private U secondValue;
    private V thirdValue;
    
    public TriCartesianIterator(Collection<T> first, Collection<U> second, Collection<V> third) {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
        this.third = Objects.requireNonNull(third);
        
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
        
        final List<Object> next = List.of(this.firstValue, this.secondValue, this.thirdValue);
        this.advanceIterator();
        
        return next;
    }
    
    
    private void advanceIterator() {
        while (this.hasNext) {
            if (this.firstIterator == null) {
                this.firstIterator = this.first.iterator();
                this.hasNext = this.advanceFirstIterator();
                this.secondIterator = null;
            } else if (this.secondIterator == null) {
                this.secondIterator = this.second.iterator();
                this.hasNext = this.advanceSecondIterator();
                this.thirdIterator = null;
            } else if (this.thirdIterator == null) {
                this.thirdIterator = this.third.iterator();
                this.hasNext = this.advanceThirdIterator();
                return;
            } else if (this.advanceThirdIterator()) {
                return;
            } else if (this.advanceSecondIterator()) {
                this.thirdIterator = null;
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
    
    
    private boolean advanceThirdIterator() {
        boolean iterHasNext = this.thirdIterator.hasNext();
        if (iterHasNext) {
            this.thirdValue = this.thirdIterator.next();
        }
        return iterHasNext;
    }
    
    public static void main(String [] args) {
        System.out.println("==========");
        test(List.of("aaa", "bbb", "ccc", "ddd"), List.of(10, 20, 30), List.of("X", "Y", "Z"));
        System.out.println("==========");
        test(List.of("aaa"), List.of(10, 20, 30), List.of("X", "Y", "Z"));
        System.out.println("==========");
        test(List.of("aaa", "bbb", "ccc", "ddd"), List.of(10), List.of("X", "Y", "Z"));
        System.out.println("==========");
        test(List.of("aaa", "bbb", "ccc", "ddd"), List.of(10, 20, 30), List.of("X"));
        System.out.println("==========");
        test(Collections.emptyList(), List.of(10, 20, 30), List.of("X", "Y", "Z"));
        System.out.println("==========");
        test(List.of("aaa", "bbb", "ccc", "ddd"), Collections.emptyList(), List.of("X", "Y", "Z"));
        System.out.println("==========");
        test(List.of("aaa", "bbb", "ccc", "ddd"), List.of(10, 20, 30), Collections.emptyList());
        System.out.println("==========");
    }
    
    public static void test(Collection<?> first, Collection<?> second, Collection<?> third) {
        TriCartesianIterator<?, ?, ?> iter = new TriCartesianIterator<>(first, second, third);
        Stream<List<Object>> stream = StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iter, 0), false);
        stream.forEach(lst -> System.out.println(lst));
    }
}
