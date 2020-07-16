#!/bin/bash

output_dir="Performance"
output_file="$output_dir/results.txt"
jar_path="Performance/build/libs/Performance-1.0-SNAPSHOT-jmh.jar"
benchmark_name="MapBenchmark"

./gradlew --stop
./gradlew --no-daemon :Performance:clean :Performance:jmhJar &&
  java -jar $jar_path -rf text -rff $output_file .*$benchmark_name.* &&
  python3 Performance/Graph/graph.py $output_file $output_dir
