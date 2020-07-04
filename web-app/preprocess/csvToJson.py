# Script to convert csv file to json downloaded from data.police.uk

import csv
import json
import sys


def csvToJson(filename):
    CSV_FILE_PATH = filename + '.csv'
    JSON_FILE_PATH = filename + '.json'

    data = []
    with open(CSV_FILE_PATH) as csvFile:
        csvReader = csv.DictReader(csvFile)
        for rows in csvReader:
            entry = {
                'month': rows['Month'],
                'longitude': rows['Longitude'],
                'latitude': rows['Latitude'],
                'crimeType': rows['Crime type']
            }
            data.append(entry)

    with open(JSON_FILE_PATH, 'w') as jsonFile:
        jsonFile.write(json.dumps(data, indent=2))


if __name__ == "__main__":
    if (len(sys.argv) != 2):
        print("Incorrect number of arguments.")
        quit()
    csvToJson(sys.argv[1])
