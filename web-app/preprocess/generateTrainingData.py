from classification import generateWordVector, saveDict
import fasttext.util

def crimeTypeCategoryMap():
    categoryDict = {}
    categoryDict["theft"] = "Theft Crimes"
    categoryDict["vehicle crime"] = "Theft Crimes"
    categoryDict["burglary"] = "Theft Crimes"
    categoryDict["larceny"] = "Theft Crimes"
    categoryDict["assault"] = "Violent Crimes"
    categoryDict["violent crime"] = "Violent Crimes"
    categoryDict["arson"] = "Violent Crimes"
    categoryDict["robbery"] = "Violent Crimes"
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
    categoryDict = crimeTypeCategoryMap()

    ft = fasttext.load_model('cc.en.300.bin')
    crimeTypeWordVectorsDict = {}

    for crimeType in categoryDict:
        crimeTypeWords = crimeType.split()
        crimeTypeWordVector = generateWordVector(crimeTypeWords, ft)
        crimeTypeWordVectorsDict[crimeType] = crimeTypeWordVector

    saveDict("categoryDict", categoryDict)
    saveDict("crimeTypeWordVectorsDict", crimeTypeWordVectorsDict)
