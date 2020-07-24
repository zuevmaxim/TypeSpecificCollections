import math
import os
import sys

import matplotlib.pyplot as plt


def read_file(file_name):
    f = open(file_name, 'r')
    results = {}
    f.readline()
    for line in f:
        name, sz, method, map_name, _, _, sc, _, _, _ = line.split()
        method = name + ' ' + method
        size = math.log10(int(sz))
        score = float(sc)
        if method not in results:
            results[method] = {}
        method_results = results[method]
        if map_name not in method_results:
            method_results[map_name] = []
        map_results = method_results[map_name]
        map_results.append((size, score))
    return results


def plot_map(map_results, map_name):
    sizes, scores = zip(*map_results)
    plt.plot(sizes, scores, 'o--', label=map_name)


def plot_method(method_results, method, save_dir):
    for map_name, map_results in method_results.items():
        plot_map(map_results, map_name)
    plt.title(method)
    plt.legend()
    plt.xlabel('log10 size')
    plt.ylabel('Throughput, ops/s')
    plt.savefig('%s/%s.png' % (save_dir, method))
    plt.clf()


def plot(results, save_dir):
    for method, method_results in results.items():
        plot_method(method_results, method, save_dir)


if __name__ == "__main__":
    file_name = sys.argv[1]
    save_dir = sys.argv[2] if len(sys.argv) >= 3 else os.curdir
    results = read_file(file_name)
    plot(results, save_dir)
