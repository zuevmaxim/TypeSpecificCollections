import math

import matplotlib.pyplot as plt


def read_file(file_name):
    f = open(file_name, 'r')
    results = {}
    f.readline()
    for line in f:
        _, sz, method, map_name, _, _, sc, _, _, _ = line.split()
        sz = int(sz)
        score = math.log10(float(sc) * sz)
        size = math.log10(sz)
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


def plot_method(method_results, method):
    for map_name, map_results in method_results.items():
        plot_map(map_results, map_name)
    plt.title(method)
    plt.legend()
    plt.xlabel('log10 size')
    plt.ylabel('log10 thrpt')
    plt.savefig('%s.png' % method)
    plt.clf()


def plot(results):
    for method, method_results in results.items():
        plot_method(method_results, method)


results = read_file("../build/reports/jmh/results.txt")
plot(results)
