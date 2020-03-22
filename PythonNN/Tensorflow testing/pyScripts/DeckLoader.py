import json

data = [] #lines of json for the deck
names = json.load(open('names.json')) #name dictionary
mcosts = json.load(open('mana_costs.json')) #mcosts dictionary
cmc = [] #int list
supers = json.load(open('supertypes.json'))
types = json.load(open('types.json')) # typing dicionary
subs = json.load(open('subtypes.json'))
pColors = ['W','U','B','R','G']
colors = '1' # string that reps a 5 digits binary chromosome (WUBRG) activation
keywords = json.load(open('keywords.json'))

fullDeck = []

def GetDeck(string):
    for line in open(string):
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
        typeline = '1'
        words = elements['type_line']
        words = words.replace("-","")
        words = words.replace("  "," ")
        wordArray = words.split(" ")
        #supertypes
        for t in supers:
            if t in wordArray:
                typeline = typeline + '1'
                wordArray.remove(t)
            else:
                typeline = typeline + "0"
        #regular types
        for t in types:
            if t in wordArray:
                typeline = typeline + '1'
                wordArray.remove(t)
            else:
                typeline = typeline + "0"
        eString = ''
        if len(wordArray) == 0:
            typeline = typeline + "000000000000"
        else:
            for s in subs:
                if s in wordArray:
                    string = str(subs[s])
                    if len(string) == 1:
                        string = '00' + string
                    elif len(string) == 2:
                        string = '0' + string
                    eString = eString + string
        while len(eString) < 12:
            eString = eString + '0'
        typeline = typeline + eString
        if len(typeline) == 25:
            #print('TypeLine correct length')
            eType_line = int(typeline)
        else:
            eType_line = 1000000000000000000000000
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

        # process keywords
        kwords = elements['keywords']
        keywordTags = '1'
        for k in keywords:
            if k in kwords:
                keywordTags = keywordTags + '1'
            else:
                keywordTags = keywordTags + '0' 
        eKeywords = int(keywordTags)
        
        #make a list
        totals = {}
        totals['name'] = eName
        totals['mana_cost'] = eMana_cost
        totals['cmc'] = cmc
        totals['type_line'] = eType_line
        totals['colors'] = eColors
        totals['power'] = ePower
        totals['toughness'] = eTough
        totals['keywords'] = eKeywords
        
        fullDeck.append(eName)
        fullDeck.append(eMana_cost)
        fullDeck.append(cmc)
        fullDeck.append(eType_line)
        fullDeck.append(eColors)
        fullDeck.append(ePower)
        fullDeck.append(eTough)
        fullDeck.append(eKeywords)
        
    return fullDeck
    print(fullDeck)
    #with open('fulldeck.json','w') as out:
    #    json.dump(fullDeck,out)
    #    
    #with open('names.json','w') as out:
    #    json.dump(names,out)
    #with open('mana_costs.json','w') as out:
    #    json.dump(mcosts,out)

GetDeck('Arcane Tempo.json')
   

