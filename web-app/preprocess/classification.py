from gensim import models
import sys
import nltk
import string
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
import os
import pickle
import numpy as np


def simplifyText(crimeType):
    crimeType = crimeType.lower()
    crimeTypeWords = nltk.word_tokenize(crimeType)
    crimeTypeWordsNoPunct = [word for word in crimeTypeWords if word not in string.punctuation]
    crimeTypeWordsNoStopwords = [word for word in crimeTypeWordsNoPunct if word not in stopwords.words('english')]
    lemmatizer = WordNetLemmatizer()
    lemmatizedCrimeTypeWords = [lemmatizer.lemmatize(word) for word in crimeTypeWordsNoStopwords]
    
    return lemmatizedCrimeTypeWords

def saveDict(categoryDict):
    with open('categoryDict.pkl', 'wb') as f:
        pickle.dump(categoryDict, f, pickle.HIGHEST_PROTOCOL)

def loadDict():
    if (os.path.isfile('categoryDict.pkl')):
        with open('categoryDict.pkl', 'rb') as f:
            return pickle.load(f)
    return {}

def generateWordVector(lemmatizedCrimeTypeWords):
    model = models.KeyedVectors.load_word2vec_format(
        '../../GoogleNews-vectors-negative300.bin', binary=True)
    for i in range(len(lemmatizedCrimeTypeWords)):
        if i == 0:
            wordVector = np.copy(model[lemmatizedCrimeTypeWords[i]])
        else: wordVector += model[lemmatizedCrimeTypeWords[i]]
        
    return wordVector

def classify(crimeType):
    lemmatizedCrimeTypeWords = simplifyText(crimeType)
    lemmatizedCrimeType = ' '.join(lemmatizedCrimeTypeWords)    

    categoryDict = loadDict()
    matchingCategory = categoryDict.get(lemmatizedCrimeType, None)
    if matchingCategory != None:
        return matchingCategory

    crimeTypeWordVector = generateWordVector(lemmatizedCrimeTypeWords)
    #classifiedCategory = knn(crimeTypeWordVector)

    #categoryDict[lemmatizedCrimeType] = classifiedCategory
    #saveDict(categoryDict)

    #return classifiedCategory

categories = ['theft', 'violence', 'drugs']
word = sys.argv[1]

classify(word)
"""
maxSimilarity = 0
crimeType = ""
for cat in categories:
    similarity = w.similarity(cat, word)
    print(cat, similarity)
    if (maxSimilarity < similarity):
        maxSimilarity = similarity
        crimeType = cat
print(crimeType)"""
