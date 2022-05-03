echo
echo "Lanzando el cliente"
echo
java -cp . -Djava.security.policy=server.policy Cliente localhost 1099 $1 
