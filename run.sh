#!/bin/bash
# Compile and run the Hotel and Residence Reservation Application

echo "Compiling sources..."
mkdir -p bin
find src -name "*.java" | xargs javac -cp "lib/*" -d bin

if [ $? -eq 0 ]; then
    echo "Compilation successful. Starting application..."
    java -cp "bin:lib/*" hotel.Main
else
    echo "Compilation failed."
    exit 1
fi
