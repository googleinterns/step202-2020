from gensim import models
import sys
import nltk
import string
from nltk.corpus import stopwords
from nltk.stem import WordNetLemmatizer


def simplifyText(crimeType):
    crimeType = crimeType.lower()
    crimeTypeWords = nltk.word_tokenize(crimeType)
    crimeTypeWordsNoPunct = [word for word in crimeTypeWords if word not in string.punctuation]
    crimeTypeWordsNoStopwords = [word for word in crimeTypeWordsNoPunct if word not in stopwords.words('english')]
    lemmatizer = WordNetLemmatizer()
    lemmatizedCrimeTypeWords = [lemmatizer.lemmatize(word) for word in crimeTypeWordsNoStopwords]
    
    return lemmatizedCrimeTypeWords

#w = models.KeyedVectors.load_word2vec_format(
#    '../../GoogleNews-vectors-negative300.bin', binary=True)

categories = ['theft', 'violence', 'drugs']
word = sys.argv[1]

simplifyText(word)
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
