package edu.uob;

public class Triangle extends TwoDimensionalShape implements MultiVariantShape{
  static int population=0;

  public static int getPopulation(){
    return Triangle.population;
  }

  long a;  long b;  long c;
  public Triangle(int aA, int bB, int cC) {
    a=(long) aA;
    b=(long) bB;
    c=(long) cC;
    Triangle.population++;
  }

  int getLongestSide() {
    return (int) Math.max(a, b);
  }

  //public Colour GetColour(){
    //return Colour.GREEN;
  //}

  public double calculateArea() {
    double s=(a+b+c)/2;
    double area = Math.sqrt(s*(s-a)*(s-b)*(s-c));
    return a;
  }

  int calculatePerimeterLength() {
    long ap = Math.abs((long)a);
    long bp = Math.abs((long)b);
    long cp = Math.abs((long)c);
    return (int) (ap+bp+cp);
  }

  public String toString() {
    return "Triangle with side lengths of: " + a +", " + b +", " +c;
  }

  public TriangleVariant getVariant() {
    if(a<=0 || b<=0 || c<=0){
      return  TriangleVariant.ILLEGAL;
    }
    if((a>b+c && a>b && a>c) || (b>a+c && b>a && b>c) || (c>a+b && c>b && c>a)){
      return  TriangleVariant.IMPOSSIBLE;
    }
    if (a==b+c || b==a+c || c==a+b){
      return TriangleVariant.FLAT;
    }
    if (a == b && b == c) {
      return TriangleVariant.EQUILATERAL;
    }
    if (a == b || b == c || c == a) {
      return TriangleVariant.ISOSCELES;
    }
    long aSq = a*a;
    long bSq = b*b;
    long cSq = c*c;

    if (aSq==bSq+cSq || bSq==aSq+cSq || cSq==aSq+bSq){
      //System.out.println("Right triangle a=" +  Math.sqrt(bSq+cSq));
      //System.out.println("Right triangle b=" +  Math.sqrt(aSq+cSq));
      //System.out.println("Right triangle c=" +  Math.sqrt(bSq+aSq));
      return TriangleVariant.RIGHT;
    }
    if (a != b && b != c && c != a) {
      return TriangleVariant.SCALENE;
    }
    return TriangleVariant.ILLEGAL;
  }


}

