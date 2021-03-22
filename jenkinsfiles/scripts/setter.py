#!/user/bin/python3

import sys
import os

from ruamel.yaml import YAML

yaml = YAML()

if len(sys.argv) != 4:
    print('Usage: %s "/path/to/yaml.yml" "name.of.key.to.change" "newValue" % sys.argv[0]')
    os.exit(1)

file = sys.argv[1]
elems = sys.argv[2].split(".")
insert = sys.argv[3]

with open(file, 'r') as stream:
    data_loaded = yaml.load(stream)

subtree = data_loaded

while len(elems) > 1:
    key = elems[0]
    if key not in subtree:
        subtree[key] = {}
    subtree = subtree[key]
    elems = elems[1:]

subtree[elems[0]] = insert

with open(file, "w") as stream:
    yaml.dump(data_loaded, stream)