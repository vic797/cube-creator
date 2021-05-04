import json

def getDefinedIn(be, ee, je):
    out = []
    if be == "Yes":
        out.append("Bedrock Edition")
    if ee == "Yes":
        out.append("Education Edition")
    if je == "Yes":
        out.append("Java Edition")
    return ", ".join(out)

def readJoint():
    joint = open("mcfunction_joint.json", "r")
    result = json.loads(joint.read())
    joint.close()
    return result

def main():
    out = readJoint()
    data = open("mcfunction.txt", "r")
    lines = data.read().split("\n")
    data.close()
    for l in lines:
        if not l.startswith(";"):
            try:
                row = l.split("|")
                entry = {
                    "type": "function",
                    "name": row[0],
                    "description": row[1],
                    "defined_in": getDefinedIn(row[2], row[3], row[4])
                }
                out["completions"].append(entry)
            except:
                pass
    file = open("mcfunction_new.json", "w")
    result = json.dumps(out, indent=4)
    file.write(result)
    file.close()

if __name__ == "__main__":
    main()