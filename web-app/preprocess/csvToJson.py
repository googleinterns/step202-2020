import csv
import json

csvFilePath = '2020_05_london.csv'
jsonFilePath = '2020_05_london.json'

data = []
with open(csvFilePath) as csvFile:
    csvReader = csv.DictReader(csvFile)
    for rows in csvReader:
        entry = {}
        entry['Month'] = rows['Month']
        entry['Longitude'] = rows['Longitude']
        entry['Latitude'] = rows['Latitude']
        entry['Crime type'] = rows['Crime type']
        data.append(entry)

with open(jsonFilePath, 'w') as jsonFile:
    jsonFile.write(json.dumps(data, indent=2))
