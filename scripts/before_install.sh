#!/bin/bash

openssl aes-256-cbc -K $encrypted_77bee7102036_key -iv $encrypted_77bee7102036_iv -in secrets.tar.enc -out secrets.tar -d
tar xvf secrets.tar
