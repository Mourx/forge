import json

data = []
names = json.load(open(names.json))
mcosts = json.load(open('mana_costs.json'))
types = json.load(open('types.json'))



for line in open('Assassin.json'):
    data.append(json.loads(line))
    
for elements in data:
    name = elements['name']
    if name not in names:
        names[name] = len(names)
    mana_cost = elements['mana_cost']
    if mana_cost not in mcosts:
        mcosts[mana_cost] = len(mcosts)
    cmc = int(elements['cmc'])
    #type_line processing - split terms up?
    
    
with open('names.json','w') as out:
    json.dump(names,out)


