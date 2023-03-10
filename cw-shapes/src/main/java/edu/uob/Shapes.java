package edu.uob;

import java.awt.*;

public class Shapes {

  private static edu.uob.Shapes Shapes;

  // TODO use this class as then entry point; play around with your shapes, etc

  public static void main(String[] args) {

    /*
    Triangle myTriangle = new Triangle(150000002, 666666671, 683333338);
    TwoDimensionalShape myShape = myTriangle;
    myTriangle.setColour(Colour.GREEN);
    System.out.println("This is a " +  myShape.getColour() +" " + myShape + " its type is "+ myTriangle.getVariant());
    if(myShape instanceof MultiVariantShape) System.out.println("This shape has multiple variants\n");
    else System.out.println("This shape has only one variant\n");

    Circle myCircle = new Circle(10);
    myShape = myCircle;
    myCircle.setColour(Colour.RED);
    System.out.println("This is a " +  myShape.getColour() +" " + myShape);
    if(myShape instanceof MultiVariantShape) System.out.println("This shape has multiple variants\n");
    else System.out.println("This shape has only one variant\n");

    Rectangle myRectangle = new Rectangle(3, 4);
    myShape = myRectangle;
    myRectangle.setColour(Colour.BLUE);
    System.out.println("This is a " +  myShape.getColour() +" " + myShape);
    if(myShape instanceof MultiVariantShape) System.out.println("This shape has multiple variants\n");
    else System.out.println("This shape has only one variant\n");
*/


    //fill array with 100 shapes
    TwoDimensionalShape arrShape;

    int cnt=0;
    TwoDimensionalShape[] shapes = new TwoDimensionalShape[100];
      //fill array with random numbers, turn into shapes after
      for(int i=0; i<100; i++){

        int x = (int)(Math.random()*10);

        if (Math.floorMod(x, 2)==0){
          arrShape=new Triangle(x, (int)(Math.random()*10), (int)(Math.random()*10));
          shapes[i]=arrShape;
        }
        else if (Math.floorMod(x, 3)==0){
          arrShape=new Rectangle(x, (int)(Math.random()*10));
          shapes[i]=arrShape;
        }
        else if (Math.floorMod(x, 5)==0){
          arrShape = new Circle(x);
          shapes[i]=arrShape;
        }
        else{
          arrShape=new Rectangle(x, (int)(Math.random()*10));;
          shapes[i]=arrShape;
        }
        //System.out.println(shapes[i] + ", " +x);
      }

      TwoDimensionalShape res = shapes[47];
      double a = res.calculateArea();

    System.out.println(res + " with an area of " + (int)a);

    for(int i=0; i<100; i++){
      if(shapes[i] instanceof Triangle){
        cnt++;
      }
    }

    System.out.println("Triangle Instance variable: " + cnt);
    System.out.println("Triangle Class Variable: "+ Triangle.getPopulation());

    //this excmaple creates an error when run and the 2D shape is not a triangle
    /*
    //casting example
    TwoDimensionalShape firstShape = shapes[0];
    // Down-cast the shape into a triangle
    Triangle firstTriangle = (Triangle)firstShape;
    TriangleVariant variant = firstTriangle.getVariant();

    System.out.println("Casting Variant Test: Shape is: " + firstShape + ", variant is: " + variant);
     */
  }
}
