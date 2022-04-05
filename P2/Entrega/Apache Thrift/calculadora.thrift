service Calculadora{
   void ping(),
   double suma(1:double num1, 2:double num2),
   double resta(1:double num1, 2:double num2),
   double producto(1:double num1, 2:double num2),
   i32 modulo(1:i32 num1, 2:i32 num2),
   double division(1:double num1, 2:double num2),
   double potencia(1:double num1, 2:double num2),
   list<list<i32>> sumaMatrices(1:list<list<i32>>m1, 2:list<list<i32>> m2),
   list<list<i32>> restaMatrices(1:list<list<i32>>m1, 2:list<list<i32>> m2),
   list<list<i32>> productoMatrices(1:list<list<i32>>m1, 2:list<list<i32>> m2),
   list<list<i32>> traspuesta(1:list<list<i32>>m1),
   i32 determinante(1:list<list<i32>>m1),

}
