import sys
import nltk
import string
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
import os
import pickle
import numpy as np
import fasttext.util


def simplifyText(crimeType):
    crimeTypeLower = crimeType.lower()
    crimeTypeNoHyphen = crimeTypeLower.replace('-', ' ')
    crimeTypeNoPunct = crimeTypeNoHyphen.translate(str.maketrans("", "", string.punctuation))

    crimeTypeWords = nltk.word_tokenize(crimeTypeNoPunct)
    crimeTypeWordsNoStopwords = [word for word in crimeTypeWords if word not in stopwords.words('english')]
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
    ft = fasttext.load_model('cc.en.300.bin')
    for i in range(len(lemmatizedCrimeTypeWords)):
        if i == 0:
            wordVector = np.copy(ft.get_word_vector(lemmatizedCrimeTypeWords[i]))
        else: wordVector += ft.get_word_vector(lemmatizedCrimeTypeWords[i])
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
