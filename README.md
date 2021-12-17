# AWS S3 downlaod 

Mininal test for S3 downlaod benchmarking 

## Get started 

In the project root type:

    ./gradlew run --args="s3://nextflow-ci/hello.txt $PWD//hello.txt"

Replace source and target file with your the ones of your choice.

Use `AWS_DEFAULT_REGION` env variable to set the AWS region to use, default `eu-west-1`. 

## Packing  

Use the following command to pack:

    ./gradlew shadowJar


Resulting JAR will be located at:

     ./build/libs/aws-s3-download-all.jar 

Run it using 


    java -jar ./build/libs/aws-s3-download-all.jar <SOURCE S3> <LOCAL PATH>


## Download big file

    AWS_DEFAULT_REGION=us-east-1 java -jar ./build/libs/aws-s3-download-all.jar \
       s3://sra-pub-src-2/ERR1305737/m151221_184918_00127_c100958412550000001823217206251660_s1_p0.1.bax.h5.1 \
       sample.bax 

## Use Flight recorder


    AWS_DEFAULT_REGION=us-east-1 \
       java -XX:+FlightRecorder -XX:StartFlightRecording=duration=100s,filename=flight.jfr \
      -jar ./build/libs/aws-s3-download-all.jar \
       s3://sra-pub-src-2/ERR1305737/m151221_184918_00127_c100958412550000001823217206251660_s1_p0.1.bax.h5.1 \
       sample.bax 

