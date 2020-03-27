import json
import numpy




def GetDeck(string):
    data = [] #lines of json for the deck
    file = open('names.json')
    names = json.load(file) #name dictionary
    file.close()
    file = open('mana_costs.json')
    mcosts = json.load(file) #mcosts dictionary
    file.close()
    cmc = [] #int list
    supers = json.load(open('supertypes.json'))
    types = json.load(open('types.json')) # typing dicionary
    subs = json.load(open('subtypes.json'))
    pColors = ['W','U','B','R','G']
    colors = '1' # string that reps a 5 digits binary chromosome (WUBRG) activation
    keywords = json.load(open('keywords.json'))
    fullDeck = numpy.array([])
    for line in open(string):
        data.append(json.loads(line))


    for elements in data:
        fDeck = numpy.array([])
        
        # enter names into the dictionary, get UID out
        name = elements['name']
        if name not in names:
            names[name] = len(names)
        eName = names[name]
        fDeck = numpy.append(fDeck,eName)
        # enter mana costs (non converted) into dictionary, get UID out
        mana_cost = elements['mana_cost']
        if mana_cost not in mcosts:
            mcosts[mana_cost] = len(mcosts)
        eMana_cost = mcosts[mana_cost]
        fDeck = numpy.append(fDeck,eMana_cost)
        # cmc is simple conversion to int
        cmc = int(elements['cmc'])
        fDeck = numpy.append(fDeck,cmc)
        #type_line processing - split terms up?
        typeline = '1'
        words = elements['type_line']
        words = words.replace("-","")
        words = words.replace("  "," ")
        wordArray = words.split(" ")
        #supertypes
        for t in supers:
            val = 0
            if t in wordArray:
                val = 1
                typeline = typeline + '1'
                wordArray.remove(t)
            else:
                typeline = typeline + "0"
            fDeck = numpy.append(fDeck,val)
        #regular types
        for t in types:
            val = 0
            if t in wordArray:
                val = 1
                wordArray.remove(t)
            fDeck = numpy.append(fDeck,val)
        else:
            for s in subs:
                if s in wordArray:
                    fDeck = numpy.append(fDeck,subs[s])
                else:
                    fDeck = numpy.append(fDeck,0)
        if len(typeline) == 25:
            #print('TypeLine correct length')
            eType_line = int(typeline)
        else:
            eType_line = 1000000000000000000000000
        #colors as binary chromosomes for WUBRG
        colors = '1'
        color = elements['colors']
        for c in pColors:
            val = 0
            if c in color:
                val = 1
                colors = colors + '1'
            else:
                colors = colors + '0'
            fDeck = numpy.append(fDeck,val)
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
            val = 0
            if k in kwords:
                val = 1
                keywordTags = keywordTags + '1'
            else:
                keywordTags = keywordTags + '0'
            fDeck = numpy.append(fDeck,val)
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

        fDeck = numpy.append(fDeck,ePower)
        fDeck = numpy.append(fDeck,eTough)
        fullDeck = numpy.append(fullDeck,[fDeck])

        
            
        #with open('fulldeck.json','w') as out:
        #    json.dump(fullDeck,out)
        #    
        # open('names.json','w') as out:
        #    json.dump(names,out)
        #with open('mana_costs.json','w') as out:
        #    json.dump(mcosts,out)
    return fullDeck


dck = GetDeck('Angrath, Minotaur Pirate.json')
#print(dck.shape)
#print(dck)

