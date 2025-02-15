package com.mycatan;

public interface ISelectable {
    void select();
    void deselect();
    void swap(ISelectable other);
    boolean contains(int ptrX, int ptrY);
}
