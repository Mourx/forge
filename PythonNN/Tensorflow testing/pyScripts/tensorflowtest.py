from __future__ import absolute_import, division, print_function, unicode_literals

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
'''
model = keras.Sequential([
    keras.layers.Flatten(input_shape=(28, 28)),
    keras.layers.Dense(128, activation='relu'),
    keras.layers.Dense(10, activation='softmax')
])

model.compile(optimizer='adam',
              loss='sparse_categorical_crossentropy',
              metrics=['accuracy'])

test_loss, test_acc = model.evaluate(test_images,  test_labels, verbose=2)
'''

def show_batch(dataset):
  for batch, label in dataset.take(1):
    for key, value in batch.items():
      print("{:20s}: {}".format(key,value.numpy()))

def getDataset(file_path, **kwargs):
    dataset = tf.data.experimental.make_csv_dataset(
        file_path,
        batch_size = 4,
        label_name="score",
        na_value="?",
        num_epochs=1,
        ignore_errors = True,
        **kwargs)
    return dataset

raw_train_data = getDataset("inputs.csv")
show_batch(raw_train_data)
