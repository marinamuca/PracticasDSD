#!/bin/sh -e
# ejecutar = Macro para compilacion y ejecucion del programa ejemplo # en una sola maquina Unix de nombre localhost.

echo "Compilando con javac ..." 
javac *.java
sleep 2
echo
echo "Lanzando el servidor"
java -cp . -Djava.rmi.server.codebase=file:./ -Djava.rmi.server.hostname=localhost -Djava.security.policy=server.policy Servidor localhost 1099 $1
sleep 2
