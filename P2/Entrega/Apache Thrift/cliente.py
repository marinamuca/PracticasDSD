from ast import For
import imp
from telnetlib import DO
from unittest import result
from calculadora import Calculadora

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

import re

def menuEnteros():
    print("¿Que operacion quiere realizar? (Formato: numero operador numero)")
    print("Operadores válidos: ")
    print("\t+ para sumar")
    print("\t- para restar")
    print("\t* para multiplicar")
    print("\t/ para dividir")
    print("\t% para modulo")
    print("\tPulse s para volver al menu principal")

def pedirFilas():
    incorrecto = True
    while(incorrecto):
        numFilas = input("Introduce el numero de filas: ")
        elemsInput = re.split(r'\s+', numFilas)
        # Le elimino los espacios vacios
        if(elemsInput[len(elemsInput)-1] == '' or elemsInput[0] == ''):
            elemsInput.remove('')
        
        if(len(elemsInput) == 1):
            numFilas = int(elemsInput[0])
            incorrecto = False
        else:
            print("Error en lectura")
    return numFilas

def pedirColumnas():
    incorrecto = True
    while(incorrecto):
        numColumnas = input("Introduce el numero de columnas: ")
        elemsInput = re.split(r'\s+', numColumnas)
        # Le elimino los espacios vacios
        if(elemsInput[len(elemsInput)-1] == '' or elemsInput[0] == ''):
            elemsInput.remove('')
        
        if(len(elemsInput) == 1):
            numColumnas = int(elemsInput[0])
            incorrecto = False
        else:
            print("Error en lectura")
    return numColumnas

def menuMatrices():
    print("¿Que operacion quiere realizar? (Seleccione una opción:)")
    print("\t+ para sumar")
    print("\t- para restar")
    print("\t* para producto")
    print("\tt para traspuesta")
    print("\td para determinante")
    print("\tPulse s para volver al menu principal")

def printMatriz(matriz):
    for fila in range(len(matriz)):
        print(matriz[fila])

def convertFilaToInt(fila):
    result = []
    for s in fila:
        result.append(int(s))
    return result
    


transport = TSocket.TSocket("localhost", 9090)
transport = TTransport.TBufferedTransport(transport)
protocol = TBinaryProtocol.TBinaryProtocol(transport)

client = Calculadora.Client(protocol)

transport.open()

# print("hacemos ping al server")
client.ping()

cont_reading = True

while cont_reading :
    print("Seleccione un modo de operacion:")
    print("\te - Operaciones con enteros.")
    print("\tm - Operaciones con matrices.")
    print("\ts - Salir")
    modo = input()

    print("----------------------------------------------------")

    lectura_incorrecta = True
    # Si el modo es operacion con enteros
    if(modo == "e"):
        while lectura_incorrecta:
            # Muestro el menu de operaciones con enteros
            menuEnteros()
            operacion = input()
            # Si quiere salir vuelve al principio del bucle
            if(operacion != "s"):
                # Utilizo expresiones regulares para obtener los operandos y los operadores
                operandos = re.findall(r'[0-9]+', operacion)
                operador = re.findall(r'[\+]|[\-]|[\*]|[\/]|[\%]', operacion)

                # Elimino los operadores, los operandos y los espacios de la variable resto
                resto = re.sub(r'[\+]|[\-]|[\*]|[\/]|[\%]', '', operacion)
                resto = re.sub(r'[0-9]+', '', resto)
                resto = re.sub(r'\s+', '', resto)
                
                if not operador:
                    print("Operador Incorrecto.")
                    print("----------------------------------------------------")
                elif(resto!='' or len(operandos) < 2):
                    print("Formato Incorrecto.")
                    print("----------------------------------------------------")
                else:
                    lectura_incorrecta = False
                    n1 = int(operandos[0])
                    n2 = int(operandos[1])
                    op = operador[0]

                    if op == '+':
                        resultado = int(client.suma(n1, n2))    
                    elif op == '-':
                        resultado = int(client.resta(n1, n2))
                    elif op == '*':
                        resultado = int(client.producto(n1, n2))
                    elif op == '/':
                        resultado = int(client.division(n1, n2))
                    elif op == '%':
                        resultado = int(client.modulo(n1, n2))

                    print("Resultado: " + str(n1) + " " + op + " " + str(n2) + " = " + str(resultado))
            else: 
                lectura_incorrecta = False
                print("----------------------------------------------------")

    elif modo == "m":
        while lectura_incorrecta:
            # Muestro el menu de operaciones disponibles para matrices
            menuMatrices()
            operacion = input()
            # si la operacion es s, vuelve al principio del buble
            if(operacion != "s"):
                operador = re.findall(r'[\+]|[\-]|[\*]|[d]|[t]', operacion)
                if not operador:
                    print("Operador no valido")
                else:
                    lectura_incorrecta = False
                    m1 = []
                    m2 = []
                    print("----------------------------------------------------")

                    if(operacion == "+" or operacion == "-" or operacion == "*"):
                        numMatrices = 2
                    else:
                        numMatrices = 1
                    
                    for i in range(numMatrices):
                        numMatriz = ''
                        if(i == 0 and numMatrices==2):
                            numMatriz = "primera "
                        elif(i == 1):
                            numMatriz = "segunda "

                        print("Leyendo la " + numMatriz + "Matriz...")
                        numFilas = pedirFilas()
                        numColumnas = pedirColumnas()

                        dimensiones_adecuadas = True
                        # Compruebo que la segunda matriz tenga las dimensiones adecuadas
                        if(i == numMatrices-1):
                            dimensiones_adecuadas=False
                            while(dimensiones_adecuadas == False):
                                if(operacion == "+" or operacion == "-"):
                                    if(numFilas != len(m1) or numColumnas != len(m1[0])):
                                        print ("Error. Ambas matrices deben tener las mismas dimensiones")
                                    else:
                                        dimensiones_adecuadas = True
                                elif(operacion == "*"):
                                    if(numFilas != len(m1[0])):
                                        print ("Error. El numero de filas debe ser igual al numero de columnas de la primera matriz")
                                    elif (numColumnas != len(m1)):
                                        print ("Error. El numero de columnas debe ser igual al numero de filas de la primera matriz")
                                    else:
                                        dimensiones_adecuadas = True
                                elif (operacion == "d"):
                                    if(numFilas!=numColumnas):
                                        print("Error. La matriz debe ser cuadrada.")
                                    else:
                                        dimensiones_adecuadas = True
                                else:
                                    dimensiones_adecuadas = True
                                # Si tras las comprobaciones se vuelve a quedar a false, vuelvo a pedir el numero de filas y columnas
                                if(dimensiones_adecuadas == False):
                                    numFilas = pedirFilas()
                                    numColumnas = pedirColumnas()

                                
                        for j in range(numFilas):
                            fila_incorrecta = True
                            print ("Introduce la fila numero " + str(j+1) + ": (separa los valores por espacios)")
                            while(fila_incorrecta):
                                fila = input()
                                elemsFila = re.split(r'\s+', fila)
                                # elimino las posiciones que se pueden quedar vacias a causa del split
                                if(elemsFila[len(elemsFila)-1] == '' or elemsFila[0] == ''):
                                    elemsFila.remove('')
                                if (len(elemsFila)!= numColumnas):
                                    print("Error. Vuelva a introducir la fila. Recuerde: la fila debe tener " + str(numColumnas) + " elementos.")
                                else:
                                    fila_incorrecta = False
                            if(i == 0):
                                m1.append(convertFilaToInt(elemsFila))
                            else:
                                m2.append(convertFilaToInt(elemsFila))                                        
                    
                    if ( operacion == '+'):
                        resultado = client.sumaMatrices(m1, m2)
                    if ( operacion == '-'):
                        resultado = client.restaMatrices(m1, m2)
                    if ( operacion == '*'):
                        resultado = client.productoMatrices(m1, m2)
                    if ( operacion == 't'):
                        resultado = client.traspuesta(m1)
                    if ( operacion == 'd'):
                        resultado = client.determinante(m1)

                    print("Resultado:")
                    if(operacion != 'd' and operacion != 't'):
                        printMatriz(m1)
                        print("  " + operacion)
                        printMatriz(m2)
                        print("  =")
                        printMatriz(resultado)
                    elif(operacion == 'd' ):
                        printMatriz(m1)
                        print("Determinante: ")
                        print(resultado)
                    else:
                        printMatriz(m1)
                        print(" = ")
                        printMatriz(resultado)
                                           
            else:
                lectura_incorrecta = False  

    elif modo == "s":
        cont_reading = False

    

transport.close()
