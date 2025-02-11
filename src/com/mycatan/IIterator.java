package com.mycatan;

public interface IIterator<Object> {
    boolean hasNext();
    Object getNext();
    void reset();
    void setHead();
}
