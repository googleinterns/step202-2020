from classification import generateWordVector, saveDict
import fasttext.util


def crimeTypeCategoryMap():
    categoryDict = {
        "theft": "Theft Crimes",
        "vehicle crime": "Theft Crimes",
        "burglary": "Theft Crimes",
        "larceny": "Theft Crimes",
        "assault": "Violent Crimes",
        "arson": "Violent Crimes",
        "violent crime": "Violent Crimes",
        "robbery": "Violent Crimes",
        "disorderly conduct": "Disorderly Conduct",
        "anti social behaviour": "Disorderly Conduct",
        "public order": "Disorderly Conduct",
        "disturb peace": "Disorderly Conduct",
        "drug": "Statutory Crimes",
        "alcohol violation": "Statutory Crimes",
        "traffic offense": "Statutory Crimes",
        "white collar crime": "Statutory Crimes",
        "other crime": "Other Crimes",
        "juvenile crime": "Other Crimes",
        "weapon possession": "Other Crimes",
        "vandalism": "Other Crimes"
    }

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
