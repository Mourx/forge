from __future__ import absolute_import, division, print_function, unicode_literals
import numpy
# TensorFlow and tf.keras
import os
import tensorflow as tf
import DeckLoader as load
import json 
from tensorflow import keras
from keras import Sequential
from keras.layers import Dense
# Helper libraries
import numpy as np
import matplotlib.pyplot as plt
from tensorflow.python.util import deprecation
deprecation._PRINT_DEPRECATION_WARNINGS = False

print(tf.__version__)
#Array of classes
#(0,1,2,3,4,5,6,7,8,9,10)
Y_labels = []
#random seed

#collect data
#load into python
#pre-process
#names -> dictionary
#value -> numbers
# Pre processing done in GetDeck
decks = {}
Y_train =[]
labels = json.load(open('C:/Users/Joel/Documents/Nottingham/Project/forge/PythonNN/Tensorflow testing/Labels/labels.json'))
for i in labels:
     Y_train.append(labels[i])
#print(labels)

for file in os.listdir('C:/Users/Joel/Documents/Nottingham/Project/forge/PythonNN/Tensorflow testing/Decks'):   
     decks[file] = file

#print(decks)
#deck = load.GetDeck('C:/Users/Joel/Documents/Nottingham/Project/forge/PythonNN/Tensorflow testing/Decks/'+file)
X_train = []
for e in decks:
     X_train.append(load.GetDeck('C:/Users/Joel/Documents/Nottingham/Project/forge/PythonNN/Tensorflow testing/Decks/'+e))
               
#match decks to their training data speed e.g.house party - 5 speed
     #matched via dictionary labels
#pad dataset (should be fine since all 60 cards
#each add adds a layer (just doing Dense because it's like my brain haha)
model = Sequential()
model.add(Dense(1,batch_size=1,input_shape=(60,8),name="Input_Layer", activation='relu'))
model.add(Dense(1,name="Hidden",activation='relu'))
model.add(Dense(1,name="Output",activation='sigmoid'))
#to_cat serialises classification ( e.g. "on" values)
#sequential model
print('starting stuff')
#model.add function
#train_test_split to do training sets test/train ratio of 10-20/80-90
model.compile(loss="categorical_crossentropy",optimizer="Adam",metrics=["accuracy"])
model.fit(X_train,Y_train,epochs=50,batch_size=10,verbose=2)
scores = model.evaluate(X_test,Y_test,batch_size=10,verbose=2)
print("Accuracy: %.2f%%" % scores[1]*100,flush=true)
#model.compile
#loss e.g. rms
#optimiser
#metrics
#model.fit (verbose = 2) for less warnings 


#eval model
#model.evaluate
#xtest, ytest, batch size, verbose 2
#print them out

#save model somewhere model = JSON h5 = weights

