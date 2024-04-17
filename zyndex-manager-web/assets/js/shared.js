let desktop = false;

document.addEventListener('contextmenu',function(e){
    e.preventDefault();
});

document.addEventListener('dragstart', function(e){
    e.preventDefault();
});

function sendRequest(request) {
    console.log("[BRIDGE] "+request)
}

function syncMenu() {
    const urlParams = new URLSearchParams(window.location.search);
    const menu = document.getElementById("menu");
    if(urlParams.get("m")!==null) {
        if(urlParams.get("m")==="true") {
            menu.classList.add("active");
        }
    }
}

function toggleMenu() {
    const menu = document.getElementById("menu");
    if(menu) {
        menu.classList.toggle("active");
    }
}

function setMode(mode) {
    if(mode.toLowerCase()==="desktop") {
        desktop = true;
        const main = document.getElementById("main");
        main.classList.add("active");
    } else {
        desktop = false;
        const main = document.getElementById("main");
        main.classList.remove("active");
    }
}

let message = true;
function toggleMessage() {
    if(message) {
        hideMessage();
    } else {
        showMessage();
    }
}

function showMessage() {
    message = true;
    document.getElementById("message").style.display = "inherit";
}

function hideMessage() {
    message = false;
    document.getElementById("message").style.display = "none";
}

function setMessage(title,message) {
    document.getElementById("message-title").innerText = title;
    document.getElementById("message-text").innerText = message;
}

function sync(url) {
    if(url.includes("?")) {
        window.location.href = url+"&m="+document.getElementById("menu").classList.contains("active");
    } else {
        window.location.href = url+"?m="+document.getElementById("menu").classList.contains("active");
    }
}

function openUrl(url) {
    if(desktop) {
        sendRequest("open.url."+url);
    } else {
        window.open(url, "_blank");
    }
}