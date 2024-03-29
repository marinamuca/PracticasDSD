/*
 * Please do not edit this file.
 * It was generated using rpcgen.
 */

#include "calc.h"

bool_t
xdr_matriz (XDR *xdrs, matriz *objp)
{
	register int32_t *buf;

	 if (!xdr_array (xdrs, (char **)&objp->matriz_val, (u_int *) &objp->matriz_len, ~0,
		sizeof (double), (xdrproc_t) xdr_double))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_calc_res (XDR *xdrs, calc_res *objp)
{
	register int32_t *buf;

	 if (!xdr_int (xdrs, &objp->errno))
		 return FALSE;
	switch (objp->errno) {
	case 0:
		 if (!xdr_double (xdrs, &objp->calc_res_u.result))
			 return FALSE;
		break;
	default:
		break;
	}
	return TRUE;
}

bool_t
xdr_suma_1_argument (XDR *xdrs, suma_1_argument *objp)
{
	 if (!xdr_double (xdrs, &objp->n1))
		 return FALSE;
	 if (!xdr_double (xdrs, &objp->n2))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_producto_1_argument (XDR *xdrs, producto_1_argument *objp)
{
	 if (!xdr_double (xdrs, &objp->n1))
		 return FALSE;
	 if (!xdr_double (xdrs, &objp->n2))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_resta_1_argument (XDR *xdrs, resta_1_argument *objp)
{
	 if (!xdr_double (xdrs, &objp->n1))
		 return FALSE;
	 if (!xdr_double (xdrs, &objp->n2))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_division_1_argument (XDR *xdrs, division_1_argument *objp)
{
	 if (!xdr_double (xdrs, &objp->n1))
		 return FALSE;
	 if (!xdr_double (xdrs, &objp->n2))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_raiz_1_argument (XDR *xdrs, raiz_1_argument *objp)
{
	 if (!xdr_double (xdrs, &objp->n1))
		 return FALSE;
	 if (!xdr_double (xdrs, &objp->n2))
		 return FALSE;
	return TRUE;
}

bool_t
xdr_potencia_1_argument (XDR *xdrs, potencia_1_argument *objp)
{
	 if (!xdr_double (xdrs, &objp->n1))
		 return FALSE;
	 if (!xdr_double (xdrs, &objp->n2))
		 return FALSE;
	return TRUE;
}
