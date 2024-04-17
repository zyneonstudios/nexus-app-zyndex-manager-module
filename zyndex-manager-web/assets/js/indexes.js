function addIndexToList(name,url,owner,instances) {
    const template = document.getElementById("index-template");
    const entry = template.cloneNode(true);
    entry.id=nameToId(name);
    const title = entry.querySelector("span");
    if(title) {
        title.innerText = name;
    }
    const button = entry.querySelector("a");
    if(button) {
        button.onclick = function () {
            //TODO edit logic
        }
    }
    const description = entry.querySelector("h3");
    if(description) {
        if(url===undefined||url===null) {
            url = ""
        } else {
            url = "<a class='link' onclick=\"openUrl('"+url+"')\">"+url+"</a><br>";
        }
        description.innerHTML = url+"by "+owner;
    }
    const instances_ = entry.querySelector("p");
    if(instances_) {
        instances_.innerText = instances;
    }
    template.parentNode.insertBefore(entry,template);
}

function nameToId(inputString) {
    return inputString.replaceAll(/[^a-z0-9]/ig, '').toLowerCase();
}

function creator() {
    document.getElementById("creator").style.display = "block";
    document.getElementById("start-creator").style.display = "none";
}

function downloadIndex() {
    const url = document.getElementById("url").value;
    if(url) {
        if(url.includes('http')) {
            sendRequest("index.download." + url);
            return;
        }
    }
    setMessage("Error: invalid url","You need to submit a valid url to download an index!");
    showMessage();
}

function preDef() {
    document.getElementById('creator').addEventListener('submit', function(event) {
        event.preventDefault();
        let name = document.getElementById("creator-name").value;
        let owner = document.getElementById("creator-owner").value;
        if(name) {
            name = name.replaceAll(".","%DOT%").replaceAll("\"","''");
            if(owner) {
                owner = owner.replaceAll(".","%DOT%").replaceAll("\"","''");
                sendRequest("index.create."+name+"."+owner);
                location.reload();
                return;
            }
        }
        setMessage("Error: invalid values","You need to submit a valid name and owner to create an index!");
    });
    document.getElementById("url").addEventListener('keydown', function(event) {
        if (event.keyCode === 13) {
            downloadIndex();
        }
    });
}