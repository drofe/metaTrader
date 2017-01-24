#!/bin/bash

protoc --java_out=src/main/java/ definitions/metaTraderMessages.proto 
