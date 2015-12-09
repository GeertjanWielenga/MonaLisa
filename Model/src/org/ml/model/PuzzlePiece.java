package org.ml.model;

public class PuzzlePiece {

    public PuzzlePiece(String name, int column, int row) {
        this.column = column; 
        this.row = row;
        this.name = name;
    }

    int column;

    int row;
    
    String name;

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public int getColumn() {return column;}

    public void setColumn(int column) {this.column = column;}

    public int getRow() {return row;}

    public void setRow(int row) {this.row = row;}

}