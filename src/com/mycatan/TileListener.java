package com.mycatan;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

record TileListener(Board board) implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        ISelectable selectedTile = (ISelectable) e.getSource();
        if(selectedTile instanceof FlippableHex fh
        && !fh.isFlipped()) {
            fh.flip(true);
            return;
        }
        if(board.getSelectedTile() == null){
            board.select(selectedTile);
        }
        else{
            board.handleSelection(selectedTile);
        }

    }
}
