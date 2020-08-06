import os
import json
from pathlib import Path

if __name__ == "__main__":
    JSON_FILE_PATH = "METADATA" + '.json'
    filenames = []
    for file in os.listdir("data"):
        if file.endswith(".csv"):
            relativePath = os.path.join("data/", file)
            filenames.append(Path(relativePath).stem)

    with open(JSON_FILE_PATH, 'w') as jsonFile:
        jsonFile.write(json.dumps(filenames, indent=2))