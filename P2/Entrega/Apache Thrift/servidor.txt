from functools import reduce
import glob
from mimetypes import init
from re import M
import sys
from unittest import result

from calculadora import Calculadora

from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

import logging
import numpy as np

logging.basicConfig(level=logging.DEBUG)

def inicializarMatriz(filas, columnas):
    result = []
    for i in range(filas):
        result.append([0] * columnas) 
    return result

def redondear(num, dec=0):
    num = str(num)[:str(num).index('.')+dec+2]
    if num[-1]>='5':
        return float(num[:-2-(not dec)]+str(int(num[-2-(not dec)])+1))
    return float(num[:-1])


class CalculadoraHandler:
    def __init__(self):
        self.log = {}

    def ping(self):
        print("me han hecho ping()")

    def suma(self, n1, n2):
        print("sumando " + str(n1) + " con " + str(n2))
        return n1 + n2

    def resta(self, n1, n2):
        print("restando " + str(n1) + " con " + str(n2))
        return n1 - n2

    def producto(self, n1, n2):
        print("multiplicando " + str(n1) + " con " + str(n2))
        return n1 * n2

    def division(self, n1, n2):
        print("dividiendo " + str(n1) + " entre " + str(n2))
        return n1 / n2

    def modulo(self, n1, n2):
        print("Calculando el resto de dividir " + str(n1) + " entre " + str(n2))
        return n1 % n2

    def sumaMatrices(self, m1, m2):
        print("Calculando suma de matrices... ")

        result = inicializarMatriz(len(m1), len(m1[0]))

        for f in range(len(m1)):
            for c in range(len(m1[f])):
                result[f][c] = m1[f][c] + m2[f][c]
        return result

    def restaMatrices(self, m1, m2):
        print("Calculando resta de matrices... ")
        
        result = inicializarMatriz(len(m1), len(m1[0]))

        for f in range(len(m1)):
            for c in range(len(m1[f])):
                result[f][c] = m1[f][c] - m2[f][c]
        return result
    
    def productoMatrices(self, m1, m2):
        print("Calculando producto de matrices... ")
        
        if (len(m1) > len(m2)):
            result = inicializarMatriz(len(m1), len(m2[0]))
        else: 
            result = inicializarMatriz(len(m1[0]), len(m2))


        for f in range(len(m2[0])):
            for c2 in range(len(m1)):
                for c1 in range(len(m1[f])):
                    result[f][c2] += m1[f][c1] * m2[c1][c2]
        return result

    def traspuesta(self, m1):
        print("Calculando traspuesta de matriz... ")
        
        result = inicializarMatriz(len(m1[0]), len(m1))

        for i in range(len(m1[0])):
            for j in range(len(m1)):
                result[i][j] = m1[j][i]

        return result

    # Mi aproximación del determinante no exacta.
    # def determinante(self, m1):
    #     print("Calculando determinante de matriz... ")
    #     orden=len(m1)
    #     positivo=0
    #     for i in range(orden):
    #         positivo += reduce((lambda x, y: x * y), [m1[(i+j)%orden][j] for j in range(orden)])
    #     negativo=0
    #     for i in range(orden):
    #         negativo+=reduce((lambda x, y: x * y), [m1[(orden-i-j)%orden][j] for j in range(orden)])
    #     return positivo-negativo

    def determinante(self, m1):
        print("Calculando determinante de matriz... ")
        return int(redondear(np.linalg.det(m1)))



    


if __name__ == "__main__":
    handler = CalculadoraHandler()
    processor = Calculadora.Processor(handler)
    transport = TSocket.TServerSocket(host="127.0.0.1", port=9090)
    tfactory = TTransport.TBufferedTransportFactory()
    pfactory = TBinaryProtocol.TBinaryProtocolFactory()

    server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)

    print("iniciando servidor...")
    server.serve()
    print("fin")
