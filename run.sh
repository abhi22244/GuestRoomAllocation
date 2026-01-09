#!/bin/bash

# Build and run the Hotel Room Allocation System

# Exit immediately if a command exits with a non-zero status
set -e

echo "Building the application..."
mvn clean package

echo "Starting the application..."
java -jar target/room-allocation-1.0.0.jar