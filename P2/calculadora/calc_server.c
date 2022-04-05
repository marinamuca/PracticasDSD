/*
 * This is sample code generated by rpcgen.
 * These are only templates and you can use them
 * as a guideline for developing your own functions.
 */

#include "calc.h"
#include <math.h>

calc_res *
suma_1_svc(double n1, double n2,  struct svc_req *rqstp)
{
	static calc_res  result;

	result.calc_res_u.result=n1+n2;

    printf("Sumando... %lf + %lf = %lf\n\n", n1, n2, result.calc_res_u.result);

	return &result;
}

calc_res *
producto_1_svc(double n1, double n2,  struct svc_req *rqstp)
{
	static calc_res  result;

	result.calc_res_u.result=n1*n2;

    printf("Multiplicando... %lf * %lf = %lf\n\n", n1, n2, result.calc_res_u.result);

	return &result;
}

calc_res *
resta_1_svc(double n1, double n2,  struct svc_req *rqstp)
{
	static calc_res  result;

	result.calc_res_u.result=n1-n2;

    printf("Restando... %lf - %lf = %lf\n\n", n1, n2, result.calc_res_u.result);

	return &result;
}

calc_res *
division_1_svc(double n1, double n2,  struct svc_req *rqstp)
{
	static calc_res  result;

	result.calc_res_u.result=n1/n2;

    printf("Dividiendo... %lf / %lf = %lf\n\n", n1, n2, result.calc_res_u.result);

	return &result;
}

calc_res *
raiz_1_svc(double n1, double n2,  struct svc_req *rqstp)
{
	static calc_res  result;

	result.calc_res_u.result = pow(n2,1/n1);

    printf("Calculando... %lf √ %lf = %lf\n\n", n1, n2, result.calc_res_u.result);


	return &result;
}

calc_res *
potencia_1_svc(double n1, double n2,  struct svc_req *rqstp)
{
	static calc_res  result;

	result.calc_res_u.result = 1;

	for (int i = 0 ; i < n2 ; i++){
		result.calc_res_u.result *= n1;
	}

    printf("Elevando... %lf ^ %lf = %lf\n\n", n1, n2, result.calc_res_u.result);

	return &result;
}