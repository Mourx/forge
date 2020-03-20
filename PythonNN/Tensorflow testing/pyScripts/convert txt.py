import json

types = open('keywords.txt').read()
print(types)
jsonD = {}
index = 1

with open('keywords.txt') as file:
    for line in file:
        line = line.replace("\n", "")
        jsonD[line] = index
        index += 1

print(json)

with open('keywords.json','w') as out:
    json.dump(jsonD,out)
