/*
 * This is sample code generated by rpcgen.
 * These are only templates and you can use them
 * as a guideline for developing your own functions.
 */

#include "basic.h"

int *
suma_1_svc(int n1, int n2,  struct svc_req *rqstp)
{
	static int  result;

	result=n1+n2;

    printf("Sumando... %d + %d = %d\n\n", n1, n2, result);

	return &result;
}

int *
producto_1_svc(int n1, int n2,  struct svc_req *rqstp)
{
	static int  result;

	result=n1*n2;

    printf("Multiplicando... %d * %d = %d\n\n", n1, n2, result);

	return &result;
}

int *
resta_1_svc(int n1, int n2,  struct svc_req *rqstp)
{
	static int  result;

	result=n1-n2;

    printf("Restando... %d - %d = %d\n\n", n1, n2, result);

	return &result;
}

int *
division_1_svc(int n1, int n2,  struct svc_req *rqstp)
{
	static int  result;

	result=n1/n2;

    printf("Dividiendo... %d / %d = %d\n\n", n1, n2, result);

	return &result;
}
