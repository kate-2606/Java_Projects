package edu.uob;

abstract class TwoDimensionalShape {

    public TwoDimensionalShape() {}
 protected Colour shapeColour;

  public void setColour(Colour c){
    shapeColour =c;
  }

  public Colour getColour(){
    return shapeColour;
  }

  abstract double calculateArea();

  abstract int calculatePerimeterLength();
}
