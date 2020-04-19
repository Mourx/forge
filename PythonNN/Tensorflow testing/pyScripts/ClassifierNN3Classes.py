from __future__ import absolute_import, division, print_function, unicode_literals
import numpy
# TensorFlow and tf.keras
import os
import tensorflow as tf
import DeckLoader as load
import json 
from tensorflow import keras
from keras import Sequential
from keras.layers import Dense, Flatten
from keras.models import model_from_json
# Helper libraries
import numpy as np
import matplotlib.pyplot as plt
from tensorflow.python.util import deprecation
from sklearn.model_selection import KFold
from keras.optimizers import SGD
from keras import regularizers
opt = SGD(lr=0.11)
num_folds = 5;
deprecation._PRINT_DEPRECATION_WARNINGS = False
decks = {}
classes = [1,2,3]
Y_train = numpy.array([])
count = 0
labels = json.load(open('C:/Users/Joel/Documents/Nottingham/Project/forge/PythonNN/Tensorflow testing/Labels/labelsArchetypes.json'))
for i in labels:
     label = numpy.array([])
     labelIN = labels[i]
     for e in classes:
          if e == labelIN:
               label = numpy.append(label,1)
          else:
               label = numpy.append(label,0)
     Y_train = numpy.append(Y_train,label)
     count += 1

Y_train = numpy.reshape(Y_train,(count,3))
#Y_train = Y_total[:250]
#print(Y_train.shape)
for file in os.listdir('C:/Users/Joel/Documents/Nottingham/Project/forge/PythonNN/Tensorflow testing/Decks'):   
     decks[file] = file

#print(decks)
#deck = load.GetDeck('C:/Users/Joel/Documents/Nottingham/Project/forge/PythonNN/Tensorflow testing/Decks/'+file)
X_train = numpy.array([])
count = 0
for e in decks:
     data = numpy.array([])
     data = load.GetDeck('C:/Users/Joel/Documents/Nottingham/Project/forge/PythonNN/Tensorflow testing/Decks/'+e)
     data = numpy.reshape(data,(60,522))
     X_train = numpy.append(X_train,data)
     count = count+1
X_train = numpy.reshape(X_train,(count,60*522))
print(tf.__version__)
models = []
mScores = []

def NewModel():
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

     #X_train = X_train[:250]
     print(X_train.shape)
     #print(X_train)
     #match decks to their training data speed e.g.house party - 5 speed
          #matched via dictionary labels
     #pad dataset (should be fine since all 60 cards
     #each add adds a layer (just doing Dense because it's like my brain haha)
     #X_trainShape = numpy.reshape(X_train,(60,8))
     print('done')
     kfold = KFold(n_splits=num_folds, shuffle=True)
     fold_no = 1
     for train, test in kfold.split(X_train,Y_train):
          model = Sequential()
          model.add(Dense(300,input_dim=(60*522),name="Input_Layer",activation="sigmoid"))
          model.add(Dense(250,name="Hidden"))
          model.add(Dense(180,name="Hidden2"))
          model.add(Dense(100,name="Hidden3", activity_regularizer=regularizers.l1(0.001)))
          model.add(Dense(3,name="Output",activation="softmax"))
          model.summary()
          #to_cat serialises classification ( e.g. "on" values)
          #sequential model
          print('starting stuff')
          #model.add function
          #train_test_split to do training sets test/train ratio of 10-20/80-90
          print(X_train.shape)
          model.compile(loss="categorical_crossentropy",optimizer="adam",metrics=["accuracy"])
          model.fit(X_train[train],Y_train[train],epochs=100,batch_size=150,verbose=2)
          scores = model.evaluate(X_train[test],Y_train[test],batch_size=150,verbose=0)
          print("Accuracy: %.2f%%" % scores[1]*100,flush=True)
          fold_no = fold_no + 1
          models.append(model)
          mScores.append(scores[1])
     #model.compile
     #loss e.g. rms
     #optimiser
     #metrics
     #model.fit (verbose = 2) for less warnings
     bestIndex = 0
     currIndex = 0
     for s in mScores:
          if mScores[currIndex] > mScores[bestIndex]:
               bestIndex = currIndex
          currIndex += 1

     model = models[bestIndex]
     model.fit(X_train,Y_train,epochs=50,batch_size=128,verbose=2)
     scores = model.evaluate(X_train,Y_train,batch_size=128,verbose=0)
     print("Accuracy: %.2f%%" % scores[1]*100,flush=True)   
     #eval model
     #model.evaluate
     #xtest, ytest, batch size, verbose 2
     #print them out
     print(mScores)
     print(mScores[bestIndex])
     print(scores[1])
     #save model somewhere model = JSON h5 = weights
     model_json = model.to_json()
     with open("model.json",'w') as json_file:
          json_file.write(model_json)
     model.save_weights('model.h5')
     print('SAVED MODEL')

     
def LoadModel():
     json_file = open('model.json', 'r')
     loaded_model_json = json_file.read()
     json_file.close()
     loaded_model = model_from_json(loaded_model_json)
     # load weights into new model
     loaded_model.load_weights("model.h5")
     print("Loaded model from disk")
     loaded_model.compile(loss="categorical_crossentropy",optimizer="Adam",metrics=["accuracy"])
     scores = loaded_model.evaluate(X_train,Y_train,batch_size=128,verbose=2)
     print("Accuracy: %.2f%%" % scores[1]*100,flush=True)
     arr = loaded_model.predict(X_train)
     newArr = numpy.array([])
     print(Y_train)
     for i in arr:
          print(i)
          print(numpy.argmax(i))
          newArr = numpy.append(newArr,numpy.argmax(i))
     print(newArr)
     print(labels)
     counts = {0:0,1:0,2:0}
     results = {0:0,1:0,2:0};
     labels2 = numpy.array([])
     for i in labels:
          labels2 = numpy.append(labels2,labels[i])
          counts[labels[i]-1] += 1
     for i in range(0,268):
          if newArr[i]+1 == labels2[i]:
                results[newArr[i]] += 1
     print(counts)
     print(results)
     

NewModel()
