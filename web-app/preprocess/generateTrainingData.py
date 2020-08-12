from gensim import models

def crimeTypeCategoryMap():
    categoryDict = {}
    categoryDict["theft"] = "Theft Crimes"
    categoryDict["shoplifting"] = "Theft Crimes"
    categoryDict["robbery"] = "Theft Crimes"
    categoryDict["burglary"] = "Theft Crimes"
    categoryDict["assault"] = "Violent Crimes"
    categoryDict["violent crime"] = "Violent Crimes"
    categoryDict["sexual assult"] = "Violent Crimes"
    categoryDict["arson"] = "Violent Crimes"
    categoryDict["disorderly conduct"] = "Disorderly Conduct"
    categoryDict["anti social behaviour"] = "Disorderly Conduct"
    categoryDict["public order"] = "Disorderly Conduct"
    categoryDict["disturb peace"] = "Disorderly Conduct"
    categoryDict["drug crime"] = "Statutory Crimes"
    categoryDict["alcohol violation"] = "Statutory Crimes"
    categoryDict["traffic offense"] = "Statutory Crimes"
    categoryDict["financial crime"] = "Statutory Crimes"
    categoryDict["other crime"] = "Other Crimes"
    categoryDict["juvenile crime"] = "Other Crimes"
    categoryDict["weapon possession"] = "Other Crimes"
    categoryDict["vandalism"] = "Other Crimes"
    
    return categoryDict



if __name__ == "__main__":
    model = models.KeyedVectors.load_word2vec_format(
        '../../GoogleNews-vectors-negative300.bin', binary=True)