import sys
import nltk
import string
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer
import os
import pickle
import numpy as np
import fasttext.util
from scipy.spatial.distance import cosine
# Remove "other" from list of stopwords in order to classify crime types such as "other crime", "etc"

def simplifyText(crimeType):
    crimeTypeLower = crimeType.lower()
    crimeTypeNoHyphen = crimeTypeLower.replace('-', ' ')
    crimeTypeNoPunct = crimeTypeNoHyphen.translate(str.maketrans("", "", string.punctuation))

    crimeTypeWords = nltk.word_tokenize(crimeTypeNoPunct)
    crimeTypeWordsNoStopwords = [word for word in crimeTypeWords if word not in stopwords.words('english')]
    lemmatizer = WordNetLemmatizer()
    lemmatizedCrimeTypeWords = [lemmatizer.lemmatize(word) for word in crimeTypeWordsNoStopwords]
    
    return lemmatizedCrimeTypeWords

def saveDict(dictName, dictToSave):
    with open(dictName + '.pkl', 'wb') as f:
        pickle.dump(dictToSave, f, pickle.HIGHEST_PROTOCOL)

def loadpkl(filename):
     with open(filename + '.pkl', 'rb') as f:
        return pickle.load(f)

def loadDict():
    if (os.path.isfile('categoryClassificationDict.pkl')):
        return loadpkl('categoryClassificationDict')
    return {}

def generateWordVector(lemmatizedCrimeTypeWords, ft):
    for i in range(len(lemmatizedCrimeTypeWords)):
        if i == 0:
            wordVector = np.copy(ft.get_word_vector(lemmatizedCrimeTypeWords[i]))
        else: wordVector += ft.get_word_vector(lemmatizedCrimeTypeWords[i])
    return wordVector

def nearestNeighbor(crimeTypeWordVector):
    categoryDict = loadpkl("categoryDict")
    crimeTypeWordVectorsDict = loadpkl("crimeTypeWordVectorsDict")

    nearest = None
    maxCosSimilarity = 0
    for crimeType in categoryDict:
        cosSimilarity = 1 - cosine(crimeTypeWordVectorsDict[crimeType], crimeTypeWordVector)
        if cosSimilarity > maxCosSimilarity:
            maxCosSimilarity = cosSimilarity
            nearest = categoryDict[crimeType]
    return nearest

def classify(crimeType):
    lemmatizedCrimeTypeWords = simplifyText(crimeType)
    lemmatizedCrimeType = ' '.join(lemmatizedCrimeTypeWords)    

    categoryClassificationDict = loadDict()
    matchingCategory = categoryClassificationDict.get(lemmatizedCrimeType, None)
    if matchingCategory != None:
        return matchingCategory

    ft = fasttext.load_model('cc.en.300.bin')
    crimeTypeWordVector = generateWordVector(lemmatizedCrimeTypeWords, ft)
    classifiedCategory = nearestNeighbor(crimeTypeWordVector)
    
    categoryClassificationDict[lemmatizedCrimeType] = classifiedCategory
    saveDict("categoryClassificationDict", categoryClassificationDict)

    return classifiedCategory
