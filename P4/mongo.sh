if [ $1 = "start" ] || [ $1 = "stop" ]; then
    brew services $1 mongodb-community
fi
