/* la siguiente union se utiliza para discriminar entre llamadas con exito y llamadas con errores */
union calc_res switch (int errno) {
	case 0:
		int result; /* sin error: valor de evaluar la operacion */
	default:
		void; /* con error: nada */
};

program CALCULADORA {
	version BASIC_CALC {
		calc_res suma(int n1, int n2) = 1;
		calc_res producto(int n1, int n2) = 2;
		calc_res resta(int n1, int n2) = 3;
		calc_res division(int n1, int n2) = 4;
	} =1;
} = 0x20000155;
