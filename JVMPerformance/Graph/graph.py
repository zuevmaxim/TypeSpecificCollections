import json
import math
import os
import sys

import matplotlib.pyplot as plt

results = {}


def read_file_txt(file_name):
    results.clear()
    with open(file_name, 'r') as f:
        f.readline()
        for line in f:
            s = line.split()
            if len(s) == 8:
                name, sz, _, _, sc, _, _, m = s
                y_label = 'Throughput, ' + m
                name, map_name = name.split('.')
            elif len(s) == 7:
                name, sz, method, map_name, _, sc, m = s
                y_label = m
            else:
                raise Exception("Unexpected line size")
            fill_result(name, "get", sz, map_name, sc, y_label)


def read_file_json(file_name):
    with open(file_name) as f:
        data = json.load(f)
    for res in data:
        name = res["benchmark"]
        name, map_name = name.split('.')[3:5]
        params = res["params"]
        sz = params["size"]
        metric = res["primaryMetric"]
        sc = metric["score"]
        m = metric["scoreUnit"]
        y_label = 'Throughput, ' + m
        fill_result(name, "get", sz, map_name[3:], sc, y_label)


def fill_result(name, method, sz, map_name, sc, y_label):
    method = name + ' ' + method
    size = math.log10(int(sz))
    score = float(sc)
    if (method, y_label) not in results:
        results[(method, y_label)] = {}
    method_results = results[(method, y_label)]
    if map_name not in method_results:
        method_results[map_name] = []
    map_results = method_results[map_name]
    map_results.append((size, score))


def plot_map(map_results, map_name):
    sizes, scores = zip(*map_results)
    plt.plot(sizes, scores, 'o--', label=map_name)


def plot_method(method_results, method, save_dir, y_label):
    for map_name, map_results in method_results.items():
        plot_map(map_results, map_name)
    plt.title(method)
    plt.legend()
    plt.xlabel('log10 size')
    plt.ylabel(y_label)
    plt.savefig('%s/%s.png' % (save_dir, method))
    plt.clf()


def plot(save_dir):
    for method, method_results in results.items():
        method, y_label = method
        plot_method(method_results, method, save_dir, y_label)


if __name__ == "__main__":
    file_name = sys.argv[1]
    save_dir = sys.argv[2] if len(sys.argv) >= 3 else os.curdir
    if file_name.endswith(".txt"):
        read_file_txt(file_name)
    elif file_name.endswith(".json"):
        read_file_json(file_name)
    else:
        raise Exception("Unexpected file format.")
    plot(save_dir)
