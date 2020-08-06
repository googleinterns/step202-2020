# Script to convert csv file to json downloaded from data.police.uk
# Needs to be run once a month because new data set is released every month
# To Run: > python csvToJson.py <filename>
# From filename.csv file, store only relevant properties to filename.json

import csv
import json
import sys
import datetime
import os
from pathlib import Path


def csvToJson(path):
    CSV_FILE_PATH = path
    filename = Path(path).stem
    JSON_FILE_PATH = filename + '.json'

    data = []
    with open(CSV_FILE_PATH) as csvFile:
        csvReader = csv.DictReader(csvFile)
        for rows in csvReader:
            if (not rows['Longitude'] or not rows['Latitude']):
                continue

            date = rows['Month']
            year = int(date[:4])
            month = int(date[5:7])

            entry = {
                'timestamp': datetime.datetime(year, month, 1).timestamp(),
                'longitude': rows['Longitude'],
                'latitude': rows['Latitude'],
                'crimeType': rows['Crime type']
            }
            data.append(entry)

    with open(JSON_FILE_PATH, 'w') as jsonFile:
        jsonFile.write(json.dumps(data, indent=2))


if __name__ == "__main__":
    for file in os.listdir("data"):
        if file.endswith(".csv"):
            relativePath = os.path.join("data/", file)
            csvToJson(relativePath)
