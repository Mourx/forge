import json

person = '{"name": "Bob", "languages": ["English","French"]}'
person_dict = json.loads(person)

print(person_dict)

print(person_dict['languages'])

data = []
for line in open('Arcane Tempo.json'):
    data.append(json.loads(line))



print(data[0]['name'])


from keras.preprocessing.text import text_to_word_sequence
from keras.preprocessing.text import one_hot
from keras.preprocessing.text import hashing_trick

for line in data:
    name = line['name']
    print(name)
    words = set(text_to_word_sequence(name))
    vocab_size = len(words)
    print(vocab_size,words)
    result = one_hot(name, round(vocab_size*1.3))
    print(result)

    result = hashing_trick(name, round(vocab_size*1.3), hash_function='md5')
    print(result)

#input type: [nameValue,manaCost,cmc,Type,Color,Power,Toughness,keywordsBin]
