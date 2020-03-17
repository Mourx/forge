import json

types = open('subtypes.txt').read()
print(types)
jsonD = {}
index = 1

with open('subtypes.txt') as file:
    for line in file:
        line = line.replace("\n", "")
        jsonD[line] = index
        index += 1

print(json)

with open('subtypes.json','w') as out:
    json.dump(jsonD,out)
