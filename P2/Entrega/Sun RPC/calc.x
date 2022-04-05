typedef double matriz<>;

/* la siguiente union se utiliza para discriminar entre llamadas con exito y llamadas con errores */
union calc_res switch (int errno) {
	case 0:
		double result; /* sin error: valor de evaluar la operacion */
	default:
		void; /* con error: nada */
};



program CALCULADORA {
	version BASIC_CALC2 {
		calc_res suma(double n1, double n2) = 1;
		calc_res producto(double n1, double n2) = 2;
		calc_res resta(double n1, double n2) = 3;
		calc_res division(double n1, double n2) = 4;
		calc_res raiz(double n1, double n2) = 5;
		calc_res potencia(double n1, double n2) = 6;	
	} =1;
} = 0x20000155;
