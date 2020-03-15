import json

data = [] #lines of json for the deck
names = json.load(open('names.json')) #name dictionary
mcosts = json.load(open('mana_costs.json')) #mcosts dictionary
cmc = [] #int list
types = json.load(open('types.json')) # typing dictionary
pColors = ['W','U','B','R','G']
colors = '1' # string that reps a 5 digits binary chromosome (WUBRG) activation
fullDeck = []

for line in open('Arcane Tempo.json'):
    data.append(json.loads(line))


for elements in data:
    
    
    # enter names into the dictionary, get UID out
    name = elements['name']
    if name not in names:
        names[name] = len(names)
    eName = names[name]
    
    # enter mana costs (non converted) into dictionary, get UID out
    mana_cost = elements['mana_cost']
    if mana_cost not in mcosts:
        mcosts[mana_cost] = len(mcosts)
    eMana_cost = mcosts[mana_cost]
    
    # cmc is simple conversion to int
    cmc = int(elements['cmc'])
    
    #type_line processing - split terms up?

    #colors as binary chromosomes for WUBRG
    colors = '1'
    color = elements['colors']
    for c in pColors:
        if c in color:
            colors = colors + '1'
        else:
            colors = colors + '0'
    eColors = int(colors)

    # clean power from special effects
    ePower = 0
    if 'power' in elements:
        power = elements['power']
        power = power.replace('*','')
        power = power.replace('+','')
        if power == '':
            ePower = 0
        else:
            ePower = int(power)
        
    # clean toughness from special effects
    eTough = 0
    if 'toughness' in elements:        
        tough = elements['toughness']
        tough = tough.replace('*','')
        tough = tough.replace('+','')
        if tough == '':
            eTough = 0
        else:
            eTough = int(tough)
    #make a list
    totals = {}
    totals['name'] = eName
    totals['mana_cost'] = eMana_cost
    totals['cmc'] = cmc
    #totals['type_line'] = eType_line
    totals['colors'] = eColors
    totals['power'] = ePower
    totals['toughness'] = eTough
    
    fullDeck.append(totals)

print(fullDeck)
with open('names.json','w') as out:
    json.dump(names,out)
with open('mana_costs.json','w') as out:
    json.dump(mcosts,out)
#with open('types.json','w') as out:
    #json.dump(types,out)

