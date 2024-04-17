function syncTab(tab) {
    const urlParams = new URLSearchParams(window.location.search);
    if(tab!==null) {
        if(tab!==undefined) {
            document.getElementById(tab).classList.add("selected");
            return;
        }
    }
    if(urlParams.get("t")!==null) {
        syncTab(urlParams.get("t"))
    }
}