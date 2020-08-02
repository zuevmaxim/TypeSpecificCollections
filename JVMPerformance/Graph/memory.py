import math
import sys

import matplotlib.pyplot as plt

load_factor_std = 0.75
load_factor_open_addressing = 0.6
load_factor_chains = 0.6
alignment = 8
link = 4
k_size = 8
v_size = 8


def round_power_of_two(x):
    return pow(2, math.ceil(math.log2(x)))


def round_multiple(x, k):
    return x + (k - x % k) % k


def wrap_object(size):
    return round_multiple(12 + size, alignment)


def wrap_array(size):
    return wrap_object(4 + size)


def capacity(size, load_factor):
    return round_power_of_two(size / load_factor)


def std_memory(size):
    _capacity = capacity(size, load_factor_std)
    array = wrap_array(link * _capacity)
    node = wrap_object(5 * link + 4) + wrap_object(k_size) + wrap_object(v_size)
    fields = 6 * link + 4 + 4 * 4
    return array + size * node + wrap_object(fields)


def open_addressing_memory(size):
    _capacity = capacity(size, load_factor_open_addressing)
    links = wrap_object(3 * 4 + link) + wrap_array(8 * _capacity + 1)
    keys = wrap_array(k_size * _capacity)
    values = wrap_array(v_size * _capacity)
    fields = 6 * link + 6 * 4 + v_size
    return keys + values + links + wrap_object(fields) + wrap_object(link)


def chains_memory(size):
    _capacity = capacity(size, load_factor_chains)
    data_capacity = int(load_factor_chains * _capacity)
    pointers = wrap_array(4 * _capacity)
    keys = wrap_array(k_size * data_capacity)
    values = wrap_array(v_size * data_capacity)
    _next = wrap_array(4 * data_capacity)
    free = wrap_object(2 * 4 + link) + wrap_array(4 * data_capacity)
    links = wrap_object(3 * 4 + link) + wrap_array(8 * data_capacity)
    fields = 9 * link + 6 * 4
    return pointers + keys + values + _next + free + links + wrap_object(fields) + wrap_object(link)


def generate_sizes():
    return list(range(10, 10_000_000, 10))


def std_factor(_std_scores, scores):
    return [std_score / score for std_score, score in zip(_std_scores, scores)]


def mean(numbers):
    return sum(numbers) / len(numbers)


if __name__ == "__main__":
    save_dir = sys.argv[1]
    sizes = generate_sizes()

    std_scores = [std_memory(s) for s in sizes]
    open_addressing_scores = [open_addressing_memory(s) for s in sizes]
    chains_scores = [chains_memory(s) for s in sizes]

    std_factors = std_factor(std_scores, std_scores)
    open_addressing_factors = std_factor(std_scores, open_addressing_scores)
    chains_factors = std_factor(std_scores, chains_scores)

    print("Min open addressing factor: %f" % min(open_addressing_factors[100:]))
    print("Min chains factor: %f" % min(chains_factors[100:]))
    print("Mean open addressing factor: %f" % mean(open_addressing_factors[100:]))
    print("Mean chains factor: %f" % mean(chains_factors[100:]))
    print("Max open addressing factor: %f" % max(open_addressing_factors[100:]))
    print("Max chains factor: %f" % max(chains_factors[100:]))

    log_sizes = [math.log10(s) for s in sizes]

    plt.plot(log_sizes, std_factors, label="STD")
    plt.plot(log_sizes, open_addressing_factors, label="Open Addressing")
    plt.plot(log_sizes, chains_factors, label="Chained")

    plt.xlabel('log10 size')
    plt.ylabel('std memory factor')
    plt.legend()
    plt.savefig('%s/%s.png' % (save_dir, "MEMORY"))
    plt.clf()
