from gensim import models
import sys

w = models.KeyedVectors.load_word2vec_format(
    '../../GoogleNews-vectors-negative300.bin', binary=True)

categories = ['theft', 'violence', 'drugs']
word = sys.argv[1]

maxSimilarity = 0
crimeType = ""
for cat in categories:
    similarity = w.similarity(cat, word)
    print(cat, similarity)
    if (maxSimilarity < similarity):
        maxSimilarity = similarity
        crimeType = cat
print(crimeType)
