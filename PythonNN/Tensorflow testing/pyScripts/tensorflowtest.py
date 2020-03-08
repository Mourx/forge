from __future__ import absolute_import, division, print_function, unicode_literals
import numpy
# TensorFlow and tf.keras
import os
import tensorflow as tf

from tensorflow import keras

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
#value -> numbers (if possible)

#match decks to their training data speed e.g.house party - 5 speed
#pad dataset (should be fine since all 60 cards
#each add adds a layer (just doing Dense because it's like my brain haha)
model = Sequential()
model.add(Dense(10, batch_size=1,input_shape=(60,9),return_sequences=True,statements=False,name="Input_Layer"))
model.add(Dense(10,name="Layer_1"))
model.add(Dense(10,name="Layer_2"))
model.add(activation="softmax",name="ACTIVATOR")

#to_cat serialises classification ( e.g. "on" values)
#sequential model

#model.add function
#train_test_split to do training sets test/train ratio of 10-20/80-90
model.compile(loss="categorical_crossentropy",optimizer="Adam",metrics=["accuracy"])
model.fit(X_train,Y_train,epochs=50,batch_size=1,verbose=2)
scores = model.evaluate(X_test,Y_test,batch_size=1,verbose=2)
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

