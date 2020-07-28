#!/bin/bash

output_dir="JVMPerformance"
output_file="$output_dir/results.txt"
jar_path="JVMPerformance/build/libs/JVMPerformance-1.0-SNAPSHOT-jmh.jar"
benchmark_name="Map"

./gradlew --stop
./gradlew --no-daemon :JVMPerformance:clean :JVMPerformance:jmhJar &&
  java -jar $jar_path -rf text -rff $output_file .*$benchmark_name.* &&
  python3 JVMPerformance/Graph/graph.py $output_file $output_dir
