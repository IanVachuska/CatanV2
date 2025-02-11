package com.mycatan;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

record TileListener(Board board) implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        Tile tile = (Tile) e.getSource();
        if(board.getSelectedTile() == null){
            board.setSelectedTile(tile);
        }
        else{
            board.handleSelection(tile);
        }

    }
}
